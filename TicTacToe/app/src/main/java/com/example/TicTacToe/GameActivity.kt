package com.example.TicTacToe


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.TicTacToe.ui.theme.TicTacToeTheme

class GameActivity : ComponentActivity() {
    private val viewModel by viewModels<TicTacToeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val x = intent.getBooleanExtra("X", false)

        setContent {
            TicTacToeTheme {

                if (x) {
                    viewModel.currentPlayer = Player.X
                }
                gameScreen(viewModel)
            }
        }
    }
}
