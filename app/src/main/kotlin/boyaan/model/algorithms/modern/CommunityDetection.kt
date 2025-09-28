package boyaan.model.algorithms.modern

import boyaan.model.algorithms.Algorithm
import boyaan.model.core.base.Edge
import boyaan.model.core.defaults.weighted.Weighted
import org.jetbrains.research.ictl.louvain.Link
import org.jetbrains.research.ictl.louvain.computeModularity

typealias Partition = Map<Int, Int>

internal class CommunityDetection<V, E>(
    val graph: boyaan.model.core.base.Graph<V, E>,
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

    val Partition.modularity: Double
        get() = computeModularity(links, this)
}
