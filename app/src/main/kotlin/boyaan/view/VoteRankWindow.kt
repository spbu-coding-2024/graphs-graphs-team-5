package boyaan.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Выделение ключевых вершин",
            style = MaterialTheme.typography.h6
        )

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val algorithm = VoteRank<String, String>()
                val vertices = algorithm.run(currentTab.graph)
                val highlightedKeys = vertices.map { it.key }
                onRun(highlightedKeys)
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
