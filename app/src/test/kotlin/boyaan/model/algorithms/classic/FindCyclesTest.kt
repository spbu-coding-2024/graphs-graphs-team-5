package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.defaults.DefaultVertex
import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FindCyclesTest {
    data class TestCase(
        val description: String,
        val graph: Graph<Any?, Any?>,
        val startVertexKey: Int,
        val expectedCycles: List<List<Int>>,
    )

    private fun extendedTestCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Undirected square",
                graph =
                    DefaultGraph<String, String>().apply {
                        val v = (0..3).map { addVertex("V$it") }
                        addEdge(v[0].key, v[1].key, "E01")
                        addEdge(v[1].key, v[2].key, "E12")
                        addEdge(v[2].key, v[3].key, "E23")
                        addEdge(v[3].key, v[0].key, "E30")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2, 3)),
            ),
            TestCase(
                description = "Undirected pentagon",
                graph =
                    DefaultGraph<String, String>().apply {
                        val v = (0..4).map { addVertex("V$it") }
                        for (i in 0..4) addEdge(v[i].key, v[(i + 1) % 5].key, "E$i")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2, 3, 4)),
            ),
            TestCase(
                description = "Directed graph with multiple cycles",
                graph =
                    DirectedUnweightedGraph<String, String>().apply {
                        val v = (0..3).map { addVertex("V$it") }
                        addEdge(v[0].key, v[1].key, "E01")
                        addEdge(v[1].key, v[2].key, "E12")
                        addEdge(v[2].key, v[0].key, "E20")
                        addEdge(v[1].key, v[3].key, "E13")
                        addEdge(v[3].key, v[1].key, "E31")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2)),
            ),
            TestCase(
                description = "Graph with chord (partial paths should not count)",
                graph =
                    DefaultGraph<String, String>().apply {
                        val v = (0..3).map { addVertex("V$it") }
                        addEdge(v[0].key, v[1].key, "E01")
                        addEdge(v[1].key, v[2].key, "E12")
                        addEdge(v[2].key, v[3].key, "E23")
                        addEdge(v[3].key, v[0].key, "E30")
                        addEdge(v[0].key, v[2].key, "E02")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2, 3), listOf(0, 2, 3)),
            ),
            TestCase(
                description = "Self-loop",
                graph =
                    DefaultGraph<String, String>().apply {
                        val v0 = addVertex("A")
                        addEdge(v0.key, v0.key, "Loop")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = emptyList(),
            ),
        )

    private fun baseTestCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Undirected triangle",
                graph =
                    DefaultGraph<String, String>().apply {
                        val v1 = addVertex("A")
                        val v2 = addVertex("B")
                        val v3 = addVertex("C")
                        addEdge(v1.key, v2.key, "AB")
                        addEdge(v2.key, v3.key, "BC")
                        addEdge(v3.key, v1.key, "CA")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2)),
            ),
            TestCase(
                description = "Directed triangle",
                graph =
                    DirectedUnweightedGraph<String, String>().apply {
                        val v1 = addVertex("A")
                        val v2 = addVertex("B")
                        val v3 = addVertex("C")
                        addEdge(v1.key, v2.key, "AB")
                        addEdge(v2.key, v3.key, "BC")
                        addEdge(v3.key, v1.key, "CA")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2)),
            ),
            TestCase(
                description = "Directed line, no cycles",
                graph =
                    DirectedUnweightedGraph<String, String>().apply {
                        val v1 = addVertex("A")
                        val v2 = addVertex("B")
                        val v3 = addVertex("C")
                        addEdge(v1.key, v2.key, "AB")
                        addEdge(v2.key, v3.key, "BC")
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = emptyList(),
            ),
            TestCase(
                description = "Directed weighted square",
                graph =
                    DirectedWeightedGraph<String, String>().apply {
                        val v1 = addVertex("A")
                        val v2 = addVertex("B")
                        val v3 = addVertex("C")
                        val v4 = addVertex("D")
                        addEdge(v1.key, v2.key, "AB", 1.0)
                        addEdge(v2.key, v3.key, "BC", 2.0)
                        addEdge(v3.key, v4.key, "CD", 3.0)
                        addEdge(v4.key, v1.key, "DA", 4.0)
                    } as Graph<Any?, Any?>,
                startVertexKey = 0,
                expectedCycles = listOf(listOf(0, 1, 2, 3)),
            ),
        )

    fun allTestCases(): List<TestCase> = baseTestCases() + extendedTestCases()

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("allTestCases")
    fun `test find cycles`(testCase: TestCase) {
        val startVertex: Vertex<Any?> =
            testCase.graph[testCase.startVertexKey]
                ?: error("Vertex not found")
        val algorithm = FindCycles(testCase.graph)
        val cycles = algorithm.findCycles(startVertex as DefaultVertex<Any?>)

        val expectedSet = testCase.expectedCycles.map { it.toSet() }.toSet()
        val actualSet = cycles.map { it.toSet() }.toSet()

        assertEquals(expectedSet, actualSet, "Failed: ${testCase.description}")
    }
}
