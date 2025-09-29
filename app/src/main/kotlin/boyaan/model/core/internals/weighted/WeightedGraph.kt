package boyaan.model.core.internals.weighted

import boyaan.model.core.base.Graph

internal interface WeightedGraph<V, E> : Graph<V, E> {
    fun addEdge(
        uKey: Int,
        vKey: Int,
        e: E,
        weight: Double,
    ): Weighted
}
