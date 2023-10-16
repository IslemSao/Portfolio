package com.example.TaskManager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class categories : AppCompatActivity(), CategoryDeleteListener, CategoryItemClickListener {
    private lateinit var newTaskPopup: PopupWindow
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var categoriesAdapter: CategoriesAdapter
    lateinit var categoryDao: CategoryDao
    lateinit var addCategoryBlock: ConstraintLayout
    lateinit var taskDao: TaskDao


    //    val taskDao = MyApplication.database.taskDao()
    val REQUEST_CODE_TASK_VIEW = 2
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                val allCategories = categoryDao.getAllCategories()
                val updatedCategories = allCategories.sortedByDescending { category ->
                    val incompleteTaskCount = MyApplication.database.taskDao()
                        .getTasksForCategory(category.id).count { it.done == false }
                    // Sorting is done in descending order, so categories with more incomplete tasks come first
                    incompleteTaskCount
                }
                withContext(Dispatchers.Main) {
                    categoriesAdapter.updateCategories(updatedCategories)
                }
            }
        }

        // Update your data or re-fetch from the database if needed
        categoriesAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TASK_VIEW && resultCode == Activity.RESULT_OK) {
            val categoryAdded = data?.getBooleanExtra("cibon", false) ?: false
            if (categoryAdded) {
                // Update the category spinner here
                categoriesAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this)
        categoryDao = MyApplication.database.categoryDao()
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val allCategories = categoryDao.getAllCategories()

// Sort the categories based on the number of incomplete tasks in each category
                val sortedCategories = allCategories.sortedByDescending { category ->
                    val incompleteTaskCount =
                        MyApplication.database.taskDao().getTasksForCategory(category.id)
                            .count { it.done == false }
                    // Sorting is done in descending order, so categories with more incomplete tasks come first
                    incompleteTaskCount
                }
                categoriesAdapter = CategoriesAdapter(sortedCategories, this@categories, this@categories , this@categories)
                rvCategories.adapter = categoriesAdapter
            }
        }
        gestureDetector = GestureDetectorCompat(this, MyGestureListener()) // Initialize here
        setupAddTaskButton()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    @Suppress("DEPRECATION")
    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2?.x?.minus(e1?.x ?: 0f) ?: 0f

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    val intent = Intent(this@categories, MainActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_TASK_VIEW)
                } else {
                }
                return true
            }
            return false
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun setupAddTaskButton() {
        var name: String
        var clr: Int = 0

        val btnAddTask: FloatingActionButton = findViewById(R.id.btnAddCategory)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.new_cetegories, null)

        // Initialize the new task popup
        newTaskPopup = PopupWindow(
            popupView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        addCategoryBlock = popupView.findViewById(R.id.clNewCategory)
        val animationBlock = popupView.findViewById<ConstraintLayout>(R.id.catAnimation)
        // Set click listener for the "Add Category" button
        btnAddTask.setOnClickListener {
            val slideAnimation = AnimationUtils.loadAnimation(this@categories, R.anim.slid_up)

            val fadeInAnimation = AnimationUtils.loadAnimation(this@categories, R.anim.fade_in)
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
        }
        // Set click listener for the "Add Category" button
        val btnColor = popupView.findViewById<Button>(R.id.btnNCcolor)
        btnColor.setOnClickListener {

            val initialColor = Color.BLACK // Replace with your desired initial color

            val dialog = AmbilWarnaDialog(
                this,
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

            val newCategory = Category(name = name, color = clr)
            // Use Kotlin Coroutine to perform database operation asynchronously
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (categoryDao.getAllCategories().any { it.name == name }) {
                            withContext(Dispatchers.Main) {
                                etName.text.clear()
                                etName.error = "This category already excite!"
                            }
                        } else {
                            categoryDao.insert(newCategory)
                            val intent = Intent()
                            intent.putExtra("category_added", true)
                            setResult(Activity.RESULT_OK, intent)
                            val allCategories = categoryDao.getAllCategories()

// Sort the categories based on the number of incomplete tasks in each category
                            val updatedCategories = allCategories.sortedByDescending { category ->
                                val incompleteTaskCount = MyApplication.database.taskDao()
                                    .getTasksForCategory(category.id).count { it.done == false }
                                // Sorting is done in descending order, so categories with more incomplete tasks come first
                                incompleteTaskCount
                            }

                            // Update the UI on the main thread
                            withContext(Dispatchers.Main) {
                                categoriesAdapter.updateCategories(updatedCategories)
                            }
                            withContext(Dispatchers.Main) {
                                newTaskPopup.dismiss()
                                etName.text.clear()
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                        e.printStackTrace() // Print the exception details to Logcat
                    }
                }

            }
        }
    }

    override fun onCategoryDeleted() {
        val intent = Intent()
        intent.putExtra("category_added", true)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun onCategoryItemClicked(category: Category, color: Int) {
        val intent = Intent(this, category1::class.java)
        intent.putExtra("NAME", category.name)
        intent.putExtra("BACKGROUND", category.color)
        intent.putExtra("COLOR", color)
        intent.putExtra("ID", category.id)
        startActivityForResult(intent, 2) // Start the activity for result
    }
}