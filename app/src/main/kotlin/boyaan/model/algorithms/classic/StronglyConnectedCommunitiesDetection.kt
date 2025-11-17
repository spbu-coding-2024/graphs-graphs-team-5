package boyaan.model.algorithms.classic

import boyaan.model.algorithms.Algorithm
import boyaan.model.core.internals.directed.DirectedGraph

internal typealias Order = ArrayDeque<Int>
internal typealias AdjacencyMap = MutableMap<Int, MutableList<Int>>

internal class StronglyConnectedCommunitiesDetection<V, E>(
    graph: DirectedGraph<V, E>,
) : Algorithm<V, E>(graph) {
    private val adjacencyMap: AdjacencyMap
        get() =
            mutableMapOf<Int, MutableList<Int>>()
                .apply {
                    graph
                        .edges
                        .map { it.key }
                        .forEach { (uKey, vKey) ->
                            this
                                .getOrPut(uKey) { mutableListOf() }
                                .add(vKey)
                        }
                }

    private val transposedAdjacencyMap: AdjacencyMap
        get() =
            mutableMapOf<Int, MutableList<Int>>()
                .apply {
                    graph
                        .edges
                        .map { it.key }
                        .forEach { (uKey, vKey) ->
                            this
                                .getOrPut(vKey) { mutableListOf() }
                                .add(uKey)
                        }
                }

    fun getPartition(): MutableList<MutableList<Int>> {
        fun firstDFS(
            key: Int,
            visited: MutableMap<Int, Boolean>,
            order: Order,
        ) {
            visited[key] = true

            adjacencyMap[key]?.forEach {
                if (!visited.getOrDefault(it, false)) {
                    firstDFS(it, visited, order)
                }
            }

            order.add(key)
        }

        fun secondDFS(
            key: Int,
            visited: MutableMap<Int, Boolean>,
            stronglyConnectedCommunity: MutableList<Int>,
        ) {
            visited[key] = true

            stronglyConnectedCommunity.add(key)

            transposedAdjacencyMap[key]?.forEach {
                if (!visited.getOrDefault(it, false)) {
                    secondDFS(it, visited, stronglyConnectedCommunity)
                }
            }
        }

        val visited = mutableMapOf<Int, Boolean>()
        val order = Order()
        adjacencyMap.forEach { (key, _) ->
            if (!visited.getOrDefault(key, false)) {
                firstDFS(key, visited, order)
            }
        }
        visited.clear()
        val stronglyConnectedCommunities = mutableListOf<MutableList<Int>>()
        while (!order.isEmpty()) {
            val key = order.removeLast()
            if (!visited.getOrDefault(key, false)) {
                val stronglyConnectedCommunity = mutableListOf<Int>()
                secondDFS(key, visited, stronglyConnectedCommunity)
                stronglyConnectedCommunities.add(stronglyConnectedCommunity)
            }
        }
        return stronglyConnectedCommunities
    }
}
