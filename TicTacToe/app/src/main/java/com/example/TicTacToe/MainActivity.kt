package com.example.TicTacToe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.example.TicTacToe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                val boxClick1 = remember { mutableStateOf(false) }
                val boxClick2 = remember { mutableStateOf(false) }
                val boxColor1 = remember { mutableStateOf(Color(0xFFB2E3FF)) }
                val boxColor2 = remember { mutableStateOf(Color(0xFFB2E3FF)) }
                mainScreen(
                    onClick = {
                        if (!boxClick1.value && !boxClick2.value) {
                            //Toast message to select a player
                            Toast.makeText(
                                this,
                                "Please select a player",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@mainScreen

                        }
                        val X = boxClick1.value // Assuming X is a local variable
                        // Launch the game activity
                        val intent = Intent(this, GameActivity::class.java)
                        intent.putExtra("X", X)
                        startActivity(intent)
                    },
                    boxClick1 = boxClick1,
                    boxClick2 = boxClick2,
                    boxColor1 = boxColor1,
                    boxColor2 = boxColor2
                )
            }
        }
    }
}
