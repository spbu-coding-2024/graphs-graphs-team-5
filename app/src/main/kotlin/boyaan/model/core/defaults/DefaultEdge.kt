package boyaan.model.core.defaults

import boyaan.model.core.base.Edge

data class DefaultEdge<E>(override val key: Pair<Int, Int>, override var value: E) : Edge<E>
