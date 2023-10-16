package com.example.TaskManager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task)

    @Query("SELECT * FROM Task WHERE categoryId = :categoryId")
    fun getTasksForCategory(categoryId: Int): List<Task>

    @Query("SELECT * FROM Task")
    fun getAllTasks(): List<Task>

    @Query("UPDATE Task SET done = :done WHERE id = :taskId")
    suspend fun updateTaskDoneStatus(taskId: Int, done: Boolean)

    @Query("UPDATE Task SET toDo = :newName WHERE id = :taskId")
    suspend fun updateTaskName(taskId: Int, newName: String)

    @Query("UPDATE Task SET date = :newDate WHERE id = :taskId")
    suspend fun updateTaskDate(taskId: Int, newDate: String)

    @Query("UPDATE Task SET importance = :newImportance WHERE id = :taskId")
    suspend fun updateTaskImportance(taskId: Int, newImportance: Char)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM Task WHERE categoryId = :categoryId")
    suspend fun deleteTasksForCategory(categoryId: Int)


    // Add other methods
}

