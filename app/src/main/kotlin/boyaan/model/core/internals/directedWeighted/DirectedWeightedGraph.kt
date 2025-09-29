package boyaan.model.core.internals.directedWeighted

import boyaan.model.core.internals.defaults.DefaultVertex
import boyaan.model.core.internals.directed.DirectedGraph
import boyaan.model.core.internals.weighted.WeightedGraph

internal class DirectedWeightedGraph<V, E> :
    DirectedGraph<V, E>,
    WeightedGraph<V, E> {
    private val _vertices: HashMap<Int, DefaultVertex<V>> = hashMapOf<Int, DefaultVertex<V>>()
    private val _edges: HashMap<Pair<Int, Int>, DirectedWeightedEdge<E>> =
        hashMapOf<Pair<Int, Int>, DirectedWeightedEdge<E>>()
    private var nextKey: Int = 0

    override val vertices: Collection<DefaultVertex<V>>
        get() = _vertices.values

    override val edges: Collection<DirectedWeightedEdge<E>>
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
    ): DirectedWeightedEdge<E> = addEdge(uKey, vKey, e, weight = 1.0)

    override fun addEdge(
        uKey: Int,
        vKey: Int,
        e: E,
        weight: Double,
    ): DirectedWeightedEdge<E> = _edges.getOrPut(uKey to vKey) { DirectedWeightedEdge(uKey to vKey, e, weight) }

    override operator fun get(key: Int): DefaultVertex<V>? = _vertices[key]

    override operator fun get(
        uKey: Int,
        vKey: Int,
    ): DirectedWeightedEdge<E>? = _edges[uKey to vKey] ?: _edges[vKey to uKey]

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
    ): DirectedWeightedEdge<E>? = _edges.remove(uKey to vKey)
}
