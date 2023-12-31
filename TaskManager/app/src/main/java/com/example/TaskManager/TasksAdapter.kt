package com.example.TaskManager

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import java.util.Calendar

class TasksAdapter(
    var taskList: List<Task>,
    val context: Context,
    private val categoryDeleteListener: CategoryDeleteListener
) : RecyclerView.Adapter<TasksAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_category_item, parent, false)
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
            note.paint.isStrikeThruText = taskList[position].done!!
            val khalfia = findViewById<ConstraintLayout>(R.id.test)
            val today = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")
            var currentTask = taskList[position]

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
            val dueDate = LocalDate.parse(taskList[position].date, formatter)
            val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), dueDate)
            val task = taskList[position]
//            if (taskList[position].done == true) {
//                khalfia.setBackgroundColor(Color.GREEN)
//            } else if (task.calculateImportance() == -1.0) {
//                khalfia.setBackgroundColor(Color.parseColor("#930000")) // Overdue tasks
//            } else if (dueDate == LocalDate.now()) {
//                khalfia.setBackgroundColor(Color.parseColor("#FfA500"))
//            }


// Assuming you have a context variable, such as 'context'
            val typedValue = TypedValue()
            val typedValue2 = TypedValue()
            val theme = context.theme

// Resolve the attribute to obtain its value
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorPrimaryContainer,
                typedValue,
                true
            )
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnPrimaryContainer,
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
                date.setTextColor(Color.BLACK)
                letter.setTextColor(Color.BLACK)
            } else {
                khalfia.setBackgroundColor(backgroundColor)
                note.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
                date.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
                letter.setTextColor(colorOnPrimaryContainer) // Set text color to white for tasks with a colored background
            }
            val dateString =
                if (daysRemaining in 1..6) (daysRemaining + 1).toString() + " Days left!" else if (daysRemaining.toInt() == 0) "1 Day left!" else taskList[position].date.toString()
            date.setText(dateString)
            btnDone.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedDoneStatus = !(taskList[position].done)!!

                    MyApplication.database.taskDao()
                        .updateTaskDoneStatus(currentTask.id, updatedDoneStatus)
                    val filteredTasks =
                        MyApplication.database.taskDao().getTasksForCategory(currentTask.categoryId).filterNot {
                            (it.done == true && LocalDate.parse(it.date , dateFormatter)
                                .isBefore(today)) || LocalDate.parse(it.date , dateFormatter).isBefore(today.minusDays(7))
                        }.sortedByDescending { it.calculateImportance() }
                    val filteredTasks2 = MyApplication.database.taskDao().getAllTasks()
                    val uncompletedTasks =
                        filteredTasks2.sortedByDescending { it.calculateImportance() }
                            .filter { it.done == false }

                    val category = MyApplication.database.categoryDao()
                        .getCategoryNameById(uncompletedTasks[0].categoryId)
                    var text = uncompletedTasks[0].toDo.toString()
                    withContext(Dispatchers.Main) {
                        categoryDeleteListener.onCategoryDeleted()
                        val serviceIntent = Intent(context, YourForegroundService::class.java)
                        serviceIntent.putExtra("notificationText", text)
                        serviceIntent.putExtra("notificationCategory", category?.uppercase())
                        ContextCompat.startForegroundService(context, serviceIntent)
                        updateTasks(filteredTasks)

                    }
                }
            }
            btnRemove.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    MyApplication.database.taskDao().deleteTask(currentTask)
                    val filteredTasks =
                        MyApplication.database.taskDao().getTasksForCategory(currentTask.categoryId)
                    val filteredTasks2 = MyApplication.database.taskDao().getAllTasks()
                    val uncompletedTasks =
                        filteredTasks2.sortedByDescending { it.calculateImportance() }
                            .filter { it.done == false }

                    var text = uncompletedTasks[0].toDo.toString()

                    val category = MyApplication.database.categoryDao()
                        .getCategoryNameById(uncompletedTasks[0].categoryId)
                    withContext(Dispatchers.Main) {
                        taskList = filteredTasks.filterNot {
                            (it.done == true && LocalDate.parse(it.date , dateFormatter)
                                .isBefore(today)) || LocalDate.parse(it.date , dateFormatter).isBefore(today.minusDays(7))
                        }.sortedByDescending { it.calculateImportance() }
                        notifyDataSetChanged()
                        val serviceIntent = Intent(context, YourForegroundService::class.java)
                        serviceIntent.putExtra("notificationText", text)
                        serviceIntent.putExtra("notificationCategory", category?.uppercase())
                        ContextCompat.startForegroundService(context, serviceIntent)
                    }
                }
            }

            val btnEdit = findViewById<ImageButton>(R.id.btnRCCedit)
            var selectedImportance = taskList[position].importance
            var newName: String
            var selectedDate = taskList[position].date
            var text = "test"
            btnEdit.setOnClickListener {
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.new_category_task, null)
                // Initialize the new task popup
                var newTaskPopup = PopupWindow(
                    popupView,
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    true
                )
                val addTaskInCategoryBlock =
                    popupView.findViewById<ConstraintLayout>(R.id.clNewTaskInCategory)
                val animationBlock = popupView.findViewById<ConstraintLayout>(R.id.clAnimation)
                // Set click listener for the "Add Category" button
                val slideAnimation = AnimationUtils.loadAnimation(context, R.anim.slid_up)

                val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                // Apply the animation to the view
                addTaskInCategoryBlock.startAnimation(slideAnimation)
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

                println("wesh a lhaj")
                val dateEditText = popupView.findViewById<EditText>(R.id.etNTCdeadline)
                dateEditText.setText(taskList[position].date)
                val spinnerImportance: Spinner = popupView.findViewById(R.id.spinnerNTCimportance)

