package boyaan.model.core.internals.weighted

import boyaan.model.core.base.Edge

internal data class WeightedEdge<E>(
    override val key: Pair<Int, Int>,
    override var value: E,
    override var weight: Double,
) : Edge<E>,
    Weighted
