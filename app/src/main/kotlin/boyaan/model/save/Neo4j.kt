package boyaan.model.save

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.defaults.DefaultGraph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Record
import org.neo4j.driver.Values
import org.neo4j.driver.Value
import java.io.Closeable

class Neo4j(
    uri: String,
    user: String,
    password: String,
) : Closeable {
    private val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))

    fun <V, E> exportGraph(
        graph: Graph<V, E>,
        clearDatabase: Boolean = false,
    ) {
        driver.session().use { session ->
            if (clearDatabase) {
                session.executeWrite { tx ->
                    tx.run("MATCH (n) DETACH DELETE n")
                }
            }
            graph.vertices.forEach { vertex ->
                session.executeWrite { tx ->
                    tx.run(
                        "CREATE (v:Vertex {origId \$origId, value \$value})",
                        mapOf("origId" to vertex.key, "value" to vertex.value.toString()),
                    )
                }
            }

            graph.edges.forEach { edge ->
                val (u, v) = edge.key
                val (minKey, maxKey) = listOf(u, v).sorted()
                session.executeWrite { tx ->
                    tx.run(
                        """
                        MATCH (a:Vertex {origId: $${"minKey"}}), (b:Vertex {origId: $${"maxKey"}})
                        CREATE (a)-[:EDGE {value: $${"value"}}]->(b)
                        """.trimIndent(),
                        mapOf("minKey" to minKey, "maxKey" to maxKey, "value" to edge.value.toString()),
                    )
                }
            }
        }
    }

    fun <V, E> importGraph(
        valueParser: (String) -> V,
        edgeParser: (String) -> E,
    ): Graph<V, E> {
        val resultGraph: DefaultGraph<V, E> = DefaultGraph()

        driver.session().use { session ->
            val vertexRecords =
                session.executeRead { tx ->
                    tx
                        .run("MATCH (v:Vertex) RETURN v.origId AS origId, v.value AS value")
                        .list { record ->
                            record["origId"].asInt() to valueParser(record["value"].asString(""))
                        }
                }

            val vertexMap = mutableMapOf<Int, Vertex<V>>()
            vertexRecords.forEach { (origId, value) ->
                val vertex = resultGraph.addVertex(value)
                vertexMap[origId] = vertex
            }

            val edgeRecords =
                session.executeRead { tx ->
                    tx
                        .run("MATCH (a:Vertex)-[e:Edge]->(b:Vertex) RETURN a.origId AS uKey, b.origId AS vKey, e.value AS value")
                        .list { record ->
                            Triple(
                                record["uKey"].asInt(),
                                record ["vKey"].asInt(),
                                edgeParser(record["value"].asString("")),
                            )
                        }
                }

            edgeRecords.forEach { (uOrig, vOrig, value) ->
                val (minKey, maxKey) = listOf(uOrig, vOrig).sorted()
                val uVertex = vertexMap[minKey]
                val vVertex = vertexMap[maxKey]
                if (uVertex != null && vVertex != null) {
                    resultGraph.addEdge(uVertex.key, vVertex.key, value)
                }
            }
        }

        return resultGraph
    }

    override fun close() {
        driver.close()
    }
}
