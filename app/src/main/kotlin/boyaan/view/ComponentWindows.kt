package boyaan.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
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

@Composable
fun nodeEditorWindow() {
    var nodeName by remember { mutableStateOf("") }
    var nodeType by remember { mutableStateOf("") }

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
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = nodeType,
            onValueChange = { nodeType = it },
            label = { Text("Тип узла") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { /* TODO() */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = nodeName.isNotBlank(),
        ) {
            Text("Сохранить узел")
        }
    }
}

@Composable
fun edgeEditorWindow() {
    var fromNode by remember { mutableStateOf("") }
    var toNode by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }

    Column(Modifier.padding(12.dp)) {
        Text("Добавить связь", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = fromNode,
            onValueChange = { fromNode = it },
            label = { Text("Из узла") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = toNode,
            onValueChange = { toNode = it },
            label = { Text("В узел") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = relationship,
            onValueChange = { relationship = it },
            label = { Text("Тип связи") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = fromNode.isNotBlank() && toNode.isNotBlank(),
        ) {
            Text("Создать связь")
        }
    }
}

@Composable
fun propertiesWindow() {
    Column(Modifier.padding(12.dp)) {
        Text("Свойства", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))
        Text("Выберите элемент для просмотра свойств", style = MaterialTheme.typography.body2)
        Spacer(Modifier.height(8.dp))
        Divider()
        Spacer(Modifier.height(8.dp))
        Text("• Узел: Не выбран", style = MaterialTheme.typography.body2)
        Text("• Тип: Нет данных", style = MaterialTheme.typography.body2)
        Text("• ID: Нет данных", style = MaterialTheme.typography.body2)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Обновить свойства")
        }
    }
}
