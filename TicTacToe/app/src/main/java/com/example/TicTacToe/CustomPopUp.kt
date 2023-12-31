package com.example.TicTacToe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CustomPopUp(
    winnerState: MutableState<WinningCombination?>,
    tie: MutableState<Boolean>,
    viewModel: TicTacToeViewModel
) {
    viewModel.disableGameClick()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x4D1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        var showBoxWithDelay by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = true) {
            delay(500) // Delay for 1 second
            showBoxWithDelay = true
        }
        if (showBoxWithDelay) {
            Box(
                Modifier
                    .width(295.dp)
                    .height(191.dp)
                    .background(
                        color = Color(0xFFB2E3FF),
                        shape = RoundedCornerShape(size = 12.dp)
                    )
            ) {
                //i want to make a column of two text views anf a button
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (tie.value) "Close Game!" else "Congratulations !",
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontFamily = FontFamily(Font(R.font.poppins)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFF000000),
                        )
                    )
                    Text(
                        text = if (tie.value) "Tie!" else if (winnerState.value!!.player == Player.X) "“X” won " else "“O” won ",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.poppins)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF1E1E1E),
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        modifier = Modifier
                            .width(250.dp)
                            .height(45.dp)
                            .background(
                                color = Color(0xEE06A4FF),
                                shape = RoundedCornerShape(size = 12.dp)
                            ),
                        onClick = {
                            viewModel.onResetButtonClick()
                            viewModel.enableGameClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xEE06A4FF),
                            contentColor = Color(0xFFFFFFFF)
                        ),

                        ) {
                        Text(
                            text = "Play again",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontFamily = FontFamily(Font(R.font.poppins)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }
                }
            }
        }
    }
}

