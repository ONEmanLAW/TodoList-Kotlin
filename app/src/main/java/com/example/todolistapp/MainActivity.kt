package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolistapp.ui.theme.TodoListAppTheme
import model.Task

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskHome(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskHome(modifier: Modifier = Modifier) {
    val tasks = remember {
        mutableStateListOf(
            Task(label = "Préparer le TD"),
            Task(label = "Envoyer le mail"),
            Task(label = "Courses")
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { showAddDialog = true }) {
                Text("Ajoute une tache")
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks) { task ->
                    Text("• ${task.label}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showAddDialog = false
                        newLabel = ""
                    },
                    title = { Text("Nouvelle Tache") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = newLabel,
                                onValueChange = { newLabel = it },
                                singleLine = true,
                                label = { Text("Tache") },
                                placeholder = { Text("Met une tache") }
                            )
                        }
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
    }
}