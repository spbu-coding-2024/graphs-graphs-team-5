package boyaan.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
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
import boyaan.viewmodel.MainViewModel

@Composable
fun mainScreen(viewModel: MainViewModel) {
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
                        if (viewModel.tabs[viewModel.selectedTab].screen is ScreenState.Graph) {
                            Row {
                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("node_editor", "Редактор узлов")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("Добавить узел")
                                }

                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("edge_editor", "Редактор связей")
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                ) {
                                    Text("Добавить связь")
                                }

                                Button(
                                    onClick = {
                                        viewModel.openFloatingWindow("properties", "Свойства")
                                    },
                                ) {
                                    Text("Свойства")
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
                    is ScreenState.Home ->
                        homeScreen(
                            onCreate = { viewModel.setScreen(ScreenState.Graph) },
                            onOpen = { viewModel.showOpenDialog = true },
                        )
                    is ScreenState.Graph -> {
                        val currentTab = viewModel.tabs[viewModel.selectedTab]

                        graphScreen(
                            floatingWindows = currentTab.floatingWindows,
                            activeWindowId = currentTab.activeWindowId,
                            onCloseWindow = { windowId -> viewModel.closeFloatingWindow(windowId) },
                            onMoveWindow = { windowId, newOffset -> viewModel.moveFloatingWindow(windowId, newOffset) },
                            onActivateWindow = { windowId ->
                                viewModel.activateWindow(windowId)
                            },
                        )
                    }
                }
            }

            if (viewModel.showOpenDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.showOpenDialog = false },
                    title = { Text("Открыть") },
                    text = {
                        Column {
                            Button(onClick = { viewModel.showOpenDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("SQL") }
                            Button(onClick = { viewModel.showOpenDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("JSON") }
                            Button(onClick = { viewModel.showOpenDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Neo4j") }
                        }
                    },
                    confirmButton = {},
                )
            }
        }
    }
}
