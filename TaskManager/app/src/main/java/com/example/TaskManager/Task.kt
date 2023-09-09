package com.example.TaskManager

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TaskEntity.kt

@Entity(tableName = "Task")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var categoryId: Int,
    var toDo: String?,
    var importance: Char?,
    var date: String?,
    var done: Boolean?
) {

    fun calculateImportance(): Double {
        val importanceValues = mapOf('A' to 300.0, 'B' to 100.0, 'C' to 30.0, 'D' to 5.0)
        if (importance == null || date == null) {
            return 0.0 // Return a default value or handle the case based on your app's logic
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        val dueDate = LocalDate.parse(date, formatter)
        val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate).toDouble()

        val importanceValue = importanceValues[importance]
        if (importanceValue == null || daysLeft == 0.0) {
            return importanceValue!!.toDouble()
        }
        if (daysLeft < 0) {
            return -1.0
        }

        return importanceValue / daysLeft
    }
}
