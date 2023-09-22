package com.example.TaskManager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var newTaskPopup: PopupWindow
    private lateinit var gestureDetector: GestureDetectorCompat
    val REQUEST_CODE_ADD_CATEGORY = 1 // Define a request code
    private lateinit var spinnerCategory: Spinner // Declare it here
    var categoryNamesList: List<String> = listOf()
    private lateinit var taskAdapter: ImportantTasksAdapter

    override fun onResume() {
        super.onResume()

        // Update your data or re-fetch from the database if needed
        loadTasksIntoRecyclerView()
    }

    private fun loadTasksIntoRecyclerView() {
        val rvTasks = findViewById<RecyclerView>(R.id.rvImportantTasks)
        rvTasks.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskDao = MyApplication.database.taskDao()
                val filteredTasks = taskDao.getAllTasks()
                val uncompletedTasks = filteredTasks.sortedByDescending { it.calculateImportance() }
                    .filter { !it.done!! }.take(5)

                withContext(Dispatchers.Main) {
                    taskAdapter = ImportantTasksAdapter(uncompletedTasks)
                    rvTasks.adapter = taskAdapter
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gestureDetector = GestureDetectorCompat(this, MyGestureListener()) // Initialize here
        var selectedImportance = 'A'
        var newName : String
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        var selectedDate = LocalDate.now().format(formatter)
        val btnAddTask: FloatingActionButton = findViewById(R.id.btnAddTask)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.new_task, null)
        var category = 0
        var categories:List<Category>
        var text : String = "test"



        // Create a notification channel (optional)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                "IDhehe",
                "NOTIFY",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        loadTasksIntoRecyclerView()
        // Initialize the new task popup
        newTaskPopup = PopupWindow(
            popupView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val addTaskBlock = popupView.findViewById<ConstraintLayout>(R.id.clNewTask)
        val animationBlock = popupView.findViewById<ConstraintLayout>(R.id.importanceAnimation)
        // Set click listener for the "Add Category" button
        btnAddTask.setOnClickListener {
            lifecycle.apply {
                CoroutineScope(Dispatchers.IO).launch {
                     if(        MyApplication.database.categoryDao().getAllCategories().isEmpty()) {
                         withContext(Dispatchers.Main) {
                             Toast.makeText(this@MainActivity , "You don't have a category, swipe to make one" , Toast.LENGTH_LONG).show()
                         }
                     } else {
                         withContext(Dispatchers.Main) {
                             val slideAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slid_up)

                             val fadeInAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_in)
                             // Apply the animation to the view
                             addTaskBlock.startAnimation(slideAnimation)
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
                             // Apply the animation to the view
                             addTaskBlock.startAnimation(slideAnimation)

                             newTaskPopup.showAtLocation(it, Gravity.CENTER, 0, 0)
                         }
                     }
                }
            }
        }
        // Set click listener for the "Add Category" button

        // Find and set click listener for the "CONFIRM" button inside the popup


        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"))
        val editTextDate: EditText =popupView.findViewById(R.id.etNTdeadline)
        editTextDate.setText(date)
        val calendar = Calendar.getInstance()

        editTextDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                 selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                editTextDate.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        val spinnerImportance: Spinner = popupView.findViewById(R.id.spinnerNTimportance)
         spinnerCategory = popupView.findViewById(R.id.spinnerNTcategory)

        // Create an array of importance letters
        val importanceLetters = listOf('A', 'B', 'C', 'D')
        lifecycle.apply {
            CoroutineScope(Dispatchers.IO).launch {
                categories = MyApplication.database.categoryDao().getAllCategories()
                categoryNamesList = categories.map { it.name }
                withContext(Dispatchers.Main) {

                    val spinnerAdapter1 = categorySpinnerAdapter(this@MainActivity,categoryNamesList)
                    spinnerCategory.adapter = spinnerAdapter1
                }
            }
        }
        val spinnerAdapter = ImportanceSpinnerAdapter(this@MainActivity,importanceLetters)
        spinnerImportance.adapter = spinnerAdapter
        spinnerImportance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedImportance = importanceLetters[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val catName = categoryNamesList[position]

                lifecycle.apply {
                    CoroutineScope(Dispatchers.IO).launch {
                        category = MyApplication.database.categoryDao().getCategoryIdByName(catName)!!
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        val btnNTConfirm: Button = popupView.findViewById(R.id.btnNTconfirm)
        btnNTConfirm.setOnClickListener {

            val etTodo = popupView.findViewById<EditText>(R.id.etNTname)
            println(etTodo.text.toString())
            newName = etTodo.text.toString()

            val newTask = Task(
                toDo = newName,
                categoryId = category,
                importance = selectedImportance,
                date = selectedDate,
                done = false,
            )
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        MyApplication.database.taskDao().insert(newTask)
                        val filteredTasks = MyApplication.database.taskDao().getTasksForCategory(category)
                        withContext(Dispatchers.Main) {
                            loadTasksIntoRecyclerView()
                        }
                    } catch (e: Exception) {
                        println(e)
                        e.printStackTrace() // Print the exception details to Logcat
                    }
                }
                newTaskPopup.dismiss()
            }
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val filteredTasks = MyApplication.database.taskDao().getAllTasks()
                        val uncompletedTasks = filteredTasks.sortedByDescending { it.calculateImportance() }.filter { it.done == false }
                        text = uncompletedTasks[0].toDo.toString()
                        withContext(Dispatchers.Main) {
                            val serviceIntent = Intent(this@MainActivity, YourForegroundService::class.java)
                            serviceIntent.putExtra("notificationText", text)
                            ContextCompat.startForegroundService(this@MainActivity, serviceIntent)
                        }
                    } catch (e: Exception) {
                        println(e)
                        e.printStackTrace() // Print the exception details to Logcat
                    }
                }
                newTaskPopup.dismiss()
            }
        }


        lifecycleScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val filteredTasks = MyApplication.database.taskDao().getAllTasks()
                    val uncompletedTasks = filteredTasks.sortedByDescending { it.calculateImportance() }.filter { it.done == false }
                    text = uncompletedTasks[0].toDo.toString()
                    withContext(Dispatchers.Main) {
                        val serviceIntent = Intent(this@MainActivity, YourForegroundService::class.java)
                        serviceIntent.putExtra("notificationText", text)
                        ContextCompat.startForegroundService(this@MainActivity, serviceIntent)
                    }
                } catch (e: Exception) {
                    println(e)
                    e.printStackTrace() // Print the exception details to Logcat
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

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
                }
                else {
                    val intent = Intent(this@MainActivity,categories::class.java)
                    startActivityForResult(intent, REQUEST_CODE_ADD_CATEGORY)                }
                return true
            }
            return false
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_CATEGORY && resultCode == Activity.RESULT_OK) {
            val categoryAdded = data?.getBooleanExtra("category_added", false) ?: false
            if (categoryAdded) {
                // Update the category spinner here
                updateCategorySpinner()
            }
        }
    }

     fun updateCategorySpinner() {

        lifecycle.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val categories = MyApplication.database.categoryDao().getAllCategories()
                 categoryNamesList = categories.map { it.name }
                withContext(Dispatchers.Main) {

                    val spinnerAdapter = categorySpinnerAdapter(this@MainActivity, categoryNamesList)
                    spinnerCategory.adapter = spinnerAdapter // Use the class-level spinnerCategory
                }
            }
        }
    }
}
