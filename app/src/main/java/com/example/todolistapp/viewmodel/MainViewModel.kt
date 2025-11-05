package com.example.todolistapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.TaskRepository
import com.example.todolistapp.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.todolistapp.model.Task
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.TaskType

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = TaskRepository(AppDatabase.getInstance(application).taskDao())
    val tasks = mutableStateListOf<Task>()
    val activeStatus = mutableStateListOf<TaskStatus>()
    val activeTypes  = mutableStateListOf<TaskType>()

    init {
        viewModelScope.launch {
            repo.observeAll().collectLatest { list ->
                tasks.clear()
                tasks.addAll(list)
            }
        }
    }

    fun addTask(label: String, desc: String, type: TaskType, due: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(Task(label = label.trim(), description = desc.trim(), type = type, dueDate = due))
        }
    }

    fun updateTaskAt(index: Int, updated: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = tasks.getOrNull(index) ?: return@launch
            repo.update(updated.copy(id = current.id))
        }
    }

    fun deleteAt(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tasks.getOrNull(index)?.let { repo.delete(it) }
        }
    }

    fun setStatusAt(index: Int, status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            tasks.getOrNull(index)?.id?.let { repo.setStatus(it, status) }
        }
    }

    fun setDueAt(index: Int, due: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            tasks.getOrNull(index)?.id?.let { repo.setDueDate(it, due) }
        }
    }

    fun applyFilters(statuses: List<TaskStatus>, types: List<TaskType>) {
        activeStatus.clear(); activeStatus.addAll(statuses)
        activeTypes.clear();  activeTypes.addAll(types)
    }

    fun resetFilters() {
        activeStatus.clear()
        activeTypes.clear()
    }
}