package boyaan.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boyaan.model.TabState
import boyaan.model.algorithms.classic.Dijkstra
import boyaan.model.algorithms.classic.FindCycles
import boyaan.model.core.base.Edge
import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex
import boyaan.model.core.internals.directed.Directed
import boyaan.model.core.internals.weighted.Weighted
import boyaan.model.core.internals.weighted.WeightedGraph
import boyaan.model.save.TabStateD
import boyaan.model.save.saveTabToFile
import boyaan.model.save.toData
import javax.swing.JFileChooser
import kotlin.math.max
import kotlin.math.min

@Composable
fun vertexEditorWindow(
    addVertex: (String) -> Unit,
    onClose: () -> Unit,
) {
    var nodeName by remember { mutableStateOf("") }

    Column(Modifier.padding(12.dp)) {
        Text("Добавить вершину", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = nodeName,
            onValueChange = { nodeName = it },
            label = { Text("Имя вершины") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                addVertex(nodeName)
                onClose()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nodeName.isNotBlank(),
        ) {
            Text("Сохранить вершину")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun edgeEditorWindow(
    onClose: () -> Unit,
    currentTab: TabState,
) {
    var fromVertexKey by remember { mutableStateOf(currentTab.selectedVertex.value) }
    var toVertexKey by remember { mutableStateOf<Int?>(null) }
    var edgeData by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }

    val fromOptions = currentTab.graph.vertices.map { it.key to it.value }
    val toOptions =
        currentTab.graph.vertices
            .filter { it.key != fromVertexKey }
            .map { it.key to it.value }

    Column(Modifier.padding(12.dp)) {
        Text("Добавить ребро", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        var fromExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = fromExpanded, onExpandedChange = { fromExpanded = !fromExpanded }) {
            OutlinedTextField(
                value = fromVertexKey?.let { currentTab.graph[it]?.value } ?: "",
                onValueChange = {},
                label = { Text("Из вершины") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                fromOptions.forEach { (key, name) ->
                    DropdownMenuItem(onClick = {
                        fromVertexKey = key
                        if (toVertexKey == key) toVertexKey = null
                        fromExpanded = false
                    }) {
                        Text(name)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        var toExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = toExpanded, onExpandedChange = { toExpanded = !toExpanded }) {
            OutlinedTextField(
                value = toVertexKey?.let { currentTab.graph[it]?.value } ?: "",
                onValueChange = {},
                label = { Text("В вершину") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                toOptions.forEach { (key, name) ->
                    DropdownMenuItem(onClick = {
                        toVertexKey = key
                        toExpanded = false
                    }) {
                        Text(name)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = edgeData,
            onValueChange = { edgeData = it },
            label = { Text("Данные ребра") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        if (currentTab.graph is WeightedGraph) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Вес ребра") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val from = fromVertexKey
                val to = toVertexKey
                val weight = weightText.toDoubleOrNull()
                if (from != null && to != null) {
                    if (currentTab.graph is WeightedGraph) {
                        currentTab.graph.addEdge(from, to, edgeData, weight ?: 1.0)
                    } else {
                        currentTab.graph.addEdge(from, to, edgeData)
                    }
                    onClose()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = fromVertexKey != null && toVertexKey != null && edgeData.isNotBlank(),
        ) {
            Text("Создать ребро")
        }
    }
}

@Composable
fun propertiesWindow(
    selectedVertex: Vertex<String>?,
    selectedEdge: Edge<String>?,
) {
    Column(Modifier.padding(12.dp)) {
        selectedVertex?.let {
            Text("Узел", style = MaterialTheme.typography.h6)
            Spacer(Modifier.height(12.dp))

            Text("• Ключ: ${selectedVertex.key}")
            Text("• Данные: ${selectedVertex.value}")
        } ?: selectedEdge?.let {
            Text("Ребро", style = MaterialTheme.typography.h6)
            Spacer(Modifier.height(12.dp))

            val (from, to) = selectedEdge.key
            if (selectedEdge is Directed) {
                Text("• Ключ начала: $from")
                Text("• Ключ конца: $to")
            } else {
                Text("• Ключи вершин: ${min(from, to)}, ${max(from, to)}")
            }
            Text("• Данные: ${selectedEdge.value}")
            if (selectedEdge is Weighted) {
                Text("• Вес: ${selectedEdge.weight}")
            }
        } ?: Text("Выберите элемент для просмотра свойств")
    }
}

@Composable
fun algorithms(
    graph: Graph<String, String>?,
    selectedVertexKey: Int?,
    onCyclesFound: (List<List<Int>>) -> Unit,
    onClearHighlight: () -> Unit,
    openWindow: (String, String) -> Unit,
) {
    Column(Modifier.padding(12.dp)) {
        Text("Применить алгоритмы на графе", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (selectedVertexKey != null && graph != null) {
                    graph[selectedVertexKey]?.let { vertex ->
                        val finder = FindCycles(graph)
                        val cycles = finder.findCycles(vertex)
                        onCyclesFound(cycles)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedVertexKey != null && graph != null,
        ) {
            Text("Найти циклы")
        }
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                openWindow("dijkstra", "Алгоритм Дейкстры")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedVertexKey != null && graph != null,
        ) {
            Text("Алгоритм Дейкстры")
        }
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onClearHighlight,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Очистить подсветку")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                openWindow("ford_bellman", "Аглоритм Форда-Беллмана")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedVertexKey != null && graph != null,
        ) {
            Text("Алгоритм Форда-Беллама")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                openWindow("bridges", "Поиск мостов")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = graph != null,
        ) {
            Text("Поиск мостов")
        }
    }
}

@Composable
fun saveTabWindow(
    tab: TabState,
    onClose: () -> Unit,
) {
    var graphName by remember { mutableStateOf(tab.title) }
    var savePath by remember { mutableStateOf<String?>(null) }

    Column(Modifier.padding(16.dp).width(300.dp)) {
        Text("Сохранение вкладки", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = graphName,
            onValueChange = { graphName = it },
            label = { Text("Название графа") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val dialog = JFileChooser()
                dialog.dialogTitle = "Выберите JSON"
                dialog.fileSelectionMode = JFileChooser.FILES_ONLY
                dialog.fileFilter =
                    object : javax.swing.filechooser.FileFilter() {
                        override fun accept(f: java.io.File) = f.isDirectory || f.name.lowercase().endsWith(".json")

                        override fun getDescription() = "JSON файлы (*.json)"
                    }
                val result = dialog.showSaveDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    var file = dialog.selectedFile
                    if (!file.name.endsWith(".json")) {
                        file = java.io.File(file.parentFile, file.name + ".json")
                    }
                    savePath = file.absolutePath
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(savePath ?: "Выбрать место сохранения")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val path = savePath ?: return@Button
                val tabData: TabStateD = tab.toData()
                saveTabToFile(tabData, path, graphName)
                onClose()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = graphName.isNotBlank() && savePath != null,
        ) {
            Text("Сохранить")
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Отмена")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun dijkstraWindow(
    currentTab: TabState,
    onRun: (Dijkstra.PathResult?) -> Unit,
    onClose: () -> Unit,
) {
    var startVertexKey by remember { mutableStateOf(currentTab.selectedVertex.value) }
    var targetVertexKey by remember { mutableStateOf<Int?>(null) }

    val startOptions =
        currentTab.graph.vertices
            .filter { it.key != targetVertexKey }
            .map { it.key to it.value }
    val targetOptions =
        currentTab.graph.vertices
            .filter { it.key != startVertexKey }
            .map { it.key to it.value }

    Column(Modifier.padding(12.dp)) {
        Text("Алгоритм Дейкстры", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        var startExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = startExpanded, onExpandedChange = { startExpanded = !startExpanded }) {
            OutlinedTextField(
                value = startVertexKey?.let { key -> currentTab.graph[key]?.value } ?: "",
                onValueChange = {},
                label = { Text("Начальная вершина") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startExpanded) },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = startExpanded, onDismissRequest = { startExpanded = false }) {
                startOptions.forEach { (key, name) ->
                    DropdownMenuItem(onClick = {
                        startVertexKey = key
                        if (targetVertexKey == key) targetVertexKey = null
                        startExpanded = false
                    }) {
                        Text(name)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        var targetExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = targetExpanded, onExpandedChange = { targetExpanded = !targetExpanded }) {
            OutlinedTextField(
                value = targetVertexKey?.let { key -> currentTab.graph[key]?.value } ?: "",
                onValueChange = {},
                label = { Text("Конечная вершина") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = targetExpanded, onDismissRequest = { targetExpanded = false }) {
                targetOptions.forEach { (key, name) ->
                    DropdownMenuItem(onClick = {
                        targetVertexKey = key
                        targetExpanded = false
                    }) {
                        Text(name)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val startKey = startVertexKey
                val targetKey = targetVertexKey
                val startVertex = startKey?.let { currentTab.graph[it] }
                val targetVertex = targetKey?.let { currentTab.graph[it] }

                if (startVertex != null && targetVertex != null) {
                    val dijkstra = Dijkstra(currentTab.graph)
                    val result = dijkstra.shortestPath(startVertex, targetVertex)
                    onRun(result)
                    onClose()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = startVertexKey != null && targetVertexKey != null,
        ) {
            Text("Выполнить")
        }
    }
}
