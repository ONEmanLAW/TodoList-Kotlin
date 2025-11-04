package com.example.todolistapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import model.Task
import model.TaskStatus
import model.TaskType
import model.nowMillisString

class MainViewModel : ViewModel() {

    val tasks = mutableStateListOf(
        Task(label = "Prepare TD"),
        Task(label = "Send email"),
        Task(label = "Groceries")
    )

    val activeStatus = mutableStateListOf<TaskStatus>()
    val activeTypes  = mutableStateListOf<TaskType>()

    fun addTask(label: String, desc: String, type: TaskType, due: String?) {
        tasks.add(Task(label = label.trim(), description = desc.trim(), type = type, dueDate = due))
    }

    fun updateTaskAt(index: Int, updated: Task) {
        tasks[index] = updated.copy(updatedAt = nowMillisString())
    }

    fun deleteAt(index: Int) {
        tasks.removeAt(index)
    }

    fun setStatusAt(index: Int, status: TaskStatus) {
        val t = tasks[index]
        tasks[index] = t.copy(status = status, updatedAt = nowMillisString())
    }

    fun setDueAt(index: Int, due: String?) {
        val t = tasks[index]
        tasks[index] = t.copy(dueDate = due, updatedAt = nowMillisString())
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