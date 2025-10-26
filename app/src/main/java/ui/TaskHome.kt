package ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.todolistapp.R
import model.Task
import model.TaskStatus
import model.TaskType
import model.nowMillisString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHome(modifier: Modifier = Modifier) {
    val tasks = remember {
        mutableStateListOf(
            Task(label = "Prepare TD"),
            Task(label = "Send email"),
            Task(label = "Groceries")
        )
    }

    val activeStatus = remember { mutableStateListOf<TaskStatus>() }
    val activeTypes = remember { mutableStateListOf<TaskType>() }

    var showAddDialog by remember { mutableStateOf(false) }
    var addLabel by rememberSaveable { mutableStateOf("") }
    var addDesc by rememberSaveable { mutableStateOf("") }
    var addType by rememberSaveable { mutableStateOf(TaskType.AUTRE) }
    var addDue by rememberSaveable { mutableStateOf<String?>(null) }
    var showAddDatePicker by remember { mutableStateOf(false) }
    val addDateState = rememberDatePickerState()

    var pendingDeleteIndex by remember { mutableStateOf<Int?>(null) }
    var showFilterScreen by remember { mutableStateOf(false) }
    var detailIndex by remember { mutableStateOf<Int?>(null) }

    val visiblePairs = tasks.withIndex().filter { iv ->
        val sOk = activeStatus.isEmpty() || iv.value.status in activeStatus
        val tOk = activeTypes.isEmpty() || iv.value.type in activeTypes
        sOk && tOk
    }
    val visibleTasks = visiblePairs.map { it.value }

    if (showFilterScreen) {
        FilterScreen(
            initialStatus = activeStatus.toList(),
            initialTypes = activeTypes.toList(),
            onApply = { selStatus, selTypes ->
                activeStatus.clear(); activeStatus.addAll(selStatus)
                activeTypes.clear(); activeTypes.addAll(selTypes)
                showFilterScreen = false
            },
            onReset = {
                activeStatus.clear(); activeTypes.clear()
            },
            onClose = { showFilterScreen = false }
        )
        return
    }

    detailIndex?.let { idx ->
        TaskDetailScreen(
            task = tasks[idx],
            onBack = { detailIndex = null },
            onUpdate = { updated ->
                tasks[idx] = updated.copy(updatedAt = nowMillisString())
                detailIndex = null
            },
            onChangeStatus = { newStatus ->
                val t = tasks[idx]
                tasks[idx] = t.copy(status = newStatus, updatedAt = nowMillisString())
            },
            onChangeDueDate = { newDue ->
                val t = tasks[idx]
                tasks[idx] = t.copy(dueDate = newDue, updatedAt = nowMillisString())
            },
            onDelete = {
                tasks.removeAt(idx)
                detailIndex = null
            }
        )
        return
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) { Text("Add task") }

        OutlinedButton(
            onClick = { showFilterScreen = true },
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) { Text("Filter") }

        TaskListScreen(
            tasks = visibleTasks,
            onDeleteAt = { visIndex ->
                val real = visiblePairs[visIndex].index
                pendingDeleteIndex = real
            },
            onOpenAt = { visIndex ->
                val real = visiblePairs[visIndex].index
                detailIndex = real
            }
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                addLabel = ""; addDesc = ""; addType = TaskType.AUTRE; addDue = null
            },
            title = { Text("New task") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = addLabel,
                        onValueChange = { addLabel = it },
                        singleLine = true,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = addDesc,
                        onValueChange = { addDesc = it },
                        label = { Text("Description (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Type", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val all = listOf(TaskType.PERSONNEL, TaskType.TRAVAIL, TaskType.ETUDE, TaskType.AUTRE)
                        items(all.size) { i ->
                            val t = all[i]
                            FilterChip(
                                selected = (t == addType),
                                onClick = { addType = t },
                                label = { Text(t.name.lowercase().replaceFirstChar { it.titlecase() }) }
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = addDue ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Due date") },
                                leadingIcon = {
                                    IconButton(onClick = { showAddDatePicker = true }) {
                                        Icon(
                                            painterResource(id = R.drawable.date_icon),
                                            contentDescription = "Pick date",
                                            modifier = Modifier.scale(0.85f)
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.matchParentSize().clickable { showAddDatePicker = true })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = addLabel.isNotBlank(),
                    onClick = {
                        tasks.add(
                            Task(
                                label = addLabel.trim(),
                                description = addDesc.trim(),
                                type = addType,
                                dueDate = addDue
                            )
                        )
                        showAddDialog = false
                        addLabel = ""; addDesc = ""; addType = TaskType.AUTRE; addDue = null
                    }
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    addLabel = ""; addDesc = ""; addType = TaskType.AUTRE; addDue = null
                }) { Text("Cancel") }
            }
        )
    }

    if (showAddDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showAddDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val ms = addDateState.selectedDateMillis
                    addDue = ms?.let { formatDateOnly(it) }
                    showAddDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = addDateState) }
    }

    pendingDeleteIndex?.let { index ->
        AlertDialog(
            onDismissRequest = { pendingDeleteIndex = null },
            title = { Text("Delete task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    tasks.removeAt(index)
                    pendingDeleteIndex = null
                    if (detailIndex == index) detailIndex = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteIndex = null }) { Text("Cancel") }
            }
        )
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatDateOnly(ms: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", ULocale.getDefault())
    return sdf.format(ms)
}
