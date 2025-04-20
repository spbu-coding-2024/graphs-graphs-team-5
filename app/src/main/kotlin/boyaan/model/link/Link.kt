package boyaan.model.link

import boyaan.model.node.Node

class Link(
    val sourceNodeId: Int,
    val targetNodeId: Int,
    var weight: Double = 0.0,
    val state: State = State.Undirected
) {
    enum class State {
        Directed,
        Undirected,
    }
}
