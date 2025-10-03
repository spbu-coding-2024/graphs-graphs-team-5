package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import boyaan.model.core.internals.weighted.UndirectedWeightedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DijkstraTest {
    data class TestCase(
        val description: String,
        val graph: Graph<Int, String>,
        val startVertexKey: Int,
        val targetVertexKey: Int,
        val expectedDistance: Double?,
        val expectedPath: List<Int>?,
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
                targetVertexKey = 2,
                expectedDistance = 1.0,
                expectedPath = listOf(0, 2),
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
                targetVertexKey = 2,
                expectedDistance = 2.0,
                expectedPath = listOf(0, 1, 2),
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
                targetVertexKey = 3,
                expectedDistance = 3.0,
                expectedPath = listOf(0, 2, 3),
            ),
            TestCase(
                description = "Undirected graph with unreachable vertex",
                graph =
                    DefaultGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        addVertex(2)
                        addEdge(v0.key, v1.key, "01")
                    },
                startVertexKey = 0,
                targetVertexKey = 2,
                expectedDistance = null,
                expectedPath = null,
            ),
            TestCase(
                description = "Graph with negative edges",
                graph =
                    UndirectedWeightedGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        addEdge(v0.key, v1.key, "01", -2.0)
                        addEdge(v0.key, v2.key, "12", 3.0)
                        addEdge(v1.key, v2.key, "02", -5.0)
                    },
                startVertexKey = 0,
                targetVertexKey = 2,
                expectedDistance = null,
                expectedPath = null,
            ),
        )

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("testCases")
    fun `test Dijkstra shortest path`(testCase: TestCase) {
        val startVertex: Vertex<Int>? = testCase.graph[testCase.startVertexKey]
        val targetVertex: Vertex<Int>? = testCase.graph[testCase.targetVertexKey]

        if (startVertex == null || targetVertex == null) {
            assertNull(testCase.expectedDistance, "Expected no path for ${testCase.description}")
            assertNull(testCase.expectedPath, "Expected no path for ${testCase.description}")
            return
        }

        val dijkstra = Dijkstra(testCase.graph)
        val result = dijkstra.shortestPath(startVertex, targetVertex)

        if (testCase.expectedDistance == null) {
            assertNull(result, "Expected no path for ${testCase.description}")
        } else {
            assertNotNull(result, "Expected path for ${testCase.description}")
            result?.let {
                assertEquals(testCase.expectedDistance, it.distance, "Wrong distance for ${testCase.description}")
                assertEquals(testCase.expectedPath, it.path, "Wrong path for ${testCase.description}")
            }
        }
    }
}
