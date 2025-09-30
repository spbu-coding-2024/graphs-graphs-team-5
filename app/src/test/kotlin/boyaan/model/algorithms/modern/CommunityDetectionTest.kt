package boyaan.model.algorithms.modern

import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import boyaan.model.core.internals.weighted.UndirectedWeightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

internal typealias SimpleGraph = DefaultGraph<String, String>
internal typealias UWGraph = UndirectedWeightedGraph<String, String>
internal typealias DUGraph = DirectedWeightedGraph<String, String>
internal typealias DWGraph = DirectedWeightedGraph<String, String>

@TestInstance(PER_CLASS)
internal class CommunityDetectionTest {
    @Test
    fun `empty graph returns empty partition`() {
        val graph = SimpleGraph()
        val algorithm = CommunityDetection(graph)
        val partition = algorithm.getPartition()
        assertTrue(partition.isEmpty())
    }

    @Test
    fun `complete graph forms one community`() {
        val graph = SimpleGraph()
        graph.addVerticesAll("1", "2", "3", "4", "5")
        for (i in 1..5) {
            for (j in (i + 1)..5) {
                graph.addEdge(i - 1, j - 1, "$i to $j")
            }
        }
        println(graph.edges.size)

        val algorithm = CommunityDetection(graph)
        val communities = algorithm.getCommunities()
        assertEquals(1, communities.size)
    }

    @Test
    fun `two complete graphs are detected as separate communities`() {
        val graph = DUGraph()
        graph.addVerticesAll("a", "b", "c", "d", "e", "f")

        graph.addEdge(0, 1, "ab")
        graph.addEdge(1, 2, "bc")
        graph.addEdge(2, 0, "ca")

        graph.addEdge(3, 4, "de")
        graph.addEdge(4, 5, "ef")
        graph.addEdge(5, 3, "fd")

        val algorithm = CommunityDetection(graph)

        val communities = algorithm.getCommunities()
        assertEquals(2, communities.size)
        assertEquals(communities[1]?.size, communities[0]?.size)
        assertNotEquals(communities[1], communities[0])
    }

    @Test
    fun `weighted edges affect partitioning`() {
        val distributedWeightGraph = UWGraph()
        with(distributedWeightGraph) {
            addVerticesAll("a", "b", "c", "d")
            addEdge(0, 1, "ab")
            addEdge(1, 2, "bc")
            addEdge(2, 3, "cd")
            addEdge(3, 0, "ad")
            addEdge(0, 2, "ac")
        }

        val notDistributedWeightGraph = UWGraph()
        with(notDistributedWeightGraph) {
            addVerticesAll("a", "b", "c", "d")
            addEdge(0, 1, "ab", 100.1)
            addEdge(1, 2, "bc", 0.002)
            addEdge(2, 3, "cd", 75.73)
            addEdge(0, 3, "ad", 0.0034)
            addEdge(0, 2, "ac", 0.0012)
        }

        val dwgAlgorithm = CommunityDetection(distributedWeightGraph)
        val dwgCommunities = dwgAlgorithm.getCommunities()

        assertEquals(1, dwgCommunities.size)
        assertEquals(4, dwgCommunities[0]?.size)

        val notDwgAlgorithm = CommunityDetection(notDistributedWeightGraph)
        val notDwgCommunities = notDwgAlgorithm.getCommunities()

        assertEquals(2, notDwgCommunities.size)
        assertEquals(2, notDwgCommunities[0]?.size)
        assertEquals(2, notDwgCommunities[1]?.size)
    }

    @Test
    fun `modularity computing`() {
        val graph = DWGraph()
        with(graph) {
            addVerticesAll("a", "b", "c", "d")
            addEdge(0, 1, "ab")
            addEdge(2, 3, "cd")
        }
        val algorithm = CommunityDetection(graph)
        val partition = mapOf(0 to 0, 1 to 0, 2 to 1, 3 to 1)
        val modularity = algorithm.computeModularity(partition)
        assertEquals(0.5, modularity)
    }
}
