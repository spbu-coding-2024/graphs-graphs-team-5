package boyaan.model.save

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.defaults.DefaultGraph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
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
        batchSize: Int = 500
    ) {
        driver.session().use { session ->
            session.executeWrite { tx ->
                tx.run("CREATE CONSTRAINT IF NOT EXIST FOR (v:Vertex) REQUIRE v.index IS UNIQUE")
            }

            if (clearDatabase) {
                session.executeWrite { tx ->
                    tx.run("MATCH (n) DETACH DELETE n")
                }
            }

            val vertexList = graph.vertices.toList()
            val originalKeyToIndex = vertexList.mapIndexed { index, vertex -> vertex.key to index }.toMap()

            val nodesPayload = vertexList.mapIndexed { index, vertex ->
                mapOf("index" to index, "value" to vertex.value.toString())
            }

            val edgesPayload = graph.edges.map { edge ->
                val leftKey = edge.key.first
                val rightKey = edge.key.second
                val leftIndex = originalKeyToIndex[leftKey] ?: throw IllegalArgumentException("Vertex key missing")
                val rightIndex = originalKeyToIndex[rightKey] ?: throw IllegalArgumentException("Vertex key missing")
                val (minIndex, maxIndex) = if (leftIndex <= rightIndex) leftIndex to rightIndex
                else rightIndex to leftIndex

                mapOf("uIndex" to minIndex, "vIndex" to maxIndex, "value" to edge.value.toString())
            }

            nodesPayload.chunked(batchSize).forEach { batch ->
                session.executeWrite { tx ->
                    tx.run(
                        """
                            UNWIND $${"nodes"} AS nodeEntry
                            MERGE (v:Vertex {origId: nodeEntry.index})
                            SET v.value = nodeEntry.value
                        """.trimIndent(),
                        mapOf("nodes" to batch)
                    )
                }
            }

            edgesPayload.chunked(batchSize).forEach { batch ->
                session.executeWrite { tx ->
                    tx.run(
                        """
                            UNWIND $${"edges"} AS edgeEntry
                            MATCH (a:Vertex {index: edgeEntry.uIndex}), (b:Vertex {index: edgeEntry.vIndex})
                            MERGE (a)-[r:EDGE {value: edgeEntry.value}]->(b)
                        """.trimIndent(),
                        mapOf("edges" to batch)
                    )
                }
            }
        }
    }

    fun <V, E> importGraph(
        valueParser: (String) -> V,
        edgeParser: (String) -> E,
    ): Graph<V, E> {
        val resultGraph = DefaultGraph<V, E>()

        driver.session().use { session ->
            val vertexRecords =
                session.executeRead { tx ->
                    tx.run("MATCH (v:Vertex) RETURN v.index AS idx, v.value AS value ORDER BY v.index ASC").list()
                }

            val indexToVertex = mutableMapOf<Int, Vertex<V>>()
            for((pos, record) in vertexRecords.withIndex()) {
                val indexValue = if(!record["idx"].isNull) record["idx"].asInt() else pos
                val valueString = if (record["value"].isNull) "" else record["value"].asString()
                val parsedValue = valueParser(valueString)
                val createdVertex = resultGraph.addVertex(parsedValue)
                indexToVertex[indexValue] = createdVertex
            }

            val edgeRecords = session.executeRead { tx ->
                tx.run(
                    "MATCH (a:Vertex)-[r:EDGE]->(b:Vertex) RETURN a.index AS uIndex, b.index AS vIndex, r.value AS value"
                ).list()
            }

            for (record in edgeRecords) {
                if(record["uIndex"].isNull || record["vIndex"].isNull) continue
                val uIndex = record["uIndex"].asInt()
                val vIndex = record["vIndex"].asInt()
                val edgeValueString = if (record["value"].isNull) "" else record["value"].asString()
                val parsedEdgeValue = edgeParser(edgeValueString)

                val fromVertex = indexToVertex[uIndex]
                val toVertex = indexToVertex[vIndex]
                if (fromVertex != null && toVertex != null) {
                    resultGraph.addEdge(fromVertex.key, toVertex.key, parsedEdgeValue)
                }
            }
        }

        return resultGraph
    }

    override fun close() {
        driver.close()
    }
}
