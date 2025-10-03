package boyaan.model.algorithms.classic

import boyaan.model.core.internals.weighted.UndirectedWeightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

internal typealias UWGraph = UndirectedWeightedGraph<String, String>

@TestInstance(PER_CLASS)
internal class MinimumSpanningTreeDetectionTest {
    @Test
    fun `empty graph`() {
        val graph = UWGraph()
        val algorithm = MinimumSpanningTreeDetection(graph)
        val tree = algorithm.getTree()
        assertEquals(0, tree.size)
    }

    @Test
    fun `graph with no edges`() {
        val graph = UWGraph()
        graph.addVerticesAll("a", "b", "c", "d", "e", "f")

        val algorithm = MinimumSpanningTreeDetection(graph)
        val tree = algorithm.getTree()
        assertEquals(0, tree.size)
    }

    @Test
    fun `simple test #1`() {
        val graph = UWGraph()
        with(graph) {
            addVerticesAll("0", "1", "2", "3")
            addEdge(0, 1, "01", 10.0)
            addEdge(1, 3, "13", 15.0)
            addEdge(2, 3, "23", 4.0)
            addEdge(2, 0, "20", 6.0)
            addEdge(0, 3, "03", 5.0)
        }

        val algorithm = MinimumSpanningTreeDetection(graph)
        val tree = algorithm.getTree()
        assertEquals(3, tree.size)
        assertEquals(19.0, tree.sumOf { it.weight })
        assert(tree.map { it.key }.containsAll(listOf(2 to 3, 0 to 3, 0 to 1)))
    }

    @Test
    fun `simple test #2`() {
        val graph = UWGraph()
        with(graph) {
            addVerticesAll("a", "b", "c", "d", "e", "f", "g")
            addEdge(0, 1, "ab", 7.0)
            addEdge(1, 2, "bc", 8.0)
            addEdge(0, 3, "ad", 5.0)
            addEdge(1, 3, "bd", 9.0)
            addEdge(1, 4, "be", 7.0)
            addEdge(2, 4, "ce", 5.0)
            addEdge(3, 4, "de", 15.0)
            addEdge(3, 5, "df", 6.0)
            addEdge(4, 5, "ef", 8.0)
            addEdge(4, 6, "eg", 9.0)
            addEdge(5, 6, "fg", 11.0)
        }

        val algorithm = MinimumSpanningTreeDetection(graph)
        val tree = algorithm.getTree()
        assertEquals(6, tree.size)
        assertEquals(39.0, tree.sumOf { it.weight })
        assert(tree.map { it.key }.containsAll(listOf(0 to 1, 0 to 3, 1 to 4, 2 to 4, 3 to 5, 4 to 6)))
    }
}
