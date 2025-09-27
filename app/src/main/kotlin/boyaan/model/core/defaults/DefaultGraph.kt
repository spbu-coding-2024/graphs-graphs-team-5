package boyaan.model.core.defaults

import boyaan.model.core.base.Graph

public class DefaultGraph<V, E> : Graph<V, E> {
    private val _vertices: HashMap<V, DefaultVertex<V>> = hashMapOf<V, DefaultVertex<V>>()
    private val _edges: HashMap<E, DefaultEdge<V, E>> = hashMapOf<E, DefaultEdge<V, E>>()

    override val vertices: Collection<DefaultVertex<V>>
        get() = _vertices.values

    override val edges: Collection<DefaultEdge<V, E>>
        get() = _edges.values

    override fun addVertex(v: V): DefaultVertex<V> = _vertices.getOrPut(v) { DefaultVertex(v) }

    override fun addEdge(u: V, v: V, e: E): DefaultEdge<V, E> {
        val sourceVertex: DefaultVertex<V> = addVertex(u)
        val targetVertex: DefaultVertex<V> = addVertex(v)
        return _edges.getOrPut(e) { DefaultEdge(sourceVertex, targetVertex, e) }
    }
}