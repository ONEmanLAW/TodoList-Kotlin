package com.example.todolistapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.TaskStatus

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insert(entity: TaskEntity): Long
    @Update
    suspend fun update(entity: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: TaskStatus, updatedAt: String)

    @Query("UPDATE tasks SET dueDate = :dueDate, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDueDate(id: Long, dueDate: String?, updatedAt: String)
}