package com.example.TaskManager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog

class CategoriesAdapter(
    private var categoryList: List<Category>,
    private val categoryDeleteListener: CategoryDeleteListener,
    private val categoryItemClickListener: CategoryItemClickListener,
    private val ctx: Context,
) : RecyclerView.Adapter<CategoriesAdapter.todoViewHolder>() {
    private lateinit var newTaskPopup: PopupWindow

    inner class todoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return todoViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {

            val tvName = findViewById<TextView>(R.id.CATcategory)
            tvName.setText(categoryList[position].name)
            val container = findViewById<ConstraintLayout>(R.id.clCATcontainer)
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
                categoryItemClickListener.onCategoryItemClicked(category, color)
            }

            val btnRemoveCategory = findViewById<Button>(R.id.btnCATdelete)
            btnRemoveCategory.setOnClickListener {
                val currentCategory = categoryList[position]
                CoroutineScope(Dispatchers.IO).launch {
                    MyApplication.database.taskDao().deleteTasksForCategory(currentCategory.id)
                    MyApplication.database.categoryDao().deleteCategory(currentCategory)

                    val allCategories = MyApplication.database.categoryDao().getAllCategories()

// Sort the categories based on the number of incomplete tasks in each category
                    val updatedCategories = allCategories.sortedByDescending { category ->
                        val incompleteTaskCount =
                            MyApplication.database.taskDao().getTasksForCategory(category.id)
                                .count { it.done == false }
                        // Sorting is done in descending order, so categories with more incomplete tasks come first
                        incompleteTaskCount
                    }
                    withContext(Dispatchers.Main) {
                        categoryList = updatedCategories
                        notifyDataSetChanged()
                        categoryDeleteListener.onCategoryDeleted() // Notify MainActivity
                    }
                }
            }

            val tvNum = findViewById<TextView>(R.id.tvCATtaskNumber)
            CoroutineScope(Dispatchers.IO).launch {
                val num =
                    MyApplication.database.taskDao().getTasksForCategory(categoryList[position].id)
                        .filter { it.done == false }.size
                if (num == 1) {
                    tvNum.text = "1 Task"

                }
                withContext(Dispatchers.Main) {
                    tvNum.text = num.toString() + " Tasks"
                }
            }

            val btnEdit = findViewById<ImageButton>(R.id.btnCATedit)
            btnEdit.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            btnEdit.setOnClickListener {
                val inflater =
                    ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.new_cetegories, null)
                // Initialize the new task popup
                newTaskPopup = PopupWindow(
                    popupView,
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    true
                )
                val addCategoryBlock = popupView.findViewById<ConstraintLayout>(R.id.clNewCategory)
                val animationBlock = popupView.findViewById<ConstraintLayout>(R.id.catAnimation)
                var clr: Int = categoryList[position].color
                var name: String
                val etName = popupView.findViewById<EditText>(R.id.etNCname)
                etName.setText(categoryList[position].name)
                // Set click listener for the "Add Category" button
                val slideAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slid_up)

                val fadeInAnimation = AnimationUtils.loadAnimation(ctx, R.anim.fade_in)
                // Apply the animation to the view
                addCategoryBlock.startAnimation(slideAnimation)
                slideAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        animationBlock.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        animationBlock.startAnimation(fadeInAnimation)
                        animationBlock.visibility = View.VISIBLE
                    }
                })    // Apply the animation to the view
                // Show the new task popup
                newTaskPopup.showAtLocation(it, Gravity.CENTER, 0, 0)

                // Set click listener for the "Add Category" button
                val btnColor = popupView.findViewById<Button>(R.id.btnNCcolor)

                btnColor.setOnClickListener {

                    val initialColor = categoryList[position].color
                    val dialog = AmbilWarnaDialog(
                        ctx,
                        initialColor,
                        object : AmbilWarnaDialog.OnAmbilWarnaListener {
                            override fun onCancel(dialog: AmbilWarnaDialog?) {
                                // Handle cancel
                            }

                            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                                clr = color
                                // Handle selected color
                            }
                        }
                    )

                    dialog.show()
                }


                // Find and set click listener for the "CONFIRM" button inside the popup
                val btnNTConfirm: Button = popupView.findViewById(R.id.btnNCconfirm)

                btnNTConfirm.setOnClickListener {
                    // Handle the new task creation here
                    // You can access the views inside the popupView to get user input
                    // Close the popup after confirming

                    val etName = popupView.findViewById<EditText>(R.id.etNCname)
                    name = etName.text.toString()

                    // Use Kotlin Coroutine to perform database operation asynchronously
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            if (MyApplication.database.categoryDao().getAllCategories()
                                    .any { it.name == name && it.id != categoryList[position].id }
                            ) {
                                withContext(Dispatchers.Main) {
                                    etName.text.clear()
                                    etName.error = "This category already excite!"
                                }
                            } else {
                                val updatedCategory = Category(
                                    id = categoryList[position].id,
                                    name = name,
                                    color = clr
                                )
                                MyApplication.database.categoryDao().updateCategory(updatedCategory)

                                val allCategories =
                                    MyApplication.database.categoryDao().getAllCategories()

                                // Sort the categories based on the number of incomplete tasks in each category
                                val updatedCategories =
                                    allCategories.sortedByDescending { category ->
                                        val incompleteTaskCount =
                                            MyApplication.database.taskDao()
                                                .getTasksForCategory(category.id)
                                                .count { it.done == false }
                                        // Sorting is done in descending order, so categories with more incomplete tasks come first
                                        incompleteTaskCount
                                    }

                                // Update the UI on the main thread
                                withContext(Dispatchers.Main) {
                                    updateCategories(updatedCategories)
                                }
                                withContext(Dispatchers.Main) {
                                    newTaskPopup.dismiss()
                                    etName.text.clear()
                                }
                            }
                            categoryDeleteListener.onCategoryDeleted() // Notify MainActivity
                        } catch (e: Exception) {
                            println(e)
                            e.printStackTrace() // Print the exception details to Logcat
                        }
                    }
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