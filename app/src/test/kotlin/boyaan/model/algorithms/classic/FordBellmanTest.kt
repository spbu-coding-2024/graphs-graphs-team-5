package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FordBellmanTest {

    data class TestCase(
        val description: String,
        val graph: Graph<Int, String>,
        val startKey: Int,
        val expectedDistances: Map<Int, Double?>,
        val expectedNegativeCycle: Boolean
    )

    fun testCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Simple directed line",
                graph = DirectedWeightedGraph<Int, String>().apply {
                    val v0 = addVertex(0)
                    val v1 = addVertex(1)
                    val v2 = addVertex(2)
                    addEdge(v0.key, v1.key, "01", 1.0)
                    addEdge(v1.key, v2.key, "12", 2.0)
                },
                startKey = 0,
                expectedDistances = mapOf(0 to 0.0, 1 to 1.0, 2 to 3.0),
                expectedNegativeCycle = false
            ),
            TestCase(
                description = "Graph with negative edge but no cycle",
                graph = DirectedWeightedGraph<Int, String>().apply {
                    val v0 = addVertex(0)
                    val v1 = addVertex(1)
                    val v2 = addVertex(2)
                    addEdge(v0.key, v1.key, "01", 4.0)
                    addEdge(v0.key, v2.key, "02", 5.0)
                    addEdge(v1.key, v2.key, "12", -2.0)
                },
                startKey = 0,
                expectedDistances = mapOf(0 to 0.0, 1 to 4.0, 2 to 2.0),
                expectedNegativeCycle = false
            ),
            TestCase(
                description = "Unreachable vertex",
                graph = DirectedWeightedGraph<Int, String>().apply {
                    val v0 = addVertex(0)
                    val v1 = addVertex(1)
                    addVertex(2)
                    addEdge(v0.key, v1.key, "01", 1.0)
                },
                startKey = 0,
                expectedDistances = mapOf(0 to 0.0, 1 to 1.0, 2 to null),
                expectedNegativeCycle = false
            ),
            TestCase(
                description = "Graph with negative cycle",
                graph = DirectedWeightedGraph<Int, String>().apply {
                    val v0 = addVertex(0)
                    val v1 = addVertex(1)
                    addEdge(v0.key, v1.key, "01", 1.0)
                    addEdge(v1.key, v0.key, "10", -2.0)
                },
                startKey = 0,
                expectedDistances = emptyMap(), // не проверяем конкретные значения
                expectedNegativeCycle = true
            )
        )

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("testCases")
    fun `test Ford-Bellman`(testCase: TestCase) {
        val startVertex: Vertex<Int>? = testCase.graph[testCase.startKey]
        assertNotNull(startVertex, "Start vertex must exist")

        val fordBellman = FordBellman<Int, String>()
        val result = fordBellman.run(testCase.graph, startVertex!!)

        assertEquals(testCase.expectedNegativeCycle, result.negativeCycle, testCase.description)

        if (!testCase.expectedNegativeCycle) {
            testCase.expectedDistances.forEach { (key, expected) ->
                val v = testCase.graph[key]
                assertNotNull(v, "Vertex $key should exist in ${testCase.description}")
                val dist = result.distances[v!!]
                if (expected == null) {
                    assertEquals(Double.POSITIVE_INFINITY, dist, "Vertex $key should be unreachable in ${testCase.description}")
                } else {
                    assertEquals(expected, dist, "Wrong distance to vertex $key in ${testCase.description}")
                }
            }
        }
    }
}
