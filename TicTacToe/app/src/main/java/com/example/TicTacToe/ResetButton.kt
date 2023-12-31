package com.example.TicTacToe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ResetButton(viewModel: TicTacToeViewModel) {
    Button(
        modifier = Modifier
            .width(300.dp)
            .height(47.dp)
            .background(
                color = Color(0xFFFFD429),
                shape = RoundedCornerShape(size = 10.dp)
            ),
        onClick = {
            if (viewModel.isGameClickable) {
                viewModel.reset.value = true
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD429),
            contentColor = Color(0xFFFFFFFF)
        )
    ) {
        Text(
            text = "Reset",
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
            )
        )

    }
}