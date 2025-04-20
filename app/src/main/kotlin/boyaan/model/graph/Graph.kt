package boyaan.model.graph

import boyaan.model.link.Link
import boyaan.model.node.Node

class Graph<V> {
    private val nodesById: MutableMap<Int, Node<V>> = mutableMapOf()
    private val linksByIds: MutableMap<Pair<Int, Int>, Link> = mutableMapOf()
    private val adjacentNodes: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    fun addNode(id: Int, node: Node<V>) {
        nodesById.getOrPut(id) { node }
    }

    fun removeNode(id: Int) {
        // TODO
    }

    fun addLink(
        sourceNodeId: Int,
        targetNodeId: Int,
        weight: Double = 0.0,
        state: Link.State = Link.State.Undirected,
    ) {
        adjacentNodes.getOrPut(sourceNodeId) { mutableSetOf() }.add(targetNodeId)
        adjacentNodes.getOrPut(targetNodeId) { mutableSetOf() }.add(sourceNodeId)
        linksByIds[Pair(sourceNodeId, targetNodeId)] = Link(sourceNodeId, targetNodeId, weight, state)
    }

    fun addLinksAll(vararg nodeIds: Int, weight: Double = 0.0) {
        for (nodeId in nodeIds) {
            for (sourceNodeId in 0..nodeIds.lastIndex) {
                for (targetNodeId in (sourceNodeId + 1)..nodeIds.lastIndex) {
                    addLink(nodeIds[sourceNodeId], nodeIds[targetNodeId], weight, Link.State.Undirected)
                }
            }
        }
    }

    fun removeLink(sourceNodeId: Int, targetNodeId: Int) {
        // TODO
    }

    operator fun get(id: Int): Node<V>? {
        return nodesById[id]
    }

    operator fun set(id: Int, node: Node<V>) {
        nodesById[id] = node
    }
}
