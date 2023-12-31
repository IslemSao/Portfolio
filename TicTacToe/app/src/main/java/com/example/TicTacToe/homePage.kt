package com.example.TicTacToe

import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun mainScreen(
    onClick : () -> Unit = {},
    boxClick1: MutableState<Boolean>,
    boxClick2: MutableState<Boolean>,
    boxColor1: MutableState<Color>,
    boxColor2: MutableState<Color>

) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),

        ) {

        Image(
            modifier = Modifier.absoluteOffset(x = 310.dp, y = 97.dp),
            painter = painterResource(id = R.drawable.x),
            contentDescription = "image description",
            contentScale = ContentScale.None
        )
        Image(
            modifier = Modifier.absoluteOffset(x = 278.dp, y = 297.dp),
            painter = painterResource(id = R.drawable.x),
            contentDescription = "image description",
            contentScale = ContentScale.None
        )
        Image(
            modifier = Modifier.absoluteOffset(x = (-10).dp, y = (-10).dp),
            painter = painterResource(id = R.drawable.o),
            contentDescription = "image description",
            contentScale = ContentScale.None
        )
        Image(
            modifier = Modifier.absoluteOffset(x = 70.dp, y = 278.dp),
            painter = painterResource(id = R.drawable.o),
            contentDescription = "image description",
            contentScale = ContentScale.None
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 100.dp, bottom = 100.dp)
                    .fillMaxWidth(),
                text = "Tic Tac \nToe",
                style = TextStyle(
                    fontSize = 60.sp,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFF1E1E1E),
                    textAlign = TextAlign.Center,
                )
            )
            //now a row of two boxes to choose x or o
            //each bow will have an image

            Row(
                modifier = Modifier
                    .padding(top = 100.dp, bottom = 50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                //the first box
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xEE06A4FF),
                            shape = RoundedCornerShape(size = 6.dp)
                        )
                        .width(108.dp)
                        .height(100.dp)
                        //i want to change the background color of the box when it is clicked
                        //if its clicked make it 0xFFB2E3FF else white
                        //i want to change the state of the box click when it is clicked
                        .clickable {
                            boxClick1.value = !boxClick1.value
                            boxClick2.value = false
                        }
                        .background(color = if (boxClick1.value) boxColor1.value else Color.White , shape = RoundedCornerShape(size = 6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "image description",
                        contentScale = ContentScale.None
                    )
                }
                //the second box
                Box(
                    Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFF929A9E),
                            shape = RoundedCornerShape(size = 6.dp)
                        )
                        .width(108.dp)
                        .clickable {
                            boxClick2.value = !boxClick2.value
                            boxClick1.value = false
                        }
                        .background(color = if (boxClick2.value) boxColor2.value else Color.White , shape = RoundedCornerShape(size = 6.dp))
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.o),
                        contentDescription = "image description",
                        contentScale = ContentScale.None
                    )
                }

            }

            //now a button with text
            Button(
                modifier = Modifier
                    .width(300.dp)
                    .height(47.dp)
                    .background(
                        color = Color(0xFFFFD429),
                        shape = RoundedCornerShape(size = 10.dp)
                    ),
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD429),
                    contentColor = Color(0xFFFFFFFF)
                )
            ) {
                Text(
                    text = "Play now",
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
