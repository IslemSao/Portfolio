package com.example.Note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NoteItemClickListener {
    private lateinit var newTaskPopup: PopupWindow
    private lateinit var noteAdapter: NoteAdapter
    lateinit var noteDao: noteDao
    lateinit var new: List<Note>

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    noteDao = MyApplication.database.noteDao()
                    new = noteDao.getAllCategories()
                }
                withContext(Dispatchers.Main) {
                    noteAdapter.updateCategories(new)
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    noteDao = MyApplication.database.noteDao()

                } catch (e: Exception) {
                    println(e)
                }
            }
            noteAdapter = NoteAdapter(noteDao.getAllCategories(), this@MainActivity)
            rv.adapter = noteAdapter

        }
        setupAddTaskButton()

    }

    private fun setupAddTaskButton() {
        var name: String
        var clr: Int = 0

        val btnAddTask: FloatingActionButton = findViewById(R.id.floatingActionButton)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.new_note, null)

        // Initialize the new task popup
        newTaskPopup = PopupWindow(
            popupView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val addNoteBlock = popupView.findViewById<ConstraintLayout>(R.id.yarajal)
        val addNoteBlock2 = popupView.findViewById<ConstraintLayout>(R.id.aaa)
        // Set click listener for the "Add Category" button
        btnAddTask.setOnClickListener {
            // Show the new task popup

// Load the fade-in animation
            val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

// Set a listener to handle animation events if needed
            fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // Animation started
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // Animation ended, you can perform additional actions here if needed
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // Animation repeated (if applicable)
                }
            })

            addNoteBlock2.startAnimation(fadeInAnimation)
// Apply the animation to the view
            addNoteBlock.startAnimation(fadeInAnimation)

            newTaskPopup.showAtLocation(it, Gravity.CENTER, 0, 0)
        }

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
            val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"))
            val newCategory = Note(name = name, color = clr, content = "", date = date)
            // Use Kotlin Coroutine to perform database operation asynchronously
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (noteDao.getAllCategories().any { it.name == name }) {
                            withContext(Dispatchers.Main) {
                                etName.text.clear()
                                etName.error = "This note already excite!"
                            }
                        } else {
                            noteDao.insert(newCategory)
                            val intent = Intent()
                            intent.putExtra("category_added", true)
                            setResult(Activity.RESULT_OK, intent)
                            val updatedCategories =
                                noteDao.getAllCategories() // Fetch updated list from database

                            // Update the UI on the main thread
                            withContext(Dispatchers.Main) {
                                noteAdapter.updateCategories(updatedCategories)
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

    override fun onCategoryItemClicked(note: Note, color: Int) {
        val intent = Intent(this, noteActivity::class.java)
        intent.putExtra("NAME", note.name)
        intent.putExtra("BACKGROUND", note.color)
        intent.putExtra("COLOR", color)
        intent.putExtra("CONTENT", note.content)
        intent.putExtra("ID", note.id)
        intent.putExtra("DATE", note.date)
        startActivityForResult(intent, 2) // Start the activity for result
    }
}
