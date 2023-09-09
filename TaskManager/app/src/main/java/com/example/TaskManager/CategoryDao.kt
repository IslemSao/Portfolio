package com.example.TaskManager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// CategoryDao.kt

@Dao
interface CategoryDao {
    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM Category")
    fun getAllCategories(): List<Category>

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT id FROM Category WHERE name = :categoryName")
    fun getCategoryIdByName(categoryName: String): Int?

    @Query("SELECT color FROM Category WHERE id = :categoryId")
    fun getCategoryColorById(categoryId: Int): Int
}
