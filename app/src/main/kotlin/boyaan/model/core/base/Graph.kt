package boyaan.model.core.base

public interface Graph<V, E> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<E>>

    fun addVertex(v: V): Vertex<V>
    fun addEdge(uKey: Int, vKey: Int, e: E): Edge<E>

    operator fun get(key: Int): Vertex<V>?
    operator fun get(uKey: Int, vKey: Int): Edge<E>?

    fun removeVertex(key: Int): Vertex<V>?
    fun removeEdge(uKey: Int, vKey: Int): Edge<E>?
}
