package boyaan.model.algorithms.modern

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.defaults.DefaultGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val expectedCandidates: List<Set<String>> = emptyList(),
    )

    fun testCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Empty graph",
                graph = DefaultGraph(),
                topK = 3,
                expectedSize = 0,
            ),
            TestCase(
                description = "Single vertex",
                graph =
                    DefaultGraph<String, String>().apply {
                        addVertex("A")
                    },
                topK = 3,
                expectedSize = 1,
                expectedCandidates = listOf(setOf("A")),
            ),
            TestCase(
                description = "Star graph, center first",
                graph =
                    DefaultGraph<String, String>().apply {
                        val c = addVertex("C")
                        val l1 = addVertex("L1")
                        val l2 = addVertex("L2")
                        val l3 = addVertex("L3")
                        addEdge(c.key, l1.key, "")
                        addEdge(c.key, l2.key, "")
                        addEdge(c.key, l3.key, "")
                    },
                topK = 3,
                expectedSize = 3,
                expectedCandidates =
                    listOf(
                        setOf("C"),
                        setOf("L1", "L2", "L3"),
                        setOf("L1", "L2", "L3"),
                    ),
            ),
            TestCase(
                description = "Line graph A-B-C-D",
                graph =
                    DefaultGraph<String, String>().apply {
                        val a = addVertex("A")
                        val b = addVertex("B")
                        val c = addVertex("C")
                        val d = addVertex("D")
                        addEdge(a.key, b.key, "")
                        addEdge(b.key, c.key, "")
                        addEdge(c.key, d.key, "")
                    },
                topK = 3,
                expectedSize = 3,
                expectedCandidates =
                    listOf(
                        setOf("B", "C"),
                        setOf("A", "D"),
                        setOf("A", "D"),
                    ),
            ),
            TestCase(
                description = "Complete graph of 4 vertices",
                graph =
                    DefaultGraph<String, String>().apply {
                        val vertices = listOf(addVertex("A"), addVertex("B"), addVertex("C"), addVertex("D"))
                        for (i in vertices.indices) {
                            for (j in i + 1 until vertices.size) {
                                addEdge(vertices[i].key, vertices[j].key, "")
                            }
                        }
                    },
                topK = 4,
                expectedSize = 4,
                expectedCandidates =
                    listOf(
                        setOf("A", "B", "C", "D"),
                        setOf("A", "B", "C", "D"),
                        setOf("A", "B", "C", "D"),
                        setOf("A", "B", "C", "D"),
                    ),
            ),
        )

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("testCases")
    fun `test VoteRank`(testCase: TestCase) {
        val result: List<Vertex<String>> = VoteRank<String, String>().run(testCase.graph)

        assertEquals(testCase.expectedSize, result.size, "Wrong size for ${testCase.description}")

        for ((index, candidates) in testCase.expectedCandidates.withIndex()) {
            if (result.size > index && candidates.isNotEmpty()) {
                assertTrue(
                    result[index].value in candidates,
                    "Vertex at position $index unexpected for ${testCase.description}",
                )
            }
        }

        val graphVertices =
            testCase.graph.vertices
                .map { it.value }
                .toSet()
        assertTrue(
            result.all { it.value in graphVertices },
            "Some vertices in result are not present in the graph for ${testCase.description}",
        )
    }
}
