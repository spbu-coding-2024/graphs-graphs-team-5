package boyaan.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import boyaan.model.algorithms.modern.ForceAtlas2
import boyaan.model.core.base.Graph
import boyaan.model.core.internals.defaults.DefaultGraph

data class TabState(
    var title: String,
    var screen: ScreenState = ScreenState.Home,
    var selectedVertex: MutableState<Int?> = mutableStateOf(null),
    var draggedVertex: Int? = null,
    var floatingWindows: List<FloatingWindow> = emptyList(),
    var activeWindowId: String? = null,
    val graph: Graph<String, String> = DefaultGraph(),
    val vertexPositions: SnapshotStateMap<Int, Offset> = mutableStateMapOf(),
    val fa2: ForceAtlas2<String, String> = ForceAtlas2(graph),
)
