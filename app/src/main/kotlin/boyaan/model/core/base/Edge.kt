package boyaan.model.core.base

public interface Edge<E> {
    val key: Pair<Int, Int>
    var value: E
}
