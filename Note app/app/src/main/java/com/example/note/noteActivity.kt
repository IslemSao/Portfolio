package com.example.Note

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class noteActivity : AppCompatActivity()  {
    lateinit var noteDao :noteDao
    private lateinit var editText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val name = intent.getStringExtra("NAME")
        val background = intent.getIntExtra("BACKGROUND", 0)
        val color = intent.getIntExtra("COLOR", 0)
        val content = intent.getStringExtra("CONTENT")
        val date = intent.getStringExtra("DATE")

        val categoryNameTextView: TextView = findViewById(R.id.tvNoteName)
        editText = findViewById(R.id.editTextNoteContent)
        editText.setText(content)

        categoryNameTextView.text = name
        val cat1: ConstraintLayout = findViewById(R.id.cl)
        cat1.setBackgroundColor(background)
        editText.setBackgroundColor(background)
        categoryNameTextView.setTextColor(color)
        editText.setTextColor(color)
        // Attach a TextWatcher to the EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has changed
                val editedContent = s.toString()
                val name = intent.getStringExtra("NAME")!!
                // Assuming you have the note ID from your intent
                val noteId = intent.getIntExtra("ID", -1)

                if (noteId != -1) {
                    // Update the note's content in the database
                    val updatedNote = Note(id = noteId, name = name, color = background, content = editedContent , date = date!!)
                    // Use Kotlin Coroutine to perform database operation asynchronously
                    lifecycleScope.launch {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                noteDao = MyApplication.database.noteDao()
                                withContext(Dispatchers.IO){
                                    noteDao.updateNoteContent(updatedNote)
                                }
                            } catch (e: Exception) {
                                println(e)
                            }
                        }
                    }
                }
            }
        })
        // Inside your noteActivity
    }
}