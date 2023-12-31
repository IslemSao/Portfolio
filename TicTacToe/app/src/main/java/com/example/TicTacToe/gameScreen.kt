package com.example.TicTacToe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun gameScreen(
    viewModel: TicTacToeViewModel,
) {
    val reset = viewModel.reset
    val winnerState = viewModel.winnerState
    val tie = viewModel.tie
    val XCounter = viewModel.XCounter
    val OCounter = viewModel.OCounter
    //we will use the surface composable to make the background color of the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        //we will use the column composable to make the layout of the screen
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //we will use the text composable to make the title of the screen
            Title()
            Spacer(modifier = Modifier.height(20.dp))
            DottedLine()
            Spacer(modifier = Modifier.height(60.dp))
            //we will use the row composable to make the score board
            ScoreBoard(
                XCounter = XCounter,
                OCounter = OCounter,
            )
            Spacer(modifier = Modifier.height(40.dp))
            //we will use the row composable to make the score board
            TicTacToeGame(
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(80.dp))
            ResetButton(viewModel)
        }
    }
    if (winnerState.value != null || tie.value) {
        CustomPopUp(
            winnerState = winnerState,
            tie = tie,
            viewModel = viewModel
        )
    }
}

