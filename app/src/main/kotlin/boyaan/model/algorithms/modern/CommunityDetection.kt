package boyaan.model.algorithms.modern

import boyaan.model.algorithms.Algorithm
import boyaan.model.core.base.Edge
import boyaan.model.core.base.Graph
import boyaan.model.core.internals.weighted.Weighted
import org.jetbrains.research.ictl.louvain.Link
import org.jetbrains.research.ictl.louvain.computeModularity

internal typealias Partition = Map<Int, Int>
internal typealias Communities = MutableMap<Int, MutableList<Int>>

internal class CommunityDetection<V, E>(
    graph: Graph<V, E>,
) : Algorithm<V, E>(graph) {
    private class InternalLink(
        val source: Int,
        val target: Int,
        val weight: Double,
    ) : Link {
        override fun source(): Int = source

        override fun target(): Int = target

        override fun weight(): Double = weight
    }

    private fun linkBuilder(edge: Edge<E>): InternalLink =
        InternalLink(
            edge.key.first,
            edge.key.second,
            if (edge is Weighted) edge.weight else 1.0,
        )

    private val links: List<InternalLink>
        get() = graph.edges.map { linkBuilder(it) }

    fun getPartition(depth: Int = 0): Partition =
        org.jetbrains.research.ictl.louvain
            .getPartition(links, depth)

    fun partitionToCommunities(partition: Partition): Communities {
        val communities = mutableMapOf<Int, MutableList<Int>>()
        partition.forEach { (key, community) ->
            communities.getOrPut(community) { mutableListOf() }.add(key)
        }
        return communities
    }

    fun getCommunities(depth: Int = 0): Communities =
        partitionToCommunities(
            org.jetbrains.research.ictl.louvain
                .getPartition(links, depth),
        )

    fun computeModularity(partition: Partition): Double = computeModularity(links, partition)
}
