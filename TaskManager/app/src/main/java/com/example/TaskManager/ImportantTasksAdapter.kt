
package com.example.TaskManager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
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

@Suppress("DEPRECATION")
class ImportantTasksAdapter (var taskList : List<Task>) : RecyclerView.Adapter<ImportantTasksAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.important_item ,parent , false)
        return todoViewHolder(view)
    }

    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {

            val letter = findViewById<TextView>(R.id.tvRCLetter)
            val note = findViewById<TextView>(R.id.tvRCnote)
            val date = findViewById<TextView>(R.id.tvRCdate)
            val view = findViewById<View>(R.id.RCview)
            letter.setText(taskList[position].importance.toString())
            note.setText(taskList[position].toDo.toString())
            date.setText(taskList[position].date.toString())
            note.paint.isStrikeThruText = taskList[position].done!!
            CoroutineScope(Dispatchers.IO).launch {
                val clr = MyApplication.database.categoryDao().getCategoryColorById(taskList[position].categoryId)
                withContext(Dispatchers.Main) {
                    view.setBackgroundColor(clr)
                }
            }
            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
            val dueDate = LocalDate.parse(taskList[position].date, formatter)
            val task = taskList[position]
            val container = findViewById<ConstraintLayout>(R.id.RCcontainer)
            if(task.calculateImportance() == -1.0) {
                val shapeDrawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rounded)
                shapeDrawable?.let {
                    it.setColorFilter(Color.parseColor("#930000"), PorterDuff.Mode.SRC_IN)
                }
                container.background = shapeDrawable

            }else {
                if (dueDate == LocalDate.now()) {

                    val shapeDrawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rounded)
                    shapeDrawable?.let {
                        it.setColorFilter(Color.parseColor("#FfA500"), PorterDuff.Mode.SRC_IN)
                    }
                    container.background = shapeDrawable
                } else {
                    val shapeDrawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rounded2)
                    container.background = shapeDrawable
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

