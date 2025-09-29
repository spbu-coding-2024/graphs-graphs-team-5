package boyaan.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boyaan.viewmodel.GraphType

@Composable
fun homeScreen(
    onCreate: (GraphType) -> Unit,
    onOpen: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Выберите тип графа, который нужно создать:")
        Spacer(Modifier.height(12.dp))

        GraphType.entries.forEach { type ->
            Button(
                onClick = { onCreate(type) },
                modifier = Modifier.width(200.dp).height(60.dp),
            ) {
                Text(type.name)
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = onOpen, modifier = Modifier.width(200.dp).height(60.dp)) {
            Icon(Icons.Default.Folder, contentDescription = "Открыть")
            Spacer(Modifier.width(8.dp))
            Text("Открыть")
        }
    }
}
