package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Edge

internal data class DirectedEdge<E>(
    override val key: Pair<Int, Int>,
    override var value: E,
) : Edge<E>,
    Directed
