package com.example.TicTacToe

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TicTacToeBox(player: MutableState<Player>, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (player.value != Player.EMPTY) {
            when (player.value) {
                Player.X ->
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "image description",
                        contentScale = ContentScale.None
                    )

                Player.O ->
                    Image(
                        painter = painterResource(id = R.drawable.o),
                        contentDescription = "image description",
                        contentScale = ContentScale.None
                    )

                else -> {
                    // No icon for empty state
                }
            }
        }
    }
}
