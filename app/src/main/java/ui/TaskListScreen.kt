package ui

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
import androidx.compose.material3.TextButton



@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onToggleStatusAt: (Int) -> Unit,
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
                onToggleStatus = { onToggleStatusAt(index) },
                onDelete = { onDeleteAt(index) }
            )
        }
    }
}


@Composable
fun TaskRow(
    task: Task,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleStatus()}
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (Modifier.weight(1f)) {
            Text(task.label, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)
            Text(
                text = when (task.status) {
                    TaskStatus.A_FAIRE -> "A faire"
                    TaskStatus.EN_COURS -> "En cours"
                    TaskStatus.TERMINEE -> "Terminer"
                },
                style  = MaterialTheme.typography.labelMedium
            )
        }
        TextButton(onClick = onDelete) {
            Text("Supprimer")
        }
    }
}