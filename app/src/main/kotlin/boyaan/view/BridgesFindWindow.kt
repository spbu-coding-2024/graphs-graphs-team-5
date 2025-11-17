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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boyaan.model.TabState
import boyaan.model.algorithms.classic.BridgesFind

@Composable
fun bridgesFindWindow(
    currentTab: TabState,
    onClose: () -> Unit,
    onRun: (bridges: List<Pair<Int, Int>>) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = "Поиск мостов",
            style = MaterialTheme.typography.h6,
        )

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(onClick = {
                val algorithm = BridgesFind<String, String>()
                val bridges = algorithm.bridgesFind(currentTab.graph).map { it.key.first to it.key.second }
                onRun(bridges)
                onClose()
            }) {
                Text("Найти мосты")
            }

            Spacer(Modifier.width(12.dp))

            OutlinedButton(onClick = onClose) {
                Text("Отмена")
            }
        }
    }
}
