package com.example.TaskManager

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

class CategoriesAdapter(
    private var categoryList : List<Category>,
    private val categoryDeleteListener: CategoryDeleteListener,
    private val categoryItemClickListener: CategoryItemClickListener
) : RecyclerView.Adapter<CategoriesAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item ,parent , false)
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

            val category = categoryList[position]
            holder.itemView.setOnClickListener {
                categoryItemClickListener.onCategoryItemClicked(category , color)
            }

            val btnRemoveCategory = findViewById<Button>(R.id.btnCATdelete)
            btnRemoveCategory.setOnClickListener {
                val currentCategory = categoryList[position]
                CoroutineScope(Dispatchers.IO).launch {
                    MyApplication.database.categoryDao().deleteCategory(currentCategory)

                    val updatedCategories = MyApplication.database.categoryDao().getAllCategories()
                    MyApplication.database.taskDao().deleteTasksForCategory(currentCategory.id)
                    withContext(Dispatchers.Main) {
                        categoryList = updatedCategories
                        notifyDataSetChanged()
                        categoryDeleteListener.onCategoryDeleted() // Notify MainActivity
                    }
                }
            }

            val tvNum = findViewById<TextView>(R.id.tvCATtaskNumber)
            CoroutineScope(Dispatchers.IO).launch {
                val num = MyApplication.database.taskDao().getTasksForCategory(categoryList[position].id).filter { it.done == false }.size
                if (num == 1) {
                    tvNum.text =  "1 Task"

                }
                withContext(Dispatchers.Main) {
                    tvNum.text = num.toString() + " Tasks"
                }
            }
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
    fun updateCategories(newCategories: List<Category>) {
        categoryList = newCategories
        notifyDataSetChanged()
    }

}