package boyaan.model.algorithms.classic

import boyaan.model.core.base.Graph
import boyaan.model.core.internals.defaults.DefaultGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BridgesFindTest {
    data class TestCase(
        val description: String,
        val graph: Graph<Int, String>,
        val expectedBridges: Set<Set<Int>>,
    )

    fun testCases(): List<TestCase> =
        listOf(
            TestCase(
                description = "Empty graph",
                graph = DefaultGraph(),
                expectedBridges = emptySet(),
            ),
            TestCase(
                description = "Single vertex",
                graph = DefaultGraph<Int, String>().apply { addVertex(0) },
                expectedBridges = emptySet(),
            ),
            TestCase(
                description = "Line graph 0-1-2-3",
                graph =
                    DefaultGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        val v3 = addVertex(3)
                        addEdge(v0.key, v1.key, "01")
                        addEdge(v1.key, v2.key, "12")
                        addEdge(v2.key, v3.key, "23")
                    },
                expectedBridges = setOf(setOf(0, 1), setOf(1, 2), setOf(2, 3)),
            ),
            TestCase(
                description = "Triangle 0-1-2",
                graph =
                    DefaultGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        addEdge(v0.key, v1.key, "01")
                        addEdge(v1.key, v2.key, "12")
                        addEdge(v2.key, v0.key, "20")
                    },
                expectedBridges = emptySet(),
            ),
            TestCase(
                description = "House graph",
                graph =
                    DefaultGraph<Int, String>().apply {
                        val v0 = addVertex(0)
                        val v1 = addVertex(1)
                        val v2 = addVertex(2)
                        val v3 = addVertex(3)
                        addEdge(v0.key, v1.key, "01")
                        addEdge(v1.key, v3.key, "13")
                        addEdge(v0.key, v2.key, "02")
                        addEdge(v2.key, v3.key, "23")
                        addEdge(v1.key, v2.key, "12") // крыша соединяет 1 и 2
                    },
                expectedBridges = emptySet(),
            ),
        )

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("testCases")
    fun `test BridgesFind`(testCase: TestCase) {
        val bridgesFinder = BridgesFind<Int, String>()
        val result = bridgesFinder.bridgesFind(testCase.graph)

        val resultSets = result.map { setOf(it.key.first, it.key.second) }.toSet()
        assertEquals(testCase.expectedBridges, resultSets, testCase.description)
    }
}
