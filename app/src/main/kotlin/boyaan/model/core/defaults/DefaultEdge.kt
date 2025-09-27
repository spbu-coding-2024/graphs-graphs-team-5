package boyaan.model.core.defaults

import boyaan.model.core.base.Edge

data class DefaultEdge<V, E>(override val u: DefaultVertex<V>, override val v: DefaultVertex<V>, override var data: E) :
    Edge<V, E>