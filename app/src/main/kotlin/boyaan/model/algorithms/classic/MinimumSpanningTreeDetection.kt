package boyaan.model.algorithms.classic

import boyaan.model.algorithms.Algorithm
import boyaan.model.core.internals.weighted.UndirectedWeightedGraph
import boyaan.model.core.internals.weighted.WeightedEdge

internal class MinimumSpanningTreeDetection<V, E>(
    graph: UndirectedWeightedGraph<V, E>,
) : Algorithm<V, E>(graph) {
    private class DSU(
        verticesSize: Int,
    ) {
        private val parent = IntArray(verticesSize) { it }
        private val rank = IntArray(verticesSize) { 1 }

        fun find(i: Int): Int {
            if (parent[i] != i) {
                parent[i] = find(parent[i])
            }
            return parent[i]
        }

        fun union(
            x: Int,
            y: Int,
        ) {
            val rootX = find(x)
            val rootY = find(y)

            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX
                } else {
                    parent[rootY] = rootX
                    ++rank[rootX]
                }
            }
        }
    }

    fun getTree(): Collection<WeightedEdge<E>> {
        @Suppress("UNCHECKED_CAST")
        val sortedEdges = (graph.edges as Collection<WeightedEdge<E>>).sortedBy { it.weight }

        val dsu = DSU(graph.vertices.size)
        val mstEdges = mutableListOf<WeightedEdge<E>>()
        var edgesCounter = 0

        for (edge in sortedEdges) {
            val (uKey, vKey) = edge.key

            if (dsu.find(uKey) != dsu.find(vKey)) {
                dsu.union(uKey, vKey)
                mstEdges.add(edge)
                ++edgesCounter

                if (edgesCounter == graph.vertices.size - 1) {
                    break
                }
            }
        }
        return mstEdges
    }
}
