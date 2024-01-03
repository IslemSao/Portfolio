package com.example.TicTacToe

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TicTacToeViewModel : ViewModel() {

    val reset = mutableStateOf(false)
    val newGame = mutableStateOf(false)
    val winnerState = mutableStateOf<WinningCombination?>(null)
    val tie = mutableStateOf(false)
    val XCounter = mutableStateOf(0)
    val OCounter = mutableStateOf(0)
    var currentPlayer by mutableStateOf(Player.O)
    // State variables
    val player = mutableStateOf(currentPlayer)
    var firstPlayer by mutableStateOf(player.value)
    var winningCombination by mutableStateOf<WinningCombination?>(null)
    val gameBoard = Array(3) { Array(3) { mutableStateOf(Player.EMPTY) } }

    var isGameClickable by mutableStateOf(true)
        private set

    // ... Other functions

    fun disableGameClick() {
        isGameClickable = false
    }

    fun enableGameClick() {
        isGameClickable = true
    }

    // Initialize the game state
    fun initializeGame() {
        if (newGame.value) {
            firstPlayer = if (firstPlayer == Player.X) Player.O else Player.X
            currentPlayer = firstPlayer
        }
        newGame.value = false
    }

    // reset the game state
    fun resetGame() {
        if (reset.value) {
            gameBoard.forEach { row ->
                row.forEach { box ->
                    box.value = Player.EMPTY
                }
            }
            winningCombination = null
            reset.value = false
            currentPlayer = firstPlayer
        }
    }

    fun onResetButtonClick() {
        reset.value = true
        winnerState.value = null
        tie.value = false
        newGame.value = true
    }


    fun checkForWinner(): WinningCombination? {
        // Check rows
        for (i in 0 until 3) {
            if (gameBoard[i][0].value != Player.EMPTY &&
                gameBoard[i][0].value == gameBoard[i][1].value &&
                gameBoard[i][1].value == gameBoard[i][2].value
            ) {
                return WinningCombination(
                    player = gameBoard[i][0].value,
                    indices = listOf(Pair(i, 0), Pair(i, 1), Pair(i, 2))
                )
            }
        }

        // Check columns
        for (j in 0 until 3) {
            if (gameBoard[0][j].value != Player.EMPTY &&
                gameBoard[0][j].value == gameBoard[1][j].value &&
                gameBoard[1][j].value == gameBoard[2][j].value
            ) {
                return WinningCombination(
                    player = gameBoard[0][j].value,
                    indices = listOf(Pair(0, j), Pair(1, j), Pair(2, j))
                )
            }
        }
        // Check diagonals
        if (gameBoard[0][0].value != Player.EMPTY &&
            gameBoard[0][0].value == gameBoard[1][1].value &&
            gameBoard[1][1].value == gameBoard[2][2].value
        ) {
            return WinningCombination(
                player = gameBoard[0][0].value,
                indices = listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
            )
        }

        if (gameBoard[0][2].value != Player.EMPTY &&
            gameBoard[0][2].value == gameBoard[1][1].value &&
            gameBoard[1][1].value == gameBoard[2][0].value
        ) {
            return WinningCombination(
                player = gameBoard[0][2].value,
                indices = listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
            )
        }

        return null  // No winner yet
    }

    fun onBoxClick(row: Int, col: Int) {
        if (gameBoard[row][col].value == Player.EMPTY) {
            gameBoard[row][col].value = currentPlayer
            currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
            CheckWinner()
        }
        CheckTie()
    }

    private fun CheckWinner() {
        val winner = checkForWinner()
        winnerState.value = winner
        if (winner != null) {
            winningCombination = winner
            if (winner.player == Player.X) {
                XCounter.value += 1
            } else {
                OCounter.value += 1
            }
        }
    }

    private fun CheckTie() {
        var Tie = true
        gameBoard.forEach { row ->
            row.forEach { box ->
                if (box.value == Player.EMPTY)
                    Tie = false
            }
        }
        tie.value = (Tie && winningCombination == null)
    }

}
