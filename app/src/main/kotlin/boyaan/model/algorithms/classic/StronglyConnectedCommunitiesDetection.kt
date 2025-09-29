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

            adjacencyMap[key]?.let {
                for (adjacentKey in it) {
                    visited[adjacentKey]?.apply {
                        if (!this) {
                            firstDFS(adjacentKey, visited, order)
                        }
                    }
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
            transposedAdjacencyMap[key]?.let {
                for (adjacentKey in it) {
                    visited[adjacentKey]?.apply {
                        if (!this) {
                            secondDFS(adjacentKey, visited, stronglyConnectedCommunity)
                        }
                    }
                }
            }
        }

        val visited =
            graph.vertices
                .associate {
                    it.key to false
                }.toMutableMap()
        val order = Order()
        adjacencyMap.forEach { (key, _) ->
            visited[key]?.apply {
                if (!this) {
                    firstDFS(key, visited, order)
                }
            }
        }
        visited.replaceAll { key, value ->
            false
        }
        val stronglyConnectedCommunities = mutableListOf<MutableList<Int>>()
        while (!order.isEmpty()) {
            val key = order.removeLast()
            visited[key]?.apply {
                if (!this) {
                    val stronglyConnectedCommunity = mutableListOf<Int>()
                    secondDFS(key, visited, stronglyConnectedCommunity)
                    stronglyConnectedCommunities.add(stronglyConnectedCommunity)
                }
            }
        }
        return stronglyConnectedCommunities
    }
}
