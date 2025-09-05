package boyaan.model.core.base

abstract class Graph<
        V : Vertex<*>,
        E : Edge<V, *>,
        > {

    abstract val vertices: Collection<V>
    abstract val edges: Collection<E>

    abstract fun <D : Any?> addVertex(data: D): Vertex<D>
    abstract fun <D : Any?> addEdge(
        firstVertex: V,
        secondVertex: V,
        data: D,
    ): Edge<V, D>

    abstract fun removeVertex(vertex: V): Boolean
    abstract fun removeEdge(edge: E): Boolean
}