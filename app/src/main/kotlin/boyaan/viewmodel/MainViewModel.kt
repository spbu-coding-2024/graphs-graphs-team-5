package boyaan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import boyaan.model.FloatingWindow
import boyaan.model.ScreenState
import boyaan.model.TabState
import boyaan.view.edgeEditorWindow
import boyaan.view.nodeEditorWindow
import boyaan.view.propertiesWindow
import java.util.UUID

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
        tabs = tabs + TabState("Вкладка ${tabs.size + 1}")
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

    fun selectNode(node: String) {
        tabs =
            tabs.toMutableList().also {
                it[selectedTab] = it[selectedTab].copy(selectedNode = node)
            }
    }

    fun openFloatingWindow(
        type: String,
        title: String,
    ) {
        val windowId = UUID.randomUUID().toString()
        val content =
            when (type) {
                "node_editor" -> {
                    @androidx.compose.runtime.Composable { nodeEditorWindow() }
                }
                "edge_editor" -> {
                    @androidx.compose.runtime.Composable { edgeEditorWindow() }
                }
                "properties" -> {
                    @androidx.compose.runtime.Composable { propertiesWindow() }
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
        println("=== ACTIVATE WINDOW CALLED ===")
        println("Requested windowId: $windowId")
        println("Current activeWindowId: ${tabs[selectedTab].activeWindowId}")
        println("All windows in tab: ${tabs[selectedTab].floatingWindows.map { it.id }}")

        tabs =
            tabs.toMutableList().also { list ->
                val tab = list[selectedTab]
                val windowExists = tab.floatingWindows.any { it.id == windowId }
                println("Window exists: $windowExists")

                if (windowExists) {
                    list[selectedTab] = tab.copy(activeWindowId = windowId)
                    println("New active window set: $windowId")
                } else {
                    println("Window not found, activation skipped")
                }
            }

        println("New activeWindowId: ${tabs[selectedTab].activeWindowId}")
        println("=== ACTIVATE WINDOW FINISHED ===")
    }

    fun getActiveWindow(): FloatingWindow? {
        val currentTab = tabs.getOrNull(selectedTab) ?: return null
        return currentTab.floatingWindows.find { it.id == currentTab.activeWindowId }
    }
}
