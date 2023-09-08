package com.example.weather

data class DailyWeather(
    val date: String,
    var highTemperature: Double,
    var lowTemperature: Double
) {
    fun updateTemperatures(newHighTemp: Double, newLowTemp: Double) {
        if (newHighTemp > highTemperature) {
            highTemperature = newHighTemp
        }
        if (newLowTemp < lowTemperature) {
            lowTemperature = newLowTemp
        }
    }
}
