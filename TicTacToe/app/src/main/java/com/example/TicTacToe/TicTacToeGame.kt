package com.example.TicTacToe


import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TicTacToeGame(
    viewModel: TicTacToeViewModel
) {
    val gameBoard = viewModel.gameBoard
    val winningCombination = viewModel.winningCombination

    viewModel.initializeGame()
    viewModel.resetGame()

    Surface(
        modifier = Modifier
            .size(300.dp)
            .background(Color.Transparent),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 0 until 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    for (j in 0 until 3) {
                        TicTacToeBox(
                            player = gameBoard[i][j],
                            onClick = {
                                if (viewModel.isGameClickable) {
                                    viewModel.onBoxClick(i, j)
                                }
                            }
                        )
                    }
                }
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            for (i in 1 until 3) {
                drawLine(
                    color = Color(0xFFB2E3FF),
                    start = Offset(((100.dp * i) + 2.dp).toPx(), 0f),
                    end = Offset(((100.dp * i) + 2.dp).toPx(), size.height),
                    strokeWidth = 4.dp.toPx()
                )
            }

            for (i in 1 until 3) {
                drawLine(
                    color = Color(0xFFB2E3FF),
                    start = Offset(0f, ((100.dp * i) + 2.dp).toPx()),
                    end = Offset(size.width, ((100.dp * i) + 2.dp).toPx()),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }
        winningCombination?.let { combination ->
            DrawWinningLines(combination.indices, combination.player)
        }
    }
}
