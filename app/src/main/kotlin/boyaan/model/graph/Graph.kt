package boyaan.model.graph

import boyaan.model.link.Link
import boyaan.model.node.Node

class Graph<V> {
    private val nodesById: MutableMap<Int, Node<V>> = mutableMapOf()
    private val linksByIds: MutableMap<Pair<Int, Int>, MutableList<Link>> = mutableMapOf()
    private val adjacentNodesById: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    fun addNode(id: Int, node: Node<V>) {
        nodesById.getOrPut(id) { node }
    }

    fun removeNode(id: Int): Node<V>? {
        adjacentNodesById[id]?.forEach {
            adjacentNodesById[it]?.remove(id)
            linksByIds.remove(Pair(id, it))
            linksByIds.remove(Pair(it, id))
        }
        adjacentNodesById.remove(id)
        return nodesById.remove(id)
    }

    fun addLink(
        sourceNodeId: Int,
        targetNodeId: Int,
        weight: Double = 0.0,
        state: Link.State = Link.State.Undirected,
    ) {
        adjacentNodesById.getOrPut(sourceNodeId) { mutableSetOf() }.add(targetNodeId)
        adjacentNodesById.getOrPut(targetNodeId) { mutableSetOf() }.add(sourceNodeId)
        linksByIds.getOrPut(Pair(sourceNodeId, targetNodeId)) { mutableListOf() }
            .add(Link(sourceNodeId, targetNodeId, weight, state))
    }

    fun addLinksAll(vararg nodeIds: Int, weight: Double = 0.0, state: Link.State = Link.State.Undirected) {
        for (nodeId in nodeIds) {
            for (sourceNodeId in 0..nodeIds.lastIndex) {
                for (targetNodeId in (sourceNodeId + 1)..nodeIds.lastIndex) {
                    addLink(nodeIds[sourceNodeId], nodeIds[targetNodeId], weight, Link.State.Undirected)
                }
            }
        }
    }

    fun removeLink(sourceNodeId: Int, targetNodeId: Int) {

    }

    operator fun get(id: Int): Node<V>? {
        return nodesById[id]
    }

    operator fun set(id: Int, node: Node<V>) {
        nodesById[id] = node
    }
}
