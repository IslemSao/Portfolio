package com.example.weather


import com.example.weather.dataFromRequest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {

    @GET("data/2.5/forecast")
    fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<dataFromRequest> // Replace ApiResponseModel with your model class
}
