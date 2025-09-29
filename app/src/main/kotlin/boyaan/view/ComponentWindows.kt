package boyaan.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import boyaan.model.algorithms.classic.FindCycles
import boyaan.model.core.base.Graph
import boyaan.model.core.base.Vertex

@Composable
fun vertexEditorWindow(
    addVertex: (String) -> Unit,
    onClose: () -> Unit,
) {
    var nodeName by remember { mutableStateOf("") }

    Column(Modifier.padding(12.dp)) {
        Text("Добавить узел", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = nodeName,
            onValueChange = { nodeName = it },
            label = { Text("Имя узла") },
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
            Text("Сохранить узел")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun edgeEditorWindow(
    graph: Graph<String, String>,
    onClose: () -> Unit,
) {
    var fromVertexKey by remember { mutableStateOf<Int?>(null) }
    var toVertexKey by remember { mutableStateOf<Int?>(null) }
    var edgeData by remember { mutableStateOf("") }

    val fromOptions = graph.vertices.map { it.key to it.value }
    val toOptions =
        graph.vertices
            .filter { it.key != fromVertexKey }
            .map { it.key to it.value }

    Column(Modifier.padding(12.dp)) {
        Text("Добавить связь", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        var fromExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = fromExpanded,
            onExpandedChange = { fromExpanded = !fromExpanded },
        ) {
            OutlinedTextField(
                value = fromVertexKey?.let { graph[it]?.value } ?: "",
                onValueChange = {},
                label = { Text("Из узла") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = fromExpanded,
                onDismissRequest = { fromExpanded = false },
            ) {
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
        ExposedDropdownMenuBox(
            expanded = toExpanded,
            onExpandedChange = { toExpanded = !toExpanded },
        ) {
            OutlinedTextField(
                value = toVertexKey?.let { graph[it]?.value } ?: "",
                onValueChange = {},
                label = { Text("В узел") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = toExpanded,
                onDismissRequest = { toExpanded = false },
            ) {
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

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val from = fromVertexKey
                val to = toVertexKey
                if (from != null && to != null) {
                    graph.addEdge(from, to, edgeData)
                    onClose()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = fromVertexKey != null && toVertexKey != null && edgeData.isNotBlank(),
        ) {
            Text("Создать связь")
        }
    }
}

@Composable
fun propertiesWindow(selectedVertex: Vertex<String>?) {
    Column(Modifier.padding(12.dp)) {
        Text("Свойства", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        if (selectedVertex != null) {
            Text("• Узел: ${selectedVertex.value}")
            Text("• ID: ${selectedVertex.key}")
        } else {
            Text("Выберите элемент для просмотра свойств")
        }
    }
}

@Composable
fun cycleFinderWindowForVertex(
    graph: Graph<String, String>?,
    selectedVertexKey: Int?,
    onCyclesFound: (List<List<Int>>) -> Unit,
    onClearHighlight: () -> Unit,
    onClose: () -> Unit,
) {
    Column(Modifier.padding(12.dp)) {
        Text("Поиск циклов из выбранной вершины", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val vertexKey = selectedVertexKey
                val g = graph
                if (vertexKey != null && g != null) {
                    g[vertexKey]?.let { vertex ->
                        val finder = FindCycles(g)
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
            onClick = onClearHighlight,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Очистить подсветку")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Закрыть")
        }
    }
}
