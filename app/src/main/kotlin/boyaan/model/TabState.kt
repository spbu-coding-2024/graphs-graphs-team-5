package boyaan.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import boyaan.model.core.base.Graph
import boyaan.model.core.defaults.DefaultGraph

data class TabState(
    var title: String,
    var screen: ScreenState = ScreenState.Home,
    var selectedVertex: Int? = null,
    var draggedVertex: Int? = null,
    var floatingWindows: List<FloatingWindow> = emptyList(),
    var activeWindowId: String? = null,
    val graph: Graph<String, String> = DefaultGraph(),
    var update: Boolean = false,
    val vertexPositions: SnapshotStateMap<Int, Offset> = mutableStateMapOf(),
)
