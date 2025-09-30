package boyaan.model.algorithms.modern

import boyaan.model.core.base.Graph
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import boyaan.model.core.internals.weighted.UndirectedWeightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VoteRankTest {

    data class TestCase(
        val description: String,
        val graph: Graph<String, String>,
        val topK: Int,
        val expectedSize: Int,
        val expectedCandidates: List<List<String>>,
    )

    fun testCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Single vertex",
                graph = DefaultGraph<String, String>().apply { addVertex("A") },
                topK = 3,
                expectedSize = 1,
                expectedCandidates = listOf(listOf("A"))
            ),
            TestCase(
                description = "Star graph, center first",
                graph = DefaultGraph<String, String>().apply {
                    val c = addVertex("C")
                    val l1 = addVertex("L1")
                    val l2 = addVertex("L2")
                    val l3 = addVertex("L3")
                    addEdge(c.key, l1.key, "C-L1")
                    addEdge(c.key, l2.key, "C-L2")
                    addEdge(c.key, l3.key, "C-L3")
                },
                topK = 3,
                expectedSize = 3,
                expectedCandidates = listOf(listOf("C"), listOf("L1", "L2", "L3"), listOf("L1", "L2", "L3"))
            ),
            TestCase(
                description = "Line graph A-B-C-D",
                graph = DefaultGraph<String, String>().apply {
                    val a = addVertex("A")
                    val b = addVertex("B")
                    val c = addVertex("C")
                    val d = addVertex("D")
                    addEdge(a.key, b.key, "A-B")
                    addEdge(b.key, c.key, "B-C")
                    addEdge(c.key, d.key, "C-D")
                },
                topK = 3,
                expectedSize = 3,
                expectedCandidates = listOf(listOf("B", "C"), listOf("A", "D"), listOf("A", "D"))
            ),
            TestCase(
                description = "Undirected weighted triangle",
                graph = UndirectedWeightedGraph<String, String>().apply {
                    val a = addVertex("A")
                    val b = addVertex("B")
                    val c = addVertex("C")
                    addEdge(a.key, b.key, "A-B", 2.0)
                    addEdge(b.key, c.key, "B-C", 3.0)
                    addEdge(a.key, c.key, "A-C", 1.0)
                },
                topK = 3,
                expectedSize = 3,
                expectedCandidates = listOf(listOf("B"), listOf("A", "C"), listOf("A", "C"))
            ),
            TestCase(
                description = "Directed unweighted triangle",
                graph = DirectedUnweightedGraph<String, String>().apply {
                    val a = addVertex("A")
                    val b = addVertex("B")
                    val c = addVertex("C")
                    addEdge(a.key, b.key, "A-B")
                    addEdge(b.key, c.key, "B-C")
                    addEdge(c.key, a.key, "C-A")
                },
                topK = 3,
                expectedSize = 3,
                expectedCandidates = listOf(listOf("A", "B", "C"), listOf("A", "B", "C"), listOf("A", "B", "C"))
            ),
            TestCase(
                description = "Directed weighted diamond",
                graph = DirectedWeightedGraph<String, String>().apply {
                    val a = addVertex("A")
                    val b = addVertex("B")
                    val c = addVertex("C")
                    val d = addVertex("D")
                    addEdge(a.key, b.key, "A-B", 1.0)
                    addEdge(a.key, c.key, "A-C", 2.0)
                    addEdge(b.key, d.key, "B-D", 5.0)
                    addEdge(c.key, d.key, "C-D", 1.0)
                },
                topK = 3,
                expectedSize = 3,
                expectedCandidates = listOf(listOf("A"), listOf("B", "C"), listOf("B", "C"))
            )
        )

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("testCases")
    fun `test VoteRank`(testCase: TestCase) {
        val voteRank = VoteRank<String, String>()
        val result = voteRank.run(testCase.graph, testCase.topK).map { it.value }

        assertEquals(testCase.expectedSize, result.size)
        testCase.expectedCandidates.forEach { candidates ->
            require(candidates.any { it in result })
        }
    }
}
