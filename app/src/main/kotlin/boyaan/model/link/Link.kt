package boyaan.model.link

import boyaan.model.node.Node

abstract class Link<N : Node<V>, V>(
    val source: N,
    val target: N,
)
