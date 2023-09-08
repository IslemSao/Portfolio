package com.example.Note

import androidx.room.Database
import androidx.room.RoomDatabase

// AppDatabase.kt

@Database(entities = [Note::class], version = 1)

abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): noteDao
}
