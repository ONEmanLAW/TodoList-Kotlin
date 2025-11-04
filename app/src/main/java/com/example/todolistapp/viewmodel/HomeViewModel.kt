package com.example.todolistapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import model.TaskType

class HomeViewModel : ViewModel() {
    var showAddDialog = mutableStateOf(false)
    var showAddDatePicker = mutableStateOf(false)
    var showFilterScreen = mutableStateOf(false)
    var detailIndex = mutableStateOf<Int?>(null)

    var addLabel = mutableStateOf("")
    var addDesc  = mutableStateOf("")
    var addType  = mutableStateOf(TaskType.AUTRE)
    var addDue   = mutableStateOf<String?>(null)

    fun resetAddForm() {
        addLabel.value = ""
        addDesc.value  = ""
        addType.value  = TaskType.AUTRE
        addDue.value   = null
    }
}