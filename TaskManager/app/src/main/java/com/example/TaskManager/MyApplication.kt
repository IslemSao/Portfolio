package com.example.TaskManager

import android.app.Application
import androidx.room.Room

// MyApplication.kt

class MyApplication : Application() {
    var catSpinner : MutableList<String> = mutableListOf()
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }
}
