package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import model.Task
import model.TaskStatus

@Composable
fun TaskHome(modifier: Modifier = Modifier) {
    val tasks = remember {
        mutableStateListOf(
            Task(label = "Préparer le TD"),
            Task(label = "Envoyer le mail"),
            Task(label = "Courses")
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { showAddDialog = true }) {
            Text("Ajouter une tache")
        }

        TaskListScreen(
            tasks = tasks,
            onToggleStatusAt = { index ->
                val t = tasks[index]
                val next = when (t.status) {
                    TaskStatus.A_FAIRE -> TaskStatus.EN_COURS
                    TaskStatus.EN_COURS -> TaskStatus.TERMINEE
                    TaskStatus.TERMINEE -> TaskStatus.A_FAIRE
                }
                tasks[index] = t.copy(status = next)
            },
            onDeleteAt = { index ->
                tasks.removeAt(index)
            }
        )
    }

    // Dialog Ajouter
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newLabel = ""
            },
            title = { Text("Nouvelle tache") },
            text = {
                OutlinedTextField(
                    value = newLabel,
                    onValueChange = { newLabel = it },
                    singleLine = true,
                    label = { Text("Nouvelle tache") },
                    placeholder = { Text("met une nouvelle tache") }
                )
            },
            confirmButton = {
                TextButton(
                    enabled = newLabel.isNotBlank(),
                    onClick = {
                        tasks.add(Task(label = newLabel.trim()))
                        newLabel = ""
                        showAddDialog = false
                    }
                ) { Text("Ajouter") }
            },
            dismissButton = {
                TextButton(onClick = {
                    newLabel = ""
                    showAddDialog = false
                }) { Text("Annuler") }
            }
        )
    }
}


