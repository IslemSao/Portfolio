package com.example.Note
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// CategoryDao.kt

@Dao
interface noteDao {
    @Insert
    fun insert(note: Note)

    @Query("SELECT * FROM Note")
    fun getAllCategories(): List<Note>

    @Delete
    suspend fun deleteCategory(note: Note)

    @Query("SELECT id FROM Note WHERE name = :noteName")
    fun getCategoryIdByName(noteName: String): Int?
    @Query("SELECT color FROM Note WHERE id = :noteId")
    fun getCategoryColorById(noteId: Int): Int
    // Add other
    @Update
    fun updateNoteContent(note: Note)
}
