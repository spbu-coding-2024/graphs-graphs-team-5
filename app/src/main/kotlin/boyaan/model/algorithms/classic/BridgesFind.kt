package boyaan.model.algorithms.classic

import boyaan.model.core.base.Edge
import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex

class BridgesFind<V, E> {
    private var timer = 0

    fun bridgesFind(graph: Graph<V, E>): List<Edge<E>> {
        val bridges = mutableListOf<Edge<E>>()
        val visited = mutableListOf<Vertex<V>>()
        val tin = mutableMapOf<Vertex<V>, Int>()
        val low = mutableMapOf<Vertex<V>, Int>()

        fun dfs(
            u: Vertex<V>,
            parent: Vertex<V>?,
        ) {
            visited.add(u)
            tin[u] = timer
            low[u] = timer
            timer++

            for (v in neighbors(graph, u)) {
                if (v == parent) continue
                if (v !in visited) {
                    dfs(v, u)
                    low[u] = minOf(low[u]!!, low[v]!!)
                    if (low[v]!! > tin[u]!!) {
                        graph[u.key, v.key]?.let { bridges.add(it) }
                    }
                } else {
                    low[u] = minOf(low[u]!!, tin[v]!!)
                }
            }
        }

        for (v in graph.vertices) {
            if (v !in visited) dfs(v, null)
        }

        return bridges
    }

    private fun neighbors(
        graph: Graph<V, E>,
        vertex: Vertex<V>,
    ): List<Vertex<V>> =
        graph.edges.mapNotNull { edge ->
            when (vertex.key) {
                edge.key.first -> graph[edge.key.second]
                edge.key.second -> graph[edge.key.first]
                else -> null
            }
        }
}
