package boyaan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import boyaan.model.FloatingWindow
import boyaan.model.ScreenState
import boyaan.model.TabState
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.view.cycleFinderWindowForVertex
import boyaan.view.edgeEditorWindow
import boyaan.view.propertiesWindow
import boyaan.view.vertexEditorWindow
import java.util.UUID
import kotlin.let

class MainViewModel {
    var isDarkTheme by mutableStateOf(false)
    var tabs by mutableStateOf(listOf(TabState("Вкладка 1")))
    var selectedTab by mutableStateOf(0)
    var showOpenDialog by mutableStateOf(false)

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun selectTab(index: Int) {
        selectedTab = index
    }

    fun addTab() {
        tabs = tabs + TabState(title = "Вкладка ${tabs.size + 1}", graph = DefaultGraph())
        selectedTab = tabs.lastIndex
    }

    fun closeTab(index: Int) {
        if (tabs.size > 1) {
            val newTabs = tabs.toMutableList()
            newTabs.removeAt(index)
            tabs = newTabs
            if (tabs.size == 1) {
                tabs.last().title = "Вкладка 1"
            }
            selectedTab = selectedTab.coerceAtMost(tabs.lastIndex)
        } else {
            tabs = listOf(TabState("Вкладка 1", screen = ScreenState.Home))
            selectedTab = 0
        }
    }

    fun setScreen(screen: ScreenState) {
        tabs =
            tabs.toMutableList().also {
                it[selectedTab] = it[selectedTab].copy(screen = screen)
            }
    }

    fun selectVertex(vKey: Int) {
        tabs[selectedTab].selectedVertex.value = vKey
    }

    fun addVertexToCurrentTab(name: String) {
        val tab = tabs[selectedTab]
        val vKey = tab.graph.addVertex(name).key
        tab.vertexPositions[vKey] =
            Offset(
                (100..800).random().toFloat(),
                (100..600).random().toFloat(),
            )
    }

    fun openFloatingWindow(
        type: String,
        title: String,
    ) {
        val windowId = UUID.randomUUID().toString()
        val content =
            when (type) {
                "node_editor" -> {
                    @androidx.compose.runtime.Composable {
                        vertexEditorWindow(
                            addVertex = {
                                addVertexToCurrentTab(it)
                            },
                            onClose = { closeFloatingWindow(windowId) },
                        )
                    }
                }
                "edge_editor" -> {
                    @androidx.compose.runtime.Composable {
                        edgeEditorWindow(
                            graph = tabs[selectedTab].graph,
                            onClose = { closeFloatingWindow(windowId) },
                        )
                    }
                }
                "properties" -> {
                    @androidx.compose.runtime.Composable {
                        val currentTab = tabs[selectedTab]
                        val selectedVertex =
                            currentTab.selectedVertex.value?.let {
                                currentTab.graph[it]
                            }
                        propertiesWindow(selectedVertex)
                    }
                }
                "cycle_finder_vertex" -> {
                    @androidx.compose.runtime.Composable
                    {
                        val currentTab = tabs.getOrNull(selectedTab)
                        if (currentTab != null) {
                            cycleFinderWindowForVertex(
                                graph = currentTab.graph,
                                selectedVertexKey = currentTab.selectedVertex.value,
                                onCyclesFound = { cycles ->
                                    currentTab.highlightedVertex.clear()
                                    cycles.forEach { cycle ->
                                        cycle.forEach { vKey -> currentTab.highlightedVertex[vKey] = true }
                                    }
                                },
                                onClearHighlight = { currentTab.highlightedVertex.clear() },
                                onClose = { closeFloatingWindow(windowId) },
                            )
                        }
                    }
                }
                else -> null
            }

        val newWindow =
            FloatingWindow(
                id = windowId,
                title = title,
                position =
                    Offset(
                        100f + tabs[selectedTab].floatingWindows.size * 30f,
                        100f + tabs[selectedTab].floatingWindows.size * 30f,
                    ),
                size = IntSize(350, 300),
                content = content,
            )

        tabs =
            tabs.toMutableList().also { list ->
                val tab = list[selectedTab]
                list[selectedTab] =
                    tab.copy(
                        floatingWindows = tab.floatingWindows + newWindow,
                        activeWindowId = windowId,
                    )
            }
    }

    fun closeFloatingWindow(windowId: String) {
        tabs =
            tabs.toMutableList().also { list ->
                val tab = list[selectedTab]
                val newWindows = tab.floatingWindows.filter { it.id != windowId }

                val newActiveWindowId =
                    if (tab.activeWindowId == windowId) {
                        newWindows.lastOrNull()?.id
                    } else {
                        tab.activeWindowId
                    }

                list[selectedTab] =
                    tab.copy(
                        floatingWindows = newWindows,
                        activeWindowId = newActiveWindowId,
                    )
            }
    }

    fun moveFloatingWindow(
        windowId: String,
        newPosition: Offset,
    ) {
        tabs =
            tabs.toMutableList().also { list ->
                val tab = list[selectedTab]
                val updatedWindows =
                    tab.floatingWindows.map { window ->
                        if (window.id == windowId) {
                            val boundedX = newPosition.x.coerceIn(0f, 1200f - window.size.width)
                            val boundedY = newPosition.y.coerceIn(0f, 800f - window.size.height)
                            window.copy(position = Offset(boundedX, boundedY))
                        } else {
                            window
                        }
                    }
                list[selectedTab] = tab.copy(floatingWindows = updatedWindows)
            }
    }

    fun activateWindow(windowId: String) {
        tabs =
            tabs.toMutableList().also { list ->
                val tab = list[selectedTab]
                val windowExists = tab.floatingWindows.any { it.id == windowId }

                if (windowExists) {
                    list[selectedTab] = tab.copy(activeWindowId = windowId)
                }
            }
    }

    fun getActiveWindow(): FloatingWindow? {
        val currentTab = tabs.getOrNull(selectedTab) ?: return null
        return currentTab.floatingWindows.find { it.id == currentTab.activeWindowId }
    }
}
