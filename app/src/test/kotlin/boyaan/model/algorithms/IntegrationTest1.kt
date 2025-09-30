package boyaan.model.algorithms

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import boyaan.model.ScreenState
import boyaan.model.TabState
import boyaan.model.algorithms.classic.Dijkstra
import boyaan.model.algorithms.classic.FindCycles
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import boyaan.model.save.loadTabFromFile
import boyaan.model.save.saveTabToFile
import boyaan.model.save.toData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class IntegrationTest1 {
    /**
     * Test checks finding cycle and path in graph
     *
     * User create graph -> save it in json file -> load from json -> use algorithm for cycle search -> use Dijkstra algorithm
     */
    @Test
    fun `create graph, save, load, and run algorithms`() {
        val graph = DirectedWeightedGraph<String, String>()
        val v1 = graph.addVertex("A")
        val v2 = graph.addVertex("B")
        val v3 = graph.addVertex("C")
        graph.addEdge(v1.key, v2.key, "AB", 1.0)
        graph.addEdge(v2.key, v3.key, "BC", 2.0)
        graph.addEdge(v3.key, v1.key, "CA", 3.0)

        val vertexPositions: SnapshotStateMap<Int, Offset> = mutableStateMapOf()
        val highlightedVertices: SnapshotStateMap<Int, Boolean> = mutableStateMapOf()

        val tabState =
            TabState(
                title = "Integration Test 1",
                screen = ScreenState.Graph,
                selectedVertex = mutableStateOf(null),
                draggedVertex = null,
                activeWindowId = null,
                graph = graph,
                vertexPositions = vertexPositions,
                highlightedVertex = highlightedVertices,
            )

        val filePath = "compose_desktop_test_graph.json"
        val file = File(filePath)
        saveTabToFile(tabState.toData(), filePath, tabState.title)
        assertTrue(File(filePath).exists(), "Save file must exist")

        val loadedTabState = loadTabFromFile(filePath)
        assertNotNull(loadedTabState, "Load TabState must be not null")

        loadedTabState?.let { loaded ->
            val loadedGraph = loaded.graph
            assertEquals(3, loadedGraph.vertices.size, "Must be 3 vertex after load")

            val startVertex = loadedGraph.vertices.firstOrNull()
            assertNotNull(startVertex, "StartVertex must be exist")

            startVertex?.let { start ->
                val cycles = FindCycles(loadedGraph).findCycles(start)
                assertTrue(
                    cycles.any { it.containsAll(listOf(v1.key, v2.key, v3.key)) },
                    "Cycle 1 -> 2 -> 3 -> 1 must be find",
                )

                val dijkstra = Dijkstra(loadedGraph)
                val result = dijkstra.shortestPaths(start)
                val pathToC = dijkstra.reconstructPath(result.previous, v3.key)
                assertEquals(listOf(v1.key, v2.key, v3.key), pathToC, "path from A to C must be correct")
            }
        }
        file.delete()
    }
}
