package boyaan.model.core.internals.directedWeighted

import boyaan.model.core.base.Edge
import boyaan.model.core.internals.directed.Directed
import boyaan.model.core.internals.weighted.Weighted

internal data class DirectedWeightedEdge<E>(
    override val key: Pair<Int, Int>,
    override var value: E,
    override var weight: Double,
) : Edge<E>,
    Directed,
    Weighted
