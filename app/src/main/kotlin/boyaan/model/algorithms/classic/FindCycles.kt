package boyaan.model.algorithms.classic

import boyaan.model.algorithms.Algorithm
import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.directed.DirectedGraph

internal class FindCycles<V, E>(
    graph: Graph<V, E>,
) : Algorithm<V, E>(graph) {
    fun findCycles(start: Vertex<V>): List<List<Int>> {
        val visited = mutableSetOf<Int>()
        val stack = mutableListOf<Int>()
        val cycles = mutableListOf<List<Int>>()

        fun dfs(
            current: Vertex<V>,
            parent: Vertex<V>?,
        ) {
            stack.add(current.key)
            visited.add(current.key)

            val neighbors: List<Vertex<V>> =
                if (graph is DirectedGraph) {
                    graph.edges
                        .filter { it.key.first == current.key }
                        .mapNotNull { graph[it.key.second] }
                } else {
                    graph.edges.flatMap { edge ->
                        when (current.key) {
                            edge.key.first -> listOfNotNull(graph[edge.key.second])
                            edge.key.second -> listOfNotNull(graph[edge.key.first])
                            else -> emptyList()
                        }
                    }
                }

            for (neighbor in neighbors) {
                if (parent != null && neighbor.key == parent.key) continue

                if (neighbor.key in stack) {
                    val cycleStartIndex = stack.indexOf(neighbor.key)
                    val cycle = stack.subList(cycleStartIndex, stack.size).toList()
                    if (cycle.size > 1) cycles.add(canonicalCycle(cycle))
                } else if (neighbor.key !in visited) {
                    dfs(neighbor, current)
                }
            }

            stack.removeAt(stack.size - 1)
        }

        dfs(start, null)

        return cycles.map { canonicalCycle(it) }.distinct()
    }

    private fun canonicalCycle(cycle: List<Int>): List<Int> {
        val n = cycle.size
        val rotations = (0 until n).map { i -> cycle.drop(i) + cycle.take(i) }
        val reversedRotations = rotations.map { it.reversed() }
        return (rotations + reversedRotations).minWithOrNull { a, b ->
            for (i in 0 until n) {
                val cmp = a[i].compareTo(b[i])
                if (cmp != 0) return@minWithOrNull cmp
            }
            0
        } ?: emptyList()
    }
}
