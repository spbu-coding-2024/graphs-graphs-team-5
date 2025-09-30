package boyaan.model.algorithms.classic

import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

internal typealias DUGraph = DirectedUnweightedGraph<String, String>

@TestInstance(PER_CLASS)
internal class StronglyConnectedCommunitiesDetectionTest {
    @Test
    fun `empty graph`() {
        val graph = DUGraph()
        val algorithm = StronglyConnectedCommunitiesDetection(graph)
        val partition = algorithm.getPartition()
        assertEquals(0, partition.size)
    }

    @Test
    fun `graph with no edges`() {
        val graph = DUGraph()
        for (value in 'a'..'g') {
            graph.addVertex(value.toString())
        }
        val algorithm = StronglyConnectedCommunitiesDetection(graph)
        val partition = algorithm.getPartition()
        assertEquals(0, partition.size)
    }

    @Test
    fun `simple test #1`() {
        val graph = DUGraph()
        with(graph) {
            for (key in 0..7) {
                addVertex(key.toString())
            }
            addEdge(0, 1, "01")
            addEdge(1, 2, "12")
            addEdge(2, 0, "20")
            addEdge(2, 3, "23")
            addEdge(3, 4, "34")
            addEdge(4, 5, "45")
            addEdge(5, 6, "56")
            addEdge(6, 7, "67")
            addEdge(4, 7, "47")
            addEdge(6, 4, "64")
        }
        val algorithm = StronglyConnectedCommunitiesDetection(graph)
        val partition = algorithm.getPartition()
        assertEquals(
            mutableListOf(
                mutableListOf(0, 2, 1),
                mutableListOf(3),
                mutableListOf(4, 6, 5),
                mutableListOf(7),
            ),
            partition,
        )
    }

    @Test
    fun `simple test #2`() {
        val graph = DUGraph()
        with(graph) {
            for (value in 'a'..'j') {
                graph.addVertex(value.toString())
            }
            addEdge(0, 3, "ad")
            addEdge(0, 1, "ab")
            addEdge(1, 2, "bc")
            addEdge(2, 0, "ca")
            addEdge(3, 8, "di")
            addEdge(4, 3, "ed")
            addEdge(4, 5, "ef")
            addEdge(5, 6, "fg")
            addEdge(6, 7, "gh")
            addEdge(7, 4, "he")
            addEdge(8, 9, "ij")
            addEdge(9, 8, "ji")
        }
        val algorithm = StronglyConnectedCommunitiesDetection(graph)
        val partition = algorithm.getPartition()
        assertEquals(
            mutableListOf(
                mutableListOf(4, 7, 6, 5),
                mutableListOf(0, 2, 1),
                mutableListOf(3),
                mutableListOf(8, 9),
            ),
            partition,
        )
    }
}
