package boyaan.model.algorithms.modern

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import kotlin.math.max

class VoteRank<V, E> {
    fun run(
        graph: Graph<V, E>,
        topK: Int = Int.MAX_VALUE,
    ): List<Vertex<V>> {
        val result = mutableListOf<Vertex<V>>()
        val scores = graph.vertices.associateWith { degree(graph, it).toDouble() }.toMutableMap()

        while (scores.values.any { it > 0.0 } && result.size < topK) {
            val candidate = scores.filter { it.value > 0.0 }.maxByOrNull { it.value }?.key ?: break
            result.add(candidate)
            scores[candidate] = 0.0

            val degCandidate = degree(graph, candidate).toDouble().coerceAtLeast(1.0)
            neighbors(graph, candidate).forEach { neighbor ->
                scores[neighbor] = max(0.0, (scores[neighbor] ?: 0.0) - 1.0 / degCandidate)
            }
        }

        if (result.isEmpty() && graph.vertices.isNotEmpty()) {
            result.add(graph.vertices.first())
        }
        return result
    }

    private fun degree(
        graph: Graph<V, E>,
        vertex: Vertex<V>,
    ): Int =
        graph.edges.count { edge ->
            edge.key.first == vertex.key || edge.key.second == vertex.key
        }

    private fun neighbors(
        graph: Graph<V, E>,
        vertex: Vertex<V>,
    ): List<Vertex<V>> =
        graph.edges.mapNotNull { edge ->
            if (edge.key.first == vertex.key) {
                graph[edge.key.second]
            } else if (edge.key.second == vertex.key) {
                graph[edge.key.first]
            } else {
                null
            }
        }
}
