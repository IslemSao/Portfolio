package com.example.CurrencyConvertor

import retrofit2.Response
import retrofit2.http.GET


interface  CurrencyApiService {
    @GET("/v6/latest/usd")
    suspend fun getCurrencyRates(): Response<myRequest>
}