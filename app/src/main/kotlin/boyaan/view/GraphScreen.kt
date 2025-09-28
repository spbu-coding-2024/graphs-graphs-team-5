package boyaan.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import boyaan.model.FloatingWindow
import boyaan.model.TabState
import boyaan.model.core.base.Graph

@Composable
fun graphScreen(
    graph: Graph<String, String>,
    currentTab: TabState,
    floatingWindows: List<FloatingWindow>,
    activeWindowId: String?,
    onCloseWindow: (String) -> Unit,
    onMoveWindow: (String, Offset) -> Unit,
    onActivateWindow: (String) -> Unit,
    onVertexSelected: (Int) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        draggableGraphView(graph = graph, modifier = Modifier.fillMaxSize(), onVertexSelected = {
            onVertexSelected(it)
        }, currentTab = currentTab)

        floatingWindows.forEach { window ->
            floatingWindowComponent(
                window = window,
                isActive = window.id == activeWindowId,
                onClose = { onCloseWindow(window.id) },
                onMove = { newOffset -> onMoveWindow(window.id, newOffset) },
                onActivate = { onActivateWindow(window.id) },
            )
        }
    }
}
