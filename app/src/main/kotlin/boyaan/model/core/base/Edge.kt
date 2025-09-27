package boyaan.model.core.base

interface Edge<V, E> {
    val u: Vertex<V>
    val v: Vertex<V>
    var data: E
}