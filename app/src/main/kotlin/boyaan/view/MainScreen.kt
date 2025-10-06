package boyaan.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boyaan.model.ScreenState
import boyaan.model.TabState
import boyaan.model.save.loadTabFromFile
import boyaan.viewmodel.MainViewModel
import kotlin.collections.toMutableList

@Composable
fun mainScreen(
    viewModel: MainViewModel,
    onTabLoaded: (TabState) -> Unit,
) {
    MaterialTheme(colors = if (viewModel.isDarkTheme) darkColors() else lightColors()) {
        Scaffold(
            topBar = {
                Column {
                    val showTabs = viewModel.tabs.size > 1 || viewModel.tabs.firstOrNull()?.screen != ScreenState.Home

                    if (showTabs) {
                        TabRow(selectedTabIndex = viewModel.selectedTab) {
                            viewModel.tabs.forEachIndexed { index, tab ->
                                Tab(
                                    selected = viewModel.selectedTab == index,
                                    onClick = { viewModel.selectTab(index) },
                                    text = {
                                        Row {
                                            Text(tab.title)
                                            Spacer(Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { viewModel.closeTab(index) },
                                                modifier = Modifier.size(18.dp),
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Закрыть")
                                            }
                                        }
                                    },
                                )
                            }
                            Tab(
                                selected = false,
                                onClick = { viewModel.addTab() },
                                text = { Text("+") },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        if (viewModel.tabs[viewModel.selectedTab].screen == ScreenState.Graph) {
                            Row {
                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("node_editor", "Редактор вершин")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("Добавить вершину")
                                }

                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("edge_editor", "Редактор ребер")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("Добавить ребро")
                                }

                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("properties", "Свойства")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("Свойства")
                                }

                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("algorithms", "Алгоритмы")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("Алгоритмы")
                                }

                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("json", "Json")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("JSON")
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (viewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                            )
                        }
                    }
                }
            },
        ) { padding ->
            val currentTab = viewModel.tabs[viewModel.selectedTab]

            Box(Modifier.fillMaxSize().padding(padding)) {
                when (currentTab.screen) {
                    ScreenState.Home ->
                        homeScreen(
                            onCreate = { graph ->
                                val currentTab = viewModel.tabs[viewModel.selectedTab]
                                val tab = TabState(title = currentTab.title, graph = graph, screen = ScreenState.Graph)
                                val newTabs = viewModel.tabs.toMutableList()
                                newTabs[viewModel.selectedTab] = tab
                                viewModel.tabs = newTabs
                            },
                            onOpen = {
                                val dialog = javax.swing.JFileChooser()
                                dialog.dialogTitle = "Загрузить JSON"
                                dialog.fileSelectionMode = javax.swing.JFileChooser.FILES_ONLY
                                dialog.fileFilter =
                                    object : javax.swing.filechooser.FileFilter() {
                                        override fun accept(f: java.io.File) = f.isDirectory || f.name.lowercase().endsWith(".json")

                                        override fun getDescription() = "JSON файлы (*.json)"
                                    }

                                val result = dialog.showOpenDialog(null)
                                if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                                    val file = dialog.selectedFile
                                    val loadedTab = loadTabFromFile(file.absolutePath)
                                    if (loadedTab != null) {
                                        onTabLoaded(loadedTab)
                                    }
                                }
                            },
                        )
                    ScreenState.Graph -> {
                        val currentTab = viewModel.tabs[viewModel.selectedTab]

                        graphScreen(
                            graph = currentTab.graph,
                            currentTab = currentTab,
                            floatingWindows = currentTab.floatingWindows,
                            activeWindowId = currentTab.activeWindowId,
                            onCloseWindow = { windowId -> viewModel.closeFloatingWindow(windowId) },
                            onMoveWindow = { windowId, newOffset -> viewModel.moveFloatingWindow(windowId, newOffset) },
                            onActivateWindow = { windowId -> viewModel.activateWindow(windowId) },
                            onVertexSelected = { vKey -> viewModel.selectVertex(vKey) },
                            onEdgeSelected = { (uKey, vKey) -> viewModel.selectEdge(uKey, vKey) },
                        )
                    }
                }
            }
        }
    }
}
