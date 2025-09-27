package boyaan.model.core.defaults

import boyaan.model.core.base.Vertex

data class DefaultVertex<V>(override var data: V) : Vertex<V>