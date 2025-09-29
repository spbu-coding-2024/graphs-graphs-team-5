package boyaan.model.algorithms.classic

import boyaan.model.algorithms.Algorithm
import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex

internal class FindCycles<V, E>(
    private val graph: Graph<V, E>,
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

            for (edge in graph.edges) {
                val neighborKey =
                    when (current.key) {
                        edge.key.first -> edge.key.second
                        edge.key.second -> edge.key.first
                        else -> continue
                    }

                if (parent != null && neighborKey == parent.key) continue

                if (neighborKey in stack) {
                    val cycleStartIndex = stack.indexOf(neighborKey)
                    val cycle = stack.subList(cycleStartIndex, stack.size).toList()
                    if (cycle.size > 1) {
                        cycles.add(canonicalCycle(cycle))
                    }
                } else if (neighborKey !in visited) {
                    val neighborVertex = graph[neighborKey] ?: continue
                    dfs(neighborVertex, current)
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
