package boyaan.model.core.internals.defaults

import boyaan.model.core.base.Vertex

internal data class DefaultVertex<V>(
    override val key: Int,
    override var value: V,
) : Vertex<V>
