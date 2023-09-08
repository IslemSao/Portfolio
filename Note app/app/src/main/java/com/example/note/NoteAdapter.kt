package com.example.Note

import android.annotation.SuppressLint
import android.content.Intent
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

class NoteAdapter(
    private var categoryList : List<Note>,
    private val categoryItemClickListener:NoteItemClickListener

) : RecyclerView.Adapter<NoteAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item ,parent , false)
        return todoViewHolder(view)
    }

    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {

            val tvName = findViewById<TextView>(R.id.CATcategory)
            tvName.setText(categoryList[position].name)
            val  container = findViewById<ConstraintLayout>(R.id.clCATcontainer)
            val tvNUM = findViewById<TextView>(R.id.tvCATtaskNumber)
            container.setBackgroundColor(categoryList[position].color)
            val brightness = calculateColorBrightness(categoryList[position].color)
            var color = 0
            if (brightness < 128) {
                // Color is dark, set text color to white
                color = Color.WHITE
            } else {
                // Color is light, set text color to black
                color = Color.BLACK
            }


            tvNUM.setTextColor(color)
            tvName.setTextColor(color)

            val note = categoryList[position]
            holder.itemView.setOnClickListener {
                categoryItemClickListener.onCategoryItemClicked(note , color)
            }

            val btnRemoveCategory = findViewById<Button>(R.id.btnCATdelete)
            btnRemoveCategory.setOnClickListener {
                val currentCategory = categoryList[position]
                CoroutineScope(Dispatchers.IO).launch {
                    MyApplication.database.noteDao().deleteCategory(currentCategory)

                    val updatedCategories = MyApplication.database.noteDao().getAllCategories()
                    withContext(Dispatchers.Main) {
                        categoryList = updatedCategories
                        notifyDataSetChanged()
                    }
                }
            }

            val tvNum = findViewById<TextView>(R.id.tvCATtaskNumber)
            tvNum.text = categoryList[position].date
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    fun calculateColorBrightness(color: Int): Double {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return 0.299 * red + 0.587 * green + 0.114 * blue
    }
    fun updateCategories(newCategories: List<Note>) {
        categoryList = newCategories
        notifyDataSetChanged()
    }

}