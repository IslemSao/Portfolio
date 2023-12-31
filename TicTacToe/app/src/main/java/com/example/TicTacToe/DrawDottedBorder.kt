package com.example.TicTacToe

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor

fun Modifier.drawDottedBorder(
    color: Color,
) = drawBehind {
    val rect = Rect(0f, 0f, size.width.toFloat(), size.height.toFloat())
    val path = Path().apply { addRect(rect) }

    // Use drawPath with a SolidColor brush for compatibility
    drawPath(path, SolidColor(color)) // Corrected call: Use SolidColor brush
}
