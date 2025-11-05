package com.example.todolistapp.data.local

import androidx.room.TypeConverter
import com.example.todolistapp.model.TaskStatus
import com.example.todolistapp.model.TaskType

class Converters {
    @TypeConverter fun fromStatus(s: TaskStatus) = s.name
    @TypeConverter fun toStatus(s: String) = TaskStatus.valueOf(s)

    @TypeConverter fun fromType(t: TaskType) = t.name
    @TypeConverter fun toType(s: String) = TaskType.valueOf(s)
}