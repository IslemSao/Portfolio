package com.example.CurrencyConvertor

data class myRequest (
    val result : String,
    val time_last_update_utc : String,
    val time_next_update_utc : String,
    val base_code : String,
    val rates : currency
        )