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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onDeleteAt: (Int) -> Unit,
    onOpenAt: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        itemsIndexed(tasks) { index, task ->
            TaskRow(
                task = task,
                onDelete = { onDeleteAt(index) },
                onOpen = { onOpenAt(index) }
            )
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDelete) { Text("Delete") }
            }

            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(task.description, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusText = when (task.status) {
                    model.TaskStatus.A_FAIRE -> "To do"
                    model.TaskStatus.EN_COURS -> "In progress"
                    model.TaskStatus.TERMINEE -> "Done"
                }
                Text(statusText, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Type: ${task.type.name.lowercase().replaceFirstChar { it.titlecase() }}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                task.dueDate?.let {
                    Text("Due: $it", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(6.dp))

            val isModified = task.updatedAt != task.createdAt
            val stamp = if (isModified) task.updatedAt else task.createdAt
            val prefix = if (isModified) "Edited on" else "Created on"
            Text(
                "$prefix ${formatDateTime(stamp)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatDateTime(msString: String?): String {
    if (msString.isNullOrBlank()) return "-"
    val ms = msString.toLongOrNull() ?: return msString
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", ULocale.getDefault())
    return sdf.format(ms)
}