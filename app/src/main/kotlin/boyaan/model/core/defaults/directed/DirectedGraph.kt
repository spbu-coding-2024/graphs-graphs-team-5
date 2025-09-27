package boyaan.model.core.defaults.directed

import boyaan.model.core.base.Graph
import boyaan.model.core.defaults.DefaultVertex

public class DirectedGraph<V, E> : Graph<V, E> {
    private val _vertices: HashMap<V, DefaultVertex<V>> = hashMapOf<V, DefaultVertex<V>>()
    private val _edges: HashMap<E, DirectedEdge<V, E>> = hashMapOf<E, DirectedEdge<V, E>>()

    override val vertices: Collection<DefaultVertex<V>>
        get() = _vertices.values

    override val edges: Collection<DirectedEdge<V, E>>
        get() = _edges.values

    override fun addVertex(v: V): DefaultVertex<V> = _vertices.getOrPut(v) { DefaultVertex(v) }

    override fun addEdge(u: V, v: V, e: E): DirectedEdge<V, E> {
        val sourceVertex: DefaultVertex<V> = addVertex(u)
        val targetVertex: DefaultVertex<V> = addVertex(v)
        return _edges.getOrPut(e) { DirectedEdge(sourceVertex, targetVertex, e) }
    }
}