package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.weighted.Weighted

data class FordBellmanResult<V>(
    val distances: Map<Vertex<V>, Double>,
    val predecessors: Map<Vertex<V>, Vertex<V>?>,
    val negativeCycle: Boolean,
)

class FordBellman<V, E> {
    fun run(
        graph: Graph<V, E>,
        start: Vertex<V>,
    ): FordBellmanResult<V> {
        val vertexDistances = graph.vertices.associateWith { Double.POSITIVE_INFINITY }.toMutableMap()
        val vertexPredecessors = graph.vertices.associateWith { null as Vertex<V>? }.toMutableMap()

        vertexDistances[start] = 0.0

        val weightedEdgesAsEdges = graph.edges.filter { it is Weighted }

        val numberOfVertices = graph.vertices.size
        if (numberOfVertices <= 1) {
            return FordBellmanResult(vertexDistances, vertexPredecessors, negativeCycle = false)
        }

        repeat(numberOfVertices - 1) {
            for (edge in weightedEdgesAsEdges) {
                val edgeKey = edge.key
                val sourceVertex = graph[edgeKey.first] ?: continue
                val targetVertex = graph[edgeKey.second] ?: continue

                val edgeWeight = (edge as Weighted).weight

                val newDistance = vertexDistances[sourceVertex]!! + edgeWeight
                if (newDistance < vertexDistances[targetVertex]!!) {
                    vertexDistances[targetVertex] = newDistance
                    vertexPredecessors[targetVertex] = sourceVertex
                }
            }
        }

        var containsNegativeCycle = false
        for (edge in weightedEdgesAsEdges) {
            val edgeKey = edge.key
            val sourceVertex = graph[edgeKey.first] ?: continue
            val targetVertex = graph[edgeKey.second] ?: continue
            val edgeWeight = (edge as Weighted).weight

            if (vertexDistances[sourceVertex]!! + edgeWeight < vertexDistances[targetVertex]!!) {
                containsNegativeCycle = true
            }
        }

        return FordBellmanResult(
            distances = vertexDistances,
            predecessors = vertexPredecessors,
            negativeCycle = containsNegativeCycle,
        )
    }
}
