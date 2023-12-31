package com.example.TicTacToe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun DottedLine() {
    Surface(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .width(304.dp)
            .height(1.dp)
            .drawDottedBorder(Color.Black)
            //i want a border dotted line
            .background(color = Color(0xFF000000))
    ) {}
}