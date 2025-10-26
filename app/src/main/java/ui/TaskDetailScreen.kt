package ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolistapp.R
import model.Task
import model.TaskStatus
import model.TaskType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: Task,
    onBack: () -> Unit,
    onUpdate: (Task) -> Unit,
    onChangeStatus: (TaskStatus) -> Unit,
    onChangeDueDate: (String?) -> Unit,
    onDelete: () -> Unit
) {
    var label by rememberSaveable { mutableStateOf(task.label) }
    var desc by rememberSaveable { mutableStateOf(task.description) }
    var type by rememberSaveable { mutableStateOf(task.type) }
    var due by rememberSaveable { mutableStateOf(task.dueDate) }
    var curStatus by rememberSaveable { mutableStateOf(task.status) }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()

    var askDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Task details") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = { TextButton(onClick = { askDelete = true }) { Text("Delete") } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        singleLine = true,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Type & status", style = MaterialTheme.typography.titleMedium)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val all = listOf(TaskType.PERSONNEL, TaskType.TRAVAIL, TaskType.ETUDE, TaskType.AUTRE)
                        items(all.size) { i ->
                            val t = all[i]
                            FilterChip(
                                selected = (t == type),
                                onClick = { type = t },
                                label = {
                                    Text(
                                        when (t) {
                                            TaskType.PERSONNEL -> "Personal"
                                            TaskType.TRAVAIL -> "Work"
                                            TaskType.ETUDE -> "Study"
                                            TaskType.AUTRE -> "Other"
                                        }
                                    )
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusChoiceButton(
                            label = "To do",
                            selected = curStatus == TaskStatus.A_FAIRE,
                            onClick = {
                                curStatus = TaskStatus.A_FAIRE
                                onChangeStatus(TaskStatus.A_FAIRE)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        StatusChoiceButton(
                            label = "In progress",
                            selected = curStatus == TaskStatus.EN_COURS,
                            onClick = {
                                curStatus = TaskStatus.EN_COURS
                                onChangeStatus(TaskStatus.EN_COURS)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        StatusChoiceButton(
                            label = "Done",
                            selected = curStatus == TaskStatus.TERMINEE,
                            onClick = {
                                curStatus = TaskStatus.TERMINEE
                                onChangeStatus(TaskStatus.TERMINEE)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = due ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Due date") },
                            leadingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        painterResource(id = R.drawable.date_icon),
                                        contentDescription = "Pick date",
                                        modifier = Modifier.scale(0.85f)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(
                            Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }
                }
            }

            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Created: ${formatDateTime(task.createdAt)}")
                    val isModified = task.updatedAt != task.createdAt
                    val prefix = if (isModified) "Updated" else "Updated"
                    Text("$prefix: ${formatDateTime(task.updatedAt)}")
                }
            }

            Button(
                onClick = {
                    if (label.isNotBlank()) {
                        onUpdate(task.copy(label = label.trim(), description = desc.trim(), type = type, dueDate = due))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Save changes") }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val ms = dateState.selectedDateMillis
                    due = ms?.let { formatDateOnly(it) }
                    onChangeDueDate(due)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = dateState) }
    }

    if (askDelete) {
        AlertDialog(
            onDismissRequest = { askDelete = false },
            title = { Text("Delete task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    askDelete = false
                    onDelete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { askDelete = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatusChoiceButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
    val content: @Composable () -> Unit = {
        Text(
            text = label,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium
        )
    }
    if (selected) {
        Button(onClick = onClick, modifier = modifier, contentPadding = padding) { content() }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier, contentPadding = padding) { content() }
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatDateOnly(ms: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", ULocale.getDefault())
    return sdf.format(ms)
}

@SuppressLint("SimpleDateFormat")
private fun formatDateTime(msString: String?): String {
    if (msString.isNullOrBlank()) return "-"
    val ms = msString.toLongOrNull() ?: return msString
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", ULocale.getDefault())
    return sdf.format(ms)
}