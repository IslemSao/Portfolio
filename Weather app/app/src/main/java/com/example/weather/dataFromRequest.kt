package com.example.weather
data class dataFromRequest(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<info>,
    val message: Int
)