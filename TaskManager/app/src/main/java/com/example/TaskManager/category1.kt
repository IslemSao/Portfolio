package com.example.TaskManager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class category1 : AppCompatActivity()  , CategoryDeleteListener{
    private lateinit var newTaskPopup: PopupWindow
    private lateinit var taskAdapter: TasksAdapter
    lateinit var categoryDao :CategoryDao
    lateinit var taskDao :TaskDao



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category1)

        val rvTasks = findViewById<RecyclerView>(R.id.rvTask1)
        rvTasks.layoutManager = LinearLayoutManager(this)
        val name = intent.getStringExtra("NAME")
        val background = intent.getIntExtra("BACKGROUND", 0)
        val color = intent.getIntExtra("COLOR", 0)
        val id = intent.getIntExtra("ID" , 0)
        val categoryNameTextView: TextView = findViewById(R.id.tvCategory1)
        var selectedImportance = 'A'
        var newName : String
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        var selectedDate =LocalDate.now().format(formatter)
        var text = "test"
        GlobalScope.launch {
            categoryDao = MyApplication.database.categoryDao()
             taskDao = MyApplication.database.taskDao()
            val category = categoryDao.getAllCategories().find { it.name == name && it.color == background }
            var filteredTasks = taskDao.getTasksForCategory(category!!.id).sortedByDescending { it.calculateImportance() }

            taskAdapter = TasksAdapter( filteredTasks , this@category1)
            rvTasks.adapter = taskAdapter

        }


        categoryNameTextView.text = name
        val cat1: ConstraintLayout = findViewById(R.id.cat1)

        cat1.setBackgroundColor(background)
        categoryNameTextView.setTextColor(color)


        val btnAddTask: FloatingActionButton = findViewById(R.id.btnAdd1)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.new_category_task, null)

        // Initialize the new task popup
        newTaskPopup = PopupWindow(
            popupView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val addTaskInCategoryBlock = popupView.findViewById<ConstraintLayout>(R.id.clNewTaskInCategory)
        // Set click listener for the "Add Category" button
        btnAddTask.setOnClickListener {
            val fadeInAnimation = AnimationUtils.loadAnimation(this@category1, R.anim.fade_in)
            // Apply the animation to the view
            addTaskInCategoryBlock.startAnimation(fadeInAnimation)
            // Show the new task popup
            newTaskPopup.showAtLocation(it, Gravity.CENTER, 0, 0)
        }

        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"))
        val dateEditText = popupView.findViewById<EditText>(R.id.etNTCdeadline)
        dateEditText.setText(date)
        val spinnerImportance: Spinner = popupView.findViewById(R.id.spinnerNTCimportance)

// Create an array of importance letters
        val importanceLetters = listOf('A', 'B', 'C', 'D')
        val spinnerAdapter = ImportanceSpinnerAdapter(this,importanceLetters)
        spinnerImportance.adapter = spinnerAdapter




        val editTextDate: EditText = popupView.findViewById(R.id.etNTCdeadline)
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
        spinnerImportance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedImportance = importanceLetters[position]
                // Use the selectedImportance as needed
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when nothing is selected
            }
        }
        val btnNTConfirm: Button = popupView.findViewById(R.id.btnNTCconfirm)
        btnNTConfirm.setOnClickListener {
            val tvTask: EditText = popupView.findViewById(R.id.etNTCname)
            newName = tvTask.text.toString()

            val newTask = Task(
                toDo = newName,
                categoryId = id,
                importance = selectedImportance,
                date = selectedDate,
                done = false,
            )
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        taskDao.insert(newTask)
                        val intent = Intent()
                        intent.putExtra("cibon", true)
                        setResult(Activity.RESULT_OK, intent)
                        val filteredTasks = taskDao.getTasksForCategory(id)
                        withContext(Dispatchers.Main) {
                            taskAdapter.updateTasks(filteredTasks.sortedByDescending { it.calculateImportance() })
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
                        val uncompletedTasks =
                            filteredTasks.sortedByDescending { it.calculateImportance() }
                                .filter { it.done == false }
                        text = uncompletedTasks[0].toDo.toString()
                        println(text)
                        withContext(Dispatchers.Main) {
                            val serviceIntent =
                                Intent(this@category1, YourForegroundService::class.java)
                            serviceIntent.putExtra("notificationText", text)
                            ContextCompat.startForegroundService(this@category1, serviceIntent)
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
        intent.putExtra("updatedTaskCount", 1)
        setResult(Activity.RESULT_OK, intent)
    }

}
