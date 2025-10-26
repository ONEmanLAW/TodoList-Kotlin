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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
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

    var pendingDeleteIndex by remember { mutableStateOf<Int?>(null) } // pour suppression depuis la liste
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
            onReset = { activeStatus.clear(); activeTypes.clear() },
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

    // modal de suppression (depuis la liste)
    pendingDeleteIndex?.let { index ->
        AlertDialog(
            onDismissRequest = { pendingDeleteIndex = null },
            title = { Text("Delete task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    tasks.removeAt(index)
                    pendingDeleteIndex = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteIndex = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterScreen(
    initialStatus: List<TaskStatus>,
    initialTypes: List<TaskType>,
    onApply: (List<TaskStatus>, List<TaskType>) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit
) {
    val selStatus = remember { mutableStateListOf<TaskStatus>().apply { addAll(initialStatus) } }
    val selTypes  = remember { mutableStateListOf<TaskType>().apply { addAll(initialTypes) } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Filters") },
                navigationIcon = { TextButton(onClick = onClose) { Text("Close") } },
                actions = {
                    TextButton(
                        onClick = {
                            selStatus.clear()
                            selTypes.clear()
                            onReset()
                        }
                    ) { Text("Reset") }
                }
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
                    Text("Status", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val statuses = listOf(TaskStatus.A_FAIRE, TaskStatus.EN_COURS, TaskStatus.TERMINEE)
                        items(statuses.size) { i ->
                            val s = statuses[i]
                            FilterChip(
                                selected = s in selStatus,
                                onClick = {
                                    if (s in selStatus) selStatus.remove(s) else selStatus.add(s)
                                },
                                label = {
                                    Text(
                                        when (s) {
                                            TaskStatus.A_FAIRE -> "To do"
                                            TaskStatus.EN_COURS -> "In progress"
                                            TaskStatus.TERMINEE -> "Done"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Type", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val types = listOf(TaskType.PERSONNEL, TaskType.TRAVAIL, TaskType.ETUDE, TaskType.AUTRE)
                        items(types.size) { i ->
                            val t = types[i]
                            FilterChip(
                                selected = t in selTypes,
                                onClick = {
                                    if (t in selTypes) selTypes.remove(t) else selTypes.add(t)
                                },
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
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onApply(selStatus.toList(), selTypes.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) { Text("Apply filters") }
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatDateOnly(ms: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", ULocale.getDefault())
    return sdf.format(ms)
}
