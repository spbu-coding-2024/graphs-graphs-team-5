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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun homeScreen(
    onCreate: () -> Unit,
    onOpen: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = onCreate, modifier = Modifier.width(200.dp).height(60.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Создать")
            Spacer(Modifier.width(8.dp))
            Text("Создать")
        }

        Spacer(Modifier.height(24.dp)) // отступ между кнопками

        Button(onClick = onOpen, modifier = Modifier.width(200.dp).height(60.dp)) {
            Icon(Icons.Default.Folder, contentDescription = "Открыть")
            Spacer(Modifier.width(8.dp))
            Text("Открыть")
        }
    }
}
