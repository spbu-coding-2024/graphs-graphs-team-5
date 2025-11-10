package boyaan.model.save
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import boyaan.model.ScreenState
import boyaan.model.TabState
import boyaan.model.core.base.Graph
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedEdge
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import boyaan.model.core.internals.weighted.UndirectedWeightedGraph
import boyaan.model.core.internals.weighted.WeightedEdge
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class VertexD(
    val key: Int,
    val value: String,
)

@Serializable
data class EdgeD(
    val from: Int,
    val to: Int,
    val value: String,
    val weight: Double? = null,
)

@Serializable
enum class GraphType { DEFAULT, DIRECTED, WEIGHTED, DIRECTED_WEIGHTED }

@Serializable
data class GraphD(
    val type: GraphT,
    val vertices: List<VertexD>,
    val edges: List<EdgeD>,
)

@Serializable
data class OffsetD(
    val x: Float,
    val y: Float,
)

@Serializable
data class TabStateD(
    var title: String,
    val screen: String,
    val selectedVertex: Int?,
    val selectedEdge: Pair<Int, Int>?,
    val draggedVertex: Int?,
    val activeWindowId: String?,
    val vertexPositions: Map<Int, OffsetD>,
    val highlightedVertex: Map<Int, Boolean>,
    val graph: GraphD,
)

fun Offset.toData() = OffsetD(x, y)

fun OffsetD.toOffset() = Offset(x, y)

fun Graph<String, String>.toData(): GraphD {
    val type =
        when (this) {
            is DirectedWeightedGraph -> GraphT.DIRECTED_WEIGHTED
            is UndirectedWeightedGraph -> GraphT.WEIGHTED
            is DirectedUnweightedGraph -> GraphT.DIRECTED
            else -> GraphT.DEFAULT
        }

    return GraphD(
        type = type,
        vertices = vertices.map { VertexD(it.key, it.value) },
        edges =
            edges.map { edge ->
                when (edge) {
                    is WeightedEdge -> EdgeD(edge.key.first, edge.key.second, edge.value, edge.weight)
                    is DirectedWeightedEdge -> EdgeD(edge.key.first, edge.key.second, edge.value, edge.weight)
                    else -> EdgeD(edge.key.first, edge.key.second, edge.value)
                }
            },
    )
}

fun GraphD.toGraph(): Graph<String, String> {
    print(type)
    val g =
        when (type) {
            GraphT.DEFAULT -> DefaultGraph<String, String>()
            GraphT.DIRECTED -> DirectedUnweightedGraph()
            GraphT.WEIGHTED -> UndirectedWeightedGraph()
            GraphT.DIRECTED_WEIGHTED -> DirectedWeightedGraph()
        }

    vertices.forEach { g.addVertex(it.value) }
    edges.forEach {
        when {
            g is DirectedWeightedGraph && it.weight != null -> g.addEdge(it.from, it.to, it.value, it.weight)
            g is UndirectedWeightedGraph && it.weight != null -> g.addEdge(it.from, it.to, it.value, it.weight)
            else -> g.addEdge(it.from, it.to, it.value)
        }
    }

    return g
}

fun TabState.toData(): TabStateD =
    TabStateD(
        title = title,
        screen = screen.name,
        selectedVertex = selectedVertex.value,
        selectedEdge = selectedEdge.value,
        draggedVertex = draggedVertex,
        activeWindowId = activeWindowId,
        vertexPositions = vertexPositions.mapValues { it.value.toData() },
        highlightedVertex = highlightedVertex.toMap(),
        graph = graph.toData(),
    )

fun TabStateD.toTabState(): TabState {
    val vertexPositionsMap: SnapshotStateMap<Int, Offset> = mutableStateMapOf()
    vertexPositions.forEach { (k, v) -> vertexPositionsMap[k] = v.toOffset() }

    val highlightedMap: SnapshotStateMap<Int, Boolean> = mutableStateMapOf()
    highlightedVertex.forEach { (k, v) -> highlightedMap[k] = v }

    return TabState(
        title = title,
        screen = ScreenState.valueOf(screen),
        selectedVertex = mutableStateOf(selectedVertex),
        selectedEdge = mutableStateOf(selectedEdge),
        draggedVertex = draggedVertex,
        activeWindowId = activeWindowId,
        graph = graph.toGraph(),
        vertexPositions = vertexPositionsMap,
        highlightedVertex = highlightedMap,
    )
}

fun saveTabToFile(
    tab: TabStateD,
    filepath: String,
    title: String,
) {
    val json = Json { prettyPrint = true }
    tab.title = title
    val jsonEncode = json.encodeToString(tab)
    File(filepath).writeText(jsonEncode)
}

fun loadTabFromFile(filepath: String): TabState? {
    val file = File(filepath)
    if (!file.exists()) return null
    val json = Json { prettyPrint = true }
    val tabData = json.decodeFromString<TabStateD>(file.readText())
    return tabData.toTabState()
}
