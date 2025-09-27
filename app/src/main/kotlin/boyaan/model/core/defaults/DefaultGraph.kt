package boyaan.model.core.defaults

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex

public class DefaultGraph<V, E> : Graph<V, E> {
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

    override fun addEdge(u: Vertex<V>, v: Vertex<V>, e: E): DefaultEdge<E> =
        _edges
            .get(u.key to v.key)
            ?: _edges
                .getOrPut(v.key to u.key) {
                    DefaultEdge(u.key to v.key, e)
                }

    operator fun get(key: Int): DefaultVertex<V>? = _vertices[key]
    operator fun get(uKey: Int, vKey: Int): DefaultEdge<E>? = _edges[uKey to vKey] ?: _edges[vKey to uKey]
}
