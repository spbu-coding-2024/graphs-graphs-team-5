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
import boyaan.model.algorithms.modern.VoteRank

@Composable
fun voteRankWindow(
    currentTab: TabState,
    onClose: () -> Unit,
    onRun: (highlightedVertexKeys: List<Int>?) -> Unit,
) {
    var topKText by remember { mutableStateOf("") }
    val vertices = currentTab.graph.vertices.map { it.key }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = "Выделение ключевых вершин",
            style = MaterialTheme.typography.h6,
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = topKText,
            onValueChange = { topKText = it },
            label = { Text("Количество ключевых вершин (top K)") },
            placeholder = { Text("Оставьте пустым для всех вершин") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(onClick = {
                val topK = topKText.toIntOrNull() ?: Int.MAX_VALUE
                val voteRank = VoteRank<String, String>()
                val result = voteRank.run(currentTab.graph, topK)
                onRun(result.map { it.key })
                onClose()
            }) {
                Text("Найти ключевые вершины")
            }

            Spacer(Modifier.width(12.dp))

            OutlinedButton(onClick = onClose) {
                Text("Отмена")
            }
        }
    }
}
