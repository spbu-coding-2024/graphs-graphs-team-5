package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.directed.DirectedGraph
import boyaan.model.core.internals.weighted.Weighted
import java.util.PriorityQueue

public class Dijkstra<V, E>(
    private val graph: Graph<V, E>,
) {
    data class Result(
        val distances: Map<Int, Double>,
        val previous: Map<Int, Int?>,
    )

    fun shortestPaths(start: Vertex<V>): Result {
        val distances = graph.vertices.associate { it.key to Double.POSITIVE_INFINITY }.toMutableMap()
        val previous = graph.vertices.associate { it.key to null as Int? }.toMutableMap()

        distances[start.key] = 0.0

        val pq = PriorityQueue<Pair<Double, Int>>(compareBy { it.first })
        pq.add(0.0 to start.key)

        while (pq.isNotEmpty()) {
            val (currentDist, currentKey) = pq.poll()
            if (currentDist > (distances[currentKey] ?: Double.POSITIVE_INFINITY)) continue

            for ((neighborKey, weight) in getNeighbors(currentKey)) {
                val currentDistance = distances[currentKey] ?: continue
                val neighborDistance = distances[neighborKey] ?: continue
                val newDist = currentDistance + weight
                if (newDist < neighborDistance) {
                    distances[neighborKey] = newDist
                    previous[neighborKey] = currentKey
                    pq.add(newDist to neighborKey)
                }
            }
        }

        return Result(distances, previous)
    }

    private fun getNeighbors(vertexKey: Int): List<Pair<Int, Double>> {
        return if (graph is DirectedGraph) {
            graph.edges.mapNotNull { edge ->
                val (from, to) = edge.key
                if (from != vertexKey) return@mapNotNull null
                val weight = if (edge is Weighted) edge.weight else 1.0
                to to weight
            }
        } else {
            graph.edges.mapNotNull { edge ->
                val (from, to) = edge.key
                val weight = if (edge is Weighted) edge.weight else 1.0
                when (vertexKey) {
                    from -> to to weight
                    to -> from to weight
                    else -> null
                }
            }
        }
    }

    fun reconstructPath(
        previous: Map<Int, Int?>,
        targetKey: Int,
    ): List<Int> {
        val path = mutableListOf<Int>()
        var current: Int? = targetKey
        while (current != null) {
            path.add(current)
            current = previous[current]
        }
        return path.reversed()
    }
}
