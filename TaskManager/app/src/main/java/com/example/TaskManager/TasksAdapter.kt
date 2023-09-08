
package com.example.TaskManager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TasksAdapter (var taskList : List<Task> ,
                    private val categoryDeleteListener: CategoryDeleteListener
                    ) : RecyclerView.Adapter<TasksAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_category_item ,parent , false)
        return todoViewHolder(view)
    }

    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {

            val letter = findViewById<TextView>(R.id.tvRCCLetter)
            val note = findViewById<TextView>(R.id.tvRCCnote)
            val date = findViewById<TextView>(R.id.tvRCCdate)
            val btnDone = findViewById<ImageButton>(R.id.btnRCCdone)
            val btnRemove = findViewById<Button>(R.id.btnRCCdelete)
            letter.setText(taskList[position].importance.toString())
            note.setText(taskList[position].toDo.toString())
            date.setText(taskList[position].date.toString())
            note.paint.isStrikeThruText = taskList[position].done!!
            println("ooof")
            val khalfia = findViewById<ConstraintLayout>(R.id.test)

            var currentTask = taskList[position]

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
            val dueDate = LocalDate.parse(taskList[position].date, formatter)
            val task = taskList[position]
            val backgroundColor = if(task.calculateImportance() == -1.0) {
                Color.parseColor("#930000") // Overdue tasks
            }else {
                if (dueDate == LocalDate.now()) {
                    Color.parseColor("#FfA500")
                } else {
                    Color.parseColor("#9CFFFFFF")
                }
            }
            if (taskList[position].done == true) {
                khalfia.setBackgroundColor(Color.GREEN)
            }else {
                khalfia.setBackgroundColor(backgroundColor)
            }
            btnDone.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedDoneStatus = !currentTask.done!!

                    MyApplication.database.taskDao().updateTaskDoneStatus(currentTask.id, updatedDoneStatus)
                    val filteredTasks = MyApplication.database.taskDao().getTasksForCategory(currentTask.categoryId)
                    val filteredTasks2 = MyApplication.database.taskDao().getAllTasks()
                    val uncompletedTasks = filteredTasks2.sortedByDescending { it.calculateImportance() }.filter { it.done == false }
                    var text = uncompletedTasks[0].toDo.toString()
                    withContext(Dispatchers.Main) {
                        updateTasks(filteredTasks.sortedByDescending { it.calculateImportance() })
                        categoryDeleteListener.onCategoryDeleted()
                        val serviceIntent = Intent(context, YourForegroundService::class.java)
                        serviceIntent.putExtra("notificationText", text)
                        ContextCompat.startForegroundService(context, serviceIntent)

                    }
                }
            }
            btnRemove.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    MyApplication.database.taskDao().deleteTask(currentTask)
                    val filteredTasks = MyApplication.database.taskDao().getTasksForCategory(currentTask.categoryId)
                    val filteredTasks2 = MyApplication.database.taskDao().getAllTasks()
                    val uncompletedTasks = filteredTasks2.sortedByDescending { it.calculateImportance() }.filter { it.done == false }
                    var text = uncompletedTasks[0].toDo.toString()
                    withContext(Dispatchers.Main) {
                        taskList = filteredTasks.sortedByDescending { it.calculateImportance() }
                        notifyDataSetChanged()
                        val serviceIntent = Intent(context, YourForegroundService::class.java)
                        serviceIntent.putExtra("notificationText", text)
                        ContextCompat.startForegroundService(context, serviceIntent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }

}

