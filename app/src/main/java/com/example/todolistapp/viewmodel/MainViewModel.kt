package com.example.todolistapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.TaskRepository
import com.example.todolistapp.model.Task
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.TaskType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    // Flux de toutes les tâches (vient du DAO via le repository)
    val tasks: StateFlow<List<Task>> =
        repo.getAllTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // Filtres comme flows
    private val _activeStatus = MutableStateFlow<Set<TaskStatus>>(emptySet())
    private val _activeTypes  = MutableStateFlow<Set<TaskType>>(emptySet())

    val activeStatus: StateFlow<Set<TaskStatus>> = _activeStatus
    val activeTypes:  StateFlow<Set<TaskType>>   = _activeTypes

    // Liste visible = tasks filtrées par flows
    val visibleTasks: StateFlow<List<Task>> =
        combine(tasks, _activeStatus, _activeTypes) { list, s, t ->
            list.filter { task ->
                (s.isEmpty() || task.status in s) &&
                        (t.isEmpty() || task.type in t)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun getTaskById(id: Long): Task? {
        return tasks.value.find { it.id == id }
    }

    // Actions d'écriture -> DB (le flow se mettra à jour tout seul)
    fun addTask(label: String, desc: String, type: TaskType, due: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(
                Task(
                    label = label.trim(),
                    description = desc.trim(),
                    type = type,
                    dueDate = due
                )
            )
        }
    }

    fun updateTask(updated: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.update(updated)
        }
    }

    fun deleteTaskById(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tasks.value.find { it.id == taskId }?.let { repo.delete(it) }
        }
    }

    fun setStatusForTask(taskId: Long, status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setStatus(taskId, status)
        }
    }

    fun setDueDateForTask(taskId: Long, due: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setDueDate(taskId, due)
        }
    }

    // Filtres
    fun applyFilters(statuses: List<TaskStatus>, types: List<TaskType>) {
        _activeStatus.value = statuses.toSet()
        _activeTypes.value  = types.toSet()
    }

    fun resetFilters() {
        _activeStatus.value = emptySet()
        _activeTypes.value  = emptySet()
    }
}
