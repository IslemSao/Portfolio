package com.example.Note

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Int,
    val content : String,
    val date : String
)
