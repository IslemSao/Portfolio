package com.example.TicTacToe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun DrawWinningLines(indices: List<Pair<Int, Int>>, winner: Player) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        var startX = 0f
        var startY = 0f
        var endX = size.width
        var endY = size.height
        // Check if it's a diagonal win
        if (indices.first().first != indices.last().first &&
            indices.first().second != indices.last().second
        ) {
            if (indices.first().first == 0 && indices.first().second == 0) {
                startX = 0f
                startY = 0f
                endX = size.width
                endY = size.height
            } else {
                startX = size.width
                startY = 0f
                endX = 0f
                endY = size.height

            }
            drawLine(
                color = if (winner == Player.X) Color(0xFF389CEB) else Color(0xFFFFD429),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 5.dp.toPx()
            )
        } else {
            // It's a horizontal or vertical win
            if (indices.first().first == indices.last().first) {
                startX = 0f
                startY = (100.dp * indices.first().first + 50.dp).toPx()
                endX = size.width
                endY = (100.dp * indices.first().first + 50.dp).toPx()
            } else {
                startX = (100.dp * indices.first().second + 50.dp).toPx()
                startY = 0f
                endX = (100.dp * indices.first().second + 50.dp).toPx()
                endY = size.height
            }
            drawLine(
                color = if (winner == Player.X) Color(0xFF389CEB) else Color(0xFFFFD429),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 5.dp.toPx()
            )

        }
    }
}



