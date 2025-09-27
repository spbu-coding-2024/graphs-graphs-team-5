package boyaan.model.core.base

interface Edge<E> {
    val key: Pair<Int, Int>
    var value: E
}
