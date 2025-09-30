package boyaan.model.core.internals.weighted

import boyaan.model.core.internals.defaults.DefaultEdge
import boyaan.model.core.internals.defaults.DefaultVertex

internal class UndirectedWeightedGraph<V, E> : WeightedGraph<V, E> {
    private val _vertices: HashMap<Int, DefaultVertex<V>> = hashMapOf<Int, DefaultVertex<V>>()
    private val _edges: HashMap<Pair<Int, Int>, WeightedEdge<E>> = hashMapOf<Pair<Int, Int>, WeightedEdge<E>>()
    private var nextKey: Int = 0

    override val vertices: Collection<DefaultVertex<V>>
        get() = _vertices.values

    override val edges: Collection<WeightedEdge<E>>
        get() = _edges.values

    override fun addVertex(v: V): DefaultVertex<V> {
        val vertex: DefaultVertex<V> = DefaultVertex(nextKey, v)
        _vertices[nextKey++] = vertex
        return vertex
    }

    override fun addEdge(
        uKey: Int,
        vKey: Int,
        e: E,
    ): WeightedEdge<E>? = addEdge(uKey, vKey, e, weight = 1.0)

    override fun addEdge(
        uKey: Int,
        vKey: Int,
        e: E,
        weight: Double,
    ): WeightedEdge<E>? =
        if (uKey == vKey) {
            null
        } else {
            _vertices[uKey]?.let {
                _vertices[vKey]?.let {
                    _edges[uKey to vKey] ?: _edges.getOrPut(vKey to uKey) { WeightedEdge(uKey to vKey, e, weight) }
                }
            }
        }

    override operator fun get(key: Int): DefaultVertex<V>? = _vertices[key]

    override operator fun get(
        uKey: Int,
        vKey: Int,
    ): WeightedEdge<E>? = _edges[uKey to vKey] ?: _edges[vKey to uKey]

    override fun removeVertex(key: Int): DefaultVertex<V>? =
        _vertices.remove(key)?.also {
            _edges
                .keys
                .filter { (uKey, vKey) ->
                    uKey == key || vKey == key
                }.forEach { (uKey, vKey) ->
                    removeEdge(uKey, vKey)
                }
        }

    override fun removeEdge(
        uKey: Int,
        vKey: Int,
    ): WeightedEdge<E>? = _edges.remove(uKey to vKey) ?: _edges.remove(vKey to uKey)
}
