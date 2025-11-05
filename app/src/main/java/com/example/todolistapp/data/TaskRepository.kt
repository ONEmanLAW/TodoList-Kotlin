package com.example.todolistapp.data

import android.content.Context
import com.example.todolistapp.data.local.AppDatabase
import com.example.todolistapp.data.local.TaskDao
import com.example.todolistapp.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.todolistapp.model.Task
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.nowMillisString

class TaskRepository private constructor(
    private val dao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun insert(task: Task): Long {
        val now = nowMillisString()
        return dao.insert(
            TaskEntity(
                id = 0,
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

    suspend fun delete(task: Task) {
        task.id?.let { dao.deleteById(it) }
    }

    suspend fun setStatus(id: Long, status: TaskStatus) {
        dao.updateStatus(id, status, nowMillisString())
    }

    suspend fun setDueDate(id: Long, due: String?) {
        dao.updateDueDate(id, due, nowMillisString())
    }

    companion object {
        @Volatile private var _inst: TaskRepository? = null

        fun getInstance(context: Context): TaskRepository =
            _inst ?: synchronized(this) {
                val dao = AppDatabase.getInstance(context).taskDao()
                TaskRepository(dao).also { _inst = it }
            }
    }
}

private fun TaskEntity.toModel(): Task =
    Task(
        id = id,
        label = label,
        description = description,
        status = status,
        type = type,
        createdAt = createdAt,
        updatedAt = updatedAt,
        dueDate = dueDate
    )
