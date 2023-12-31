package com.example.TaskManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class archive : AppCompatActivity() {
    private lateinit var taskAdapter: ArchiveAdapter
    lateinit var taskDao: TaskDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        val rvArchive = findViewById<RecyclerView>(R.id.rvArchive)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")

        rvArchive.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch {
            taskDao = MyApplication.database.taskDao()
            val today = LocalDate.now()

            var filteredTasks = taskDao.getAllTasks().filter {
                (it.done == true && LocalDate.parse(it.date , dateFormatter)
                    .isBefore(today)) || LocalDate.parse(it.date , dateFormatter).isBefore(today.minusDays(7))
            }
            try {
                taskAdapter = ArchiveAdapter(filteredTasks, this@archive)
            } catch (e:Exception) {
                println(e)
            }
            rvArchive.adapter = taskAdapter
        }
    }
}