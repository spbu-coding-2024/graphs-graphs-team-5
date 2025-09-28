package boyaan.model.core.defaults

import boyaan.model.core.base.Graph

internal class DefaultGraph<V, E> : Graph<V, E> {
    private val _vertices: HashMap<Int, DefaultVertex<V>> = hashMapOf<Int, DefaultVertex<V>>()
    private val _edges: HashMap<Pair<Int, Int>, DefaultEdge<E>> = hashMapOf<Pair<Int, Int>, DefaultEdge<E>>()
    private var _nextKey: Int = 0

    override val vertices: Collection<DefaultVertex<V>>
        get() = _vertices.values

    override val edges: Collection<DefaultEdge<E>>
        get() = _edges.values

    override fun addVertex(v: V): DefaultVertex<V> {
        val vertex: DefaultVertex<V> = DefaultVertex(_nextKey, v)
        _vertices[_nextKey++] = vertex
        return vertex
    }

    override fun addEdge(
        uKey: Int,
        vKey: Int,
        e: E,
    ): DefaultEdge<E> =
        _edges[uKey to vKey]
            ?: _edges
                .getOrPut(vKey to uKey) {
                    DefaultEdge(uKey to vKey, e)
                }

    override operator fun get(key: Int): DefaultVertex<V>? = _vertices[key]

    override operator fun get(
        uKey: Int,
        vKey: Int,
    ): DefaultEdge<E>? = _edges[uKey to vKey] ?: _edges[vKey to uKey]

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
    ): DefaultEdge<E>? = _edges.remove(uKey to vKey) ?: _edges.remove(vKey to uKey)
}
