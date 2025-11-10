package boyaan.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import boyaan.model.algorithms.classic.FordBellman
import boyaan.model.algorithms.classic.FordBellmanResult
import boyaan.model.core.base.Vertex

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun fordBellmanWindow(
    currentTab: TabState,
    onClose: () -> Unit,
    onRun: (result: List<Int>?) -> Unit,
) {
    var startVertexKey by remember { mutableStateOf<Int?>(null) }
    val vertices = currentTab.graph.vertices.map { it.key }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = "Алгоритм Форда–Беллмана",
            style = MaterialTheme.typography.h6,
        )

        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = startVertexKey?.let { key -> currentTab.graph[key]?.value } ?: "",
                onValueChange = {},
                label = { Text("Начальная вершина") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth(),
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                vertices.forEach { key ->
                    DropdownMenuItem(
                        onClick = {
                            startVertexKey = key
                            expanded = false
                        },
                    ) {
                        Text(currentTab.graph[key]?.value?.toString() ?: "Вершина $key")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = {
                    if (startVertexKey != null) {
                        val algorithm = FordBellman<String, String>()
                        val startVertex: Vertex<String> = currentTab.graph[startVertexKey!!]!!
                        val result: FordBellmanResult<String> = algorithm.run(currentTab.graph, startVertex)
                        val pathKeys: List<Int>? =
                            if (result.negativeCycle) {
                                null
                            } else {
                                result.distances.keys.map { it.key }
                            }

                        onRun(pathKeys)
                    } else {
                        onRun(null)
                    }
                    onClose()
                },
                enabled = vertices.isNotEmpty(),
            ) {
                Text("Выполнить")
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedButton(onClick = onClose) {
                Text("Отмена")
            }
        }
    }
}
