package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DijkstraTest {
    data class TestCase(
        val description: String,
        val graph: Graph<Int, String>,
        val startVertexKey: Int,
        val expectedDistances: Map<Int, Double>,
        val expectedPath: Pair<Int, List<Int>>? = null,
    )

    fun testCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Undirected unweighted triangle",
                graph =
                    DefaultGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        addEdge(v0.key, v1.key, "01")
                        addEdge(v1.key, v2.key, "12")
                        addEdge(v0.key, v2.key, "02")
                    },
                startVertexKey = 0,
                expectedDistances = mapOf(0 to 0.0, 1 to 1.0, 2 to 1.0),
                expectedPath = 2 to listOf(0, 2),
            ),
            TestCase(
                description = "Directed unweighted line",
                graph =
                    DirectedUnweightedGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        addEdge(v0.key, v1.key, "01")
                        addEdge(v1.key, v2.key, "12")
                    },
                startVertexKey = 0,
                expectedDistances = mapOf(0 to 0.0, 1 to 1.0, 2 to 2.0),
                expectedPath = 2 to listOf(0, 1, 2),
            ),
            TestCase(
                description = "Directed weighted diamond",
                graph =
                    DirectedWeightedGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        val v3 = addVertex(3)
                        addEdge(v0.key, v1.key, "01", 1.0)
                        addEdge(v0.key, v2.key, "02", 2.0)
                        addEdge(v1.key, v3.key, "13", 5.0)
                        addEdge(v2.key, v3.key, "23", 1.0)
                    },
                startVertexKey = 0,
                expectedDistances = mapOf(0 to 0.0, 1 to 1.0, 2 to 2.0, 3 to 3.0),
                expectedPath = 3 to listOf(0, 2, 3),
            ),
            TestCase(
                description = "Undirected graph with unreachable vertex",
                graph =
                    DefaultGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        addEdge(v0.key, v1.key, "01")
                    },
                startVertexKey = 0,
                expectedDistances =
                    mapOf(
                        0 to 0.0,
                        1 to 1.0,
                        2 to Double.POSITIVE_INFINITY,
                    ),
                expectedPath = 2 to listOf(2),
            ),
        )

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("testCases")
    fun `test Dijkstra shortest paths`(testCase: TestCase) {
        val startVertex: Vertex<Int> =
            testCase.graph[testCase.startVertexKey] ?: error("Start vertex not found")
        val dijkstra = Dijkstra(testCase.graph)
        val result = dijkstra.shortestPaths(startVertex)

        testCase.expectedDistances.forEach { (key, value) ->
            assertEquals(value, result.distances[key], "Failed distances for ${testCase.description}")
        }

        testCase.expectedPath?.let { (target, expectedPath) ->
            val path = dijkstra.reconstructPath(result.previous, target)
            assertEquals(expectedPath, path, "Failed path for ${testCase.description}")
        }
    }
}