// Create an array of importance letters
                val importanceLetters = listOf('A', 'B', 'C', 'D')
                val spinnerAdapter = ImportanceSpinnerAdapter(context, importanceLetters)
                spinnerImportance.adapter = spinnerAdapter
                val index = importanceLetters.indexOf(taskList[position].importance)
                spinnerImportance.setSelection(index)
                val editTextDate: EditText = popupView.findViewById(R.id.etNTCdeadline)
                val calendar = Calendar.getInstance()
                val tvTask: EditText = popupView.findViewById(R.id.etNTCname)
                tvTask.setText(taskList[position].toDo)
                editTextDate.setOnClickListener {
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val datePickerDialog =
                        DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
                            selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                            editTextDate.setText(selectedDate)
                        }, year, month, day)

                    datePickerDialog.show()
                }
                spinnerImportance.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedImportance = importanceLetters[position]
                            // Use the selectedImportance as needed
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Do something when nothing is selected
                        }
                    }
                val btnNTConfirm: Button = popupView.findViewById(R.id.btnNTCconfirm)
                btnNTConfirm.setOnClickListener {
                    newName = tvTask.text.toString()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            MyApplication.database.taskDao()
                                .updateTaskDate(taskList[position].id, selectedDate.toString())
                            MyApplication.database.taskDao()
                                .updateTaskName(taskList[position].id, newName)
                            MyApplication.database.taskDao()
                                .updateTaskImportance(taskList[position].id, selectedImportance!!)
                            var filteredTasks = MyApplication.database.taskDao().getAllTasks()
                            val uncompletedTasks =
                                filteredTasks.sortedByDescending { it.calculateImportance() }
                                    .filter { it.done == false }
                            text = uncompletedTasks[0].toDo.toString()

                            val category = MyApplication.database.categoryDao()
                                .getCategoryNameById(uncompletedTasks[0].categoryId)
                            withContext(Dispatchers.Main) {
                                val serviceIntent =
                                    Intent(context, YourForegroundService::class.java)
                                serviceIntent.putExtra("notificationText", text)
                                serviceIntent.putExtra(
                                    "notificationCategory",
                                    category?.uppercase()
                                )
                                ContextCompat.startForegroundService(
                                    context,
                                    serviceIntent
                                )
                            }
                            filteredTasks =
                                MyApplication.database.taskDao().getTasksForCategory(taskList[position].categoryId)
                            withContext(Dispatchers.Main) {
                                updateTasks(filteredTasks.sortedByDescending { it.calculateImportance() })
                                newTaskPopup.dismiss()
                            }
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
        return taskList.size
    }

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }

}

