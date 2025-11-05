package com.example.todolistapp.data

import com.example.todolistapp.data.local.TaskDao
import com.example.todolistapp.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.todolistapp.model.Task
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.nowMillisString

class TaskRepository(private val dao: TaskDao) {

    fun observeAll(): Flow<List<Task>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun insert(task: Task) {
        val now = nowMillisString()
        dao.insert(
            TaskEntity(
                label = task.label,
                description = task.description,
                status = task.status,
                type = task.type,
                createdAt = task.createdAt.ifBlank { now },
                updatedAt = task.updatedAt.ifBlank { now },
                dueDate = task.dueDate
            )
        )
    }

    suspend fun update(task: Task) {
        val id = task.id ?: return
        dao.update(
            TaskEntity(
                id = id,
                label = task.label,
                description = task.description,
                status = task.status,
                type = task.type,
                createdAt = task.createdAt,
                updatedAt = nowMillisString(),
                dueDate = task.dueDate
            )
        )
    }

    suspend fun delete(task: Task) { task.id?.let { dao.deleteById(it) } }
    suspend fun setStatus(id: Long, status: TaskStatus) = dao.updateStatus(id, status, nowMillisString())
    suspend fun setDueDate(id: Long, due: String?)   = dao.updateDueDate(id, due, nowMillisString())
}

private fun TaskEntity.toModel() = Task(
    id = id, label = label, description = description, status = status,
    type = type, createdAt = createdAt, updatedAt = updatedAt, dueDate = dueDate
)
