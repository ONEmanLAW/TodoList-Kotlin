package com.example.todolistapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.TaskType

class FilterViewModel : ViewModel() {
    val selStatus = mutableStateListOf<TaskStatus>()
    val selTypes  = mutableStateListOf<TaskType>()
    var seeded = mutableStateOf(false)

    fun seed(initialStatus: List<TaskStatus>, initialTypes: List<TaskType>) {
        if (seeded.value) return
        selStatus.clear(); selStatus.addAll(initialStatus)
        selTypes.clear();  selTypes.addAll(initialTypes)
        seeded.value = true
    }

    fun reset() {
        selStatus.clear()
        selTypes.clear()
    }
}