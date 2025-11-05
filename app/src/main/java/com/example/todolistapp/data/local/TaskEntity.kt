package com.example.todolistapp.data.local

import androidx.room.Entity
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.TaskType
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val label: String,
    val description: String,
    val status: TaskStatus,
    val type: TaskType,
    val createdAt: String,
    val updatedAt: String,
    val dueDate: String?
)