package com.example.todolistapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import model.Task
import model.TaskStatus
import model.TaskType

class DetailViewModel : ViewModel() {
    var label = mutableStateOf("")
    var desc = mutableStateOf("")
    var type = mutableStateOf(TaskType.AUTRE)
    var due = mutableStateOf<String?>(null)
    var status = mutableStateOf(TaskStatus.A_FAIRE)

    var showDatePicker = mutableStateOf(false)
    var askDelete = mutableStateOf(false)
    var seeded = mutableStateOf(false)

    fun seedFrom(task: Task) {
        if (seeded.value) return
        label.value = task.label
        desc.value = task.description
        type.value = task.type
        due.value = task.dueDate
        status.value = task.status
        seeded.value = true
    }

    fun toUpdated(base: Task): Task =
        base.copy(
            label = label.value.trim(),
            description = desc.value.trim(),
            type = type.value,
            dueDate = due.value,
            status = status.value
        )
}
