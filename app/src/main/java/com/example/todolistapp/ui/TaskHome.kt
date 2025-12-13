package com.example.todolistapp.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistapp.R
import com.example.todolistapp.model.TaskType
import com.example.todolistapp.viewmodel.FilterViewModel
import com.example.todolistapp.viewmodel.HomeViewModel
import com.example.todolistapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHome(modifier: Modifier = Modifier) {
    val mainVm: MainViewModel = hiltViewModel()
    val homeVm: HomeViewModel = viewModel()
    val addDateState = rememberDatePickerState()

    val masterTasksState = mainVm.tasks.collectAsState(initial = emptyList())
    val visibleState     = mainVm.visibleTasks.collectAsState(initial = emptyList())
    val statusFilters    = mainVm.activeStatus.collectAsState(initial = emptySet())
    val typeFilters      = mainVm.activeTypes.collectAsState(initial = emptySet())

    if (homeVm.showFilterScreen.value) {
        val filterVm: FilterViewModel = viewModel()
        FilterScreen(
            initialStatus = statusFilters.value.toList(),
            initialTypes = typeFilters.value.toList(),
            vm = filterVm,
            onApply = { st, tp ->
                mainVm.applyFilters(st, tp)
                homeVm.showFilterScreen.value = false
            },
            onReset = { mainVm.resetFilters() },
            onClose = { homeVm.showFilterScreen.value = false }
        )
        return
    }

    homeVm.detailIndex.value?.let { idx ->
        TaskDetailScreenHost(
            index = idx,
            task = masterTasksState.value[idx],
            onBack = { homeVm.detailIndex.value = null },
            onSave = { updated -> mainVm.updateTaskAt(idx, updated); homeVm.detailIndex.value = null },
            onDelete = { mainVm.deleteAt(idx); homeVm.detailIndex.value = null },
            onSetStatus = { st -> mainVm.setStatusAt(idx, st) },
            onSetDue = { due -> mainVm.setDueAt(idx, due) }
        )
        return
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Button(
            onClick = { homeVm.showAddDialog.value = true },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) { Text("Add task") }

        OutlinedButton(
            onClick = { homeVm.showFilterScreen.value = true },
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) { Text("Filter") }

        val visibleTasks = visibleState.value
        if (visibleTasks.isNotEmpty()) {
            TaskListScreen(
                tasks = visibleTasks,
                onDeleteAt = { visIndex ->
                    val task = visibleTasks.getOrNull(visIndex) ?: return@TaskListScreen
                    val real = masterTasksState.value.indexOfFirst { it.id == task.id }
                    if (real >= 0) homeVm.pendingDeleteIndex.value = real
                },
                onOpenAt = { visIndex ->
                    val task = visibleTasks.getOrNull(visIndex) ?: return@TaskListScreen
                    val real = masterTasksState.value.indexOfFirst { it.id == task.id }
                    if (real >= 0) homeVm.detailIndex.value = real
                }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No tasks found.")
                Text("Click on the 'Add task' button to add a new task.")
            }
        }
    }

    if (homeVm.showAddDialog.value) {
        AlertDialog(
            onDismissRequest = {
                homeVm.showAddDialog.value = false
                homeVm.resetAddForm()
            },
            title = { Text("New task") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = homeVm.addLabel.value,
                        onValueChange = { homeVm.addLabel.value = it },
                        singleLine = true,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = homeVm.addDesc.value,
                        onValueChange = { homeVm.addDesc.value = it },
                        label = { Text("Description (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Type", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val all = listOf(TaskType.PERSONNEL, TaskType.TRAVAIL, TaskType.ETUDE, TaskType.AUTRE)
                        items(all.size) { i ->
                            val t = all[i]
                            FilterChip(
                                selected = t == homeVm.addType.value,
                                onClick = { homeVm.addType.value = t },
                                label = { Text(t.name.lowercase().replaceFirstChar { it.titlecase() }) }
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = homeVm.addDue.value ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Due date") },
                                leadingIcon = {
                                    IconButton(onClick = { homeVm.showAddDatePicker.value = true }) {
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
                                    .clickable { homeVm.showAddDatePicker.value = true }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = homeVm.addLabel.value.isNotBlank(),
                    onClick = {
                        mainVm.addTask(
                            label = homeVm.addLabel.value,
                            desc = homeVm.addDesc.value,
                            type = homeVm.addType.value,
                            due = homeVm.addDue.value
                        )
                        homeVm.showAddDialog.value = false
                        homeVm.resetAddForm()
                    }
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    homeVm.showAddDialog.value = false
                    homeVm.resetAddForm()
                }) { Text("Cancel") }
            }
        )
    }

    if (homeVm.showAddDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { homeVm.showAddDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    val ms = addDateState.selectedDateMillis
                    homeVm.addDue.value = ms?.let { formatDateOnly(it) }
                    homeVm.showAddDatePicker.value = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { homeVm.showAddDatePicker.value = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = addDateState) }
    }

    homeVm.pendingDeleteIndex.value?.let { index ->
        AlertDialog(
            onDismissRequest = { homeVm.pendingDeleteIndex.value = null },
            title = { Text("Delete task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    mainVm.deleteAt(index)
                    homeVm.pendingDeleteIndex.value = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { homeVm.pendingDeleteIndex.value = null }) { Text("Cancel") }
            }
        )
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatDateOnly(ms: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", ULocale.getDefault())
    return sdf.format(ms)
}
