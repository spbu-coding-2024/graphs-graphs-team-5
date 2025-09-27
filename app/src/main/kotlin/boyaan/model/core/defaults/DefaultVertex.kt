package boyaan.model.core.defaults

import boyaan.model.core.base.Vertex

data class DefaultVertex<V>(override val key: Int, override var value: V) : Vertex<V>
