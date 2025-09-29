package boyaan.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boyaan.model.core.base.Graph
import boyaan.model.core.internals.defaults.DefaultGraph
import boyaan.model.core.internals.directed.DirectedUnweightedGraph
import boyaan.model.core.internals.directedWeighted.DirectedWeightedGraph
import boyaan.model.core.internals.weighted.UndirectedWeightedGraph

@Composable
fun homeScreen(
    onCreate: (Graph<String, String>) -> Unit,
    onOpen: () -> Unit,
) {
    var isDirected by remember { mutableStateOf(false) }
    var isWeighted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Выберите свойства графа:", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp), fontWeight = FontWeight.Bold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isDirected, onCheckedChange = { isDirected = it })
            Text("Направленный")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isWeighted, onCheckedChange = { isWeighted = it })
            Text("Взвешенный")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val graph =
                    when {
                        isDirected && isWeighted -> DirectedWeightedGraph<String, String>()
                        isDirected && !isWeighted -> DirectedUnweightedGraph()
                        !isDirected && isWeighted -> UndirectedWeightedGraph()
                        else -> DefaultGraph()
                    }
                onCreate(graph)
            },
            modifier = Modifier.width(200.dp).height(60.dp),
        ) {
            Text("Создать граф")
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = onOpen, modifier = Modifier.width(200.dp).height(60.dp)) {
            Text("Открыть")
        }
    }
}
