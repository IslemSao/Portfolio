package com.example.TaskManager

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Context
import android.util.TypedValue

class ArchiveAdapter(
    var taskList: List<Task>,
    val context: Context
) : RecyclerView.Adapter<ArchiveAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.archive_item, parent, false)
        return todoViewHolder(view)
    }

    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {

            val letter = findViewById<TextView>(R.id.tvARLetter)
            val note = findViewById<TextView>(R.id.tvARnote)
            val date = findViewById<TextView>(R.id.tvARdate)
            val category = findViewById<TextView>(R.id.tvARcategory)
            val btnRemove = findViewById<Button>(R.id.btnARdelete)
            letter.setText(taskList[position].importance.toString())
            CoroutineScope(Dispatchers.IO).launch {
                val cat = MyApplication.database.categoryDao().getCategoryNameById(taskList[position].categoryId)
                withContext(Dispatchers.Main) {
                category.setText(cat)
                }
            }
            date.setText(taskList[position].date.toString())
            note.setText(taskList[position].toDo.toString())
            note.paint.isStrikeThruText = taskList[position].done!!
            val khalfia = findViewById<ConstraintLayout>(R.id.test2)

            var currentTask = taskList[position]
            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
            val dueDate = LocalDate.parse(taskList[position].date, formatter)
            val task = taskList[position]

            // Assuming you have a context variable, such as 'context'
            val typedValue = TypedValue()
            val typedValue2 = TypedValue()
            val theme = context.theme

            // Resolve the attribute to obtain its value
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnSecondaryContainer,
                typedValue,
                true
            )
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnSecondaryContainer,
                typedValue2,
                true
            )

            // Now, you can access the color using typedValue.data
            val colorPrimaryContainer = typedValue.data
            val colorOnPrimaryContainer = typedValue2.data
            val backgroundColor = if (task.calculateImportance() == -1.0) {
                Color.parseColor("#C40202") // Overdue tasks
            } else {
                if (dueDate == LocalDate.now()) {
                    Color.parseColor("#FF791F")
                } else {
                    colorPrimaryContainer
                }
            }

            if (taskList[position].done == true) {
                khalfia.setBackgroundColor(Color.parseColor("#00D447"))
                note.setTextColor(Color.BLACK)
                category.setTextColor(Color.BLACK)
                date.setTextColor(Color.BLACK)
                letter.setTextColor(Color.BLACK)
            } else {
                khalfia.setBackgroundColor(backgroundColor)
                note.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
                category.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
                date.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
                letter.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
            }

            btnRemove.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    MyApplication.database.taskDao().deleteTask(currentTask)
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")
                    val today = LocalDate.now()
                    var filteredTasks =    MyApplication.database.taskDao().getAllTasks().filter {
                        (it.done == true && LocalDate.parse(it.date , dateFormatter)
                            .isBefore(today)) || LocalDate.parse(it.date , dateFormatter).isBefore(today.minusDays(7))
                    }
                            .filter { it.done == false }
                    withContext(Dispatchers.Main) {
                        taskList = filteredTasks.sortedByDescending { it.calculateImportance() }
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}

