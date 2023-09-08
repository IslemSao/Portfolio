package com.example.TaskManager

import androidx.room.Database
import androidx.room.RoomDatabase

// AppDatabase.kt

@Database(entities = [Category::class, Task::class], version = 1)

abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
}
