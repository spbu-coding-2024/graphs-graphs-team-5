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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boyaan.model.TabState
import boyaan.model.save.Neo4j
import kotlinx.coroutines.launch

@Composable
fun neo4jExportWindow(
    currentTab: TabState,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var uri by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isValid = uri.isNotBlank() && user.isNotBlank() && password.isNotBlank()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text("Экспорт в Neo4j", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uri,
            onValueChange = { uri = it },
            label = { Text("URI") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("User") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            OutlinedButton(onClick = onClose) {
                Text("Отмена")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val neo = Neo4j(uri, user, password)
                        neo.exportGraph(currentTab.graph)
                        neo.close()
                        onClose()
                    }
                },
                enabled = isValid,
            ) {
                Text("Экспортировать")
            }
        }
    }
}
