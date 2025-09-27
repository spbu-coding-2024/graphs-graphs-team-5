package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Edge

internal class DirectedEdge<E>(override val key: Pair<Int, Int>, override var value: E) : Edge<E>, Directed
