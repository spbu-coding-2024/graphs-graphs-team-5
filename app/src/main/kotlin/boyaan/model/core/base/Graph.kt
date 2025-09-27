package boyaan.model.core.base

interface Graph<V, E> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<E>>

    fun addVertex(v: V): Vertex<V>
    fun addEdge(u: Vertex<V>, v: Vertex<V>, e: E): Edge<E>
}
