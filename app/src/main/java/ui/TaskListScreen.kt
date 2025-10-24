package ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import model.Task
import model.TaskStatus
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onChangeStatusAt: (Int, TaskStatus) -> Unit,
    onEditAt: (Int) -> Unit,
    onDeleteAt: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        itemsIndexed(tasks) { index, task ->
            TaskRow(
                task = task,
                onChangeStatus = { new -> onChangeStatusAt(index, new) },
                onEdit = { onEditAt(index) },
                onDelete = { onDeleteAt(index) }
            )
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onChangeStatus: (TaskStatus) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = task.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(task.description, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusMenu(
                        status = task.status,
                        onSelect = onChangeStatus
                    )
                    Text("• Type: ${task.type.name.lowercase().replaceFirstChar { it.titlecase() }}",
                        style = MaterialTheme.typography.labelMedium)
                }
                val due = task.dueDate?.let { "Échéance: $it" }
                if (due != null) {
                    Text(due, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(6.dp))

            val isModified = task.updatedAt != task.createdAt
            val stamp = if (isModified) task.updatedAt else task.createdAt
            val label = if (isModified) "Modifiée le" else "Créée le"
            Text(
                "$label ${formatDateTime(taskStamp = stamp)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Modifier") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onDelete) { Text("Supprimer") }
            }
        }
    }
}

@Composable
private fun StatusMenu(
    status: TaskStatus,
    onSelect: (TaskStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    TextButton(onClick = { expanded = true }) {
        Text(
            when (status) {
                TaskStatus.A_FAIRE -> "À faire"
                TaskStatus.EN_COURS -> "En cours"
                TaskStatus.TERMINEE -> "Terminée"
            }
        )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("À faire") },
            onClick = { expanded = false; onSelect(TaskStatus.A_FAIRE) }
        )
        DropdownMenuItem(
            text = { Text("En cours") },
            onClick = { expanded = false; onSelect(TaskStatus.EN_COURS) }
        )
        DropdownMenuItem(
            text = { Text("Terminée") },
            onClick = { expanded = false; onSelect(TaskStatus.TERMINEE) }
        )
    }
}


// Added SuppressLint pour eviter surlinage de SimpleDateFormat.
@SuppressLint("SimpleDateFormat")
private fun formatDateTime(taskStamp: String?): String {
    if (taskStamp.isNullOrBlank()) return "-"
    val ms = taskStamp.toLongOrNull() ?: return taskStamp
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", ULocale.getDefault())
    return sdf.format(ms)
}