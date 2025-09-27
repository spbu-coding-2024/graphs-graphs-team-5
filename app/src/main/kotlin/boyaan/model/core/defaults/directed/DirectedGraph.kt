package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Graph
import boyaan.model.core.defaults.DefaultVertex

internal class DirectedGraph<V, E> : Graph<V, E> {
    private val _vertices: HashMap<Int, DefaultVertex<V>> = hashMapOf<Int, DefaultVertex<V>>()
    private val _edges: HashMap<Pair<Int, Int>, DirectedEdge<E>> = hashMapOf<Pair<Int, Int>, DirectedEdge<E>>()
    private var _nextKey: Int = 0

    override val vertices: Collection<DefaultVertex<V>>
        get() = _vertices.values

    override val edges: Collection<DirectedEdge<E>>
        get() = _edges.values

    override fun addVertex(v: V): DefaultVertex<V> {
        val vertex: DefaultVertex<V> = DefaultVertex(_nextKey, v)
        _vertices[_nextKey++] = vertex
        return vertex
    }

    override fun addEdge(uKey: Int, vKey: Int, e: E): DirectedEdge<E> =
        _edges.getOrPut(uKey to vKey) { DirectedEdge(uKey to vKey, e) }

    override operator fun get(key: Int): DefaultVertex<V>? = _vertices[key]
    override operator fun get(uKey: Int, vKey: Int): DirectedEdge<E>? = _edges[uKey to vKey]

    override fun removeVertex(key: Int): DefaultVertex<V>? =
        _vertices.remove(key)?.also {
            _edges
                .keys
                .filter { (uKey, vKey) ->
                    uKey == key || vKey == key
                }
                .forEach { (uKey, vKey) ->
                    removeEdge(uKey, vKey)
                }
        }

    override fun removeEdge(uKey: Int, vKey: Int): DirectedEdge<E>? =
        _edges.remove(uKey to vKey)
}
