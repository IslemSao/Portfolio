package com.example.weather

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private var latitude: Double = 37.773972
    private var longitude: Double = -122.431297
    private var weatherList = mutableListOf<wetherInfo>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        work()
    }

    private fun work() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            // Handle no network connection scenario
            val tvTemp: TextView = findViewById(R.id.tvTemperature)
            Snackbar.make(tvTemp, "No connection!", Snackbar.LENGTH_LONG).setAction("retry") {
                work()
            }.show()
        } else {
            setupRecycleView()
            checkLocation()
            btnUpdater()
        }
    }

    private fun btnUpdater() {
        val tvCity = findViewById<EditText>(R.id.tvCity)
        val btn = findViewById<Button>(R.id.btnChanger)
        btn.setOnClickListener {
            updateAdress()

            // Clear the focus from the EditText
            tvCity.clearFocus()
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(tvCity.windowToken, 0)

        }
        val btnLoc = findViewById<ImageButton>(R.id.imageButton)
        btnLoc.setOnClickListener {
            checkLocation()
            tvCity.clearFocus()
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(tvCity.windowToken, 0)
        }
    }

    private fun updateAdress() {
        val tvCity = findViewById<EditText>(R.id.tvCity)
        val cityName = tvCity.text
        val geocoder = Geocoder(this@MainActivity)
        try {
            val addresses: List<Address> =
                geocoder.getFromLocationName(cityName.toString(), 1) as List<Address>
            if (addresses.isNotEmpty()) {
                latitude = addresses[0].latitude
                longitude = addresses[0].longitude
                UpdateEverything()
                // Use latitude and longitude as needed
            } else {
                Snackbar.make(tvCity, "WRONG NAME!", Snackbar.LENGTH_LONG).show()
            }
        } catch (e: IOException) {

            e.printStackTrace()
        }
    }

    private fun setupRecycleView() {
        val weatherRecyclerView = findViewById<RecyclerView>(R.id.rv)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weatherRecyclerView.layoutManager = layoutManager
    }

    private fun checkLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with location retrieval and API call
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    // Proceed with the API call and UI updates
                    UpdateEverything()
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationError", "Error getting location: ${e.message}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location retrieval and API call
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            UpdateEverything()
                            // Proceed with the API call and UI updates
                        }
                    }
                .addOnFailureListener { e ->
                    Log.e("LocationError", "Error getting location: ${e.message}")
                }
            }
        }
    }

    private fun UpdateEverything() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApiService = retrofit.create(OpenWeatherMapService::class.java)

        val call = weatherApiService.getWeatherForecast(
            latitude = latitude,
            longitude = longitude,
            apiKey = "05d5be70dda64f02c62f7f5ad8ea16c7",
            units = "metric"
        )
        updateCity()

        call.enqueue(object : Callback<dataFromRequest> {
            override fun onResponse(
                call: Call<dataFromRequest>,
                response: Response<dataFromRequest>
            ) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    val firstInfo = weatherData?.list?.get(0)
                    if (firstInfo != null) {
                        updateUI(firstInfo)
                        fetchData(response)
                    }
                } else {
                    Log.d("ERROR", "response not succesfull")
                    Toast.makeText(this@MainActivity, "an error has occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<dataFromRequest>, t: Throwable) {
                // Handle failure
                Log.e("API_CALL_ERROR", "Failed to fetch weather data", t)
            }
        })
    }

    private fun fetchData(response: Response<dataFromRequest>) {
        val tvPressure = findViewById<TextView>(R.id.tvPressurePerc)
        val tvTempDay1 = findViewById<TextView>(R.id.tcDay1Temp)
        val tvTempDay2 = findViewById<TextView>(R.id.tcDay2Temp)
        val tvTempDay3 = findViewById<TextView>(R.id.tcDay3Temp)
        val tvTempDay4 = findViewById<TextView>(R.id.tcDay5Temp)
        val tvDay1 = findViewById<TextView>(R.id.tvDay1)
        val tvDay2 = findViewById<TextView>(R.id.tvDay2)
        val tvDay3 = findViewById<TextView>(R.id.tvDay3)
        val tvDay4 = findViewById<TextView>(R.id.tvDay4)

        val weatherData = response.body()

        // Group weather data by date using dt_txt substring
        val dailyWeatherData = weatherData?.list?.groupBy {
            it.dt_txt.substring(0, 10) // Extract the date from dt_txt
        }

        // Iterate through the grouped data and update TextViews
        // Get the current date in the format "yyyy-MM-dd"
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Filter out today's weather data from dailyWeatherData
        val filteredWeatherData = dailyWeatherData?.filterKeys { it != currentDate }

        // Sort the filtered data by date (optional)
        val sortedWeatherData = filteredWeatherData?.toSortedMap()

        // Create a list of temperature TextViews for the next 4 days (excluding today)
        val temperatureTextViews = listOf(tvTempDay1, tvTempDay2, tvTempDay3, tvTempDay4)

        val dayTextViews = listOf(tvDay1, tvDay2, tvDay3, tvDay4)
        // Iterate through the filtered and sorted data and update TextViews
        weatherData?.list?.forEachIndexed { index, entry ->
            val item = wetherInfo(
                entry.dt_txt,
                entry.weather[0].description,
                entry.main.temp,
                entry.weather[0].icon
            )
            weatherList.add(index, item)
            val weatherRecyclerView = findViewById<RecyclerView>(R.id.rv)

            val weatherAdapter = weatherAdapter(weatherList)
            weatherRecyclerView.adapter = weatherAdapter
        }

        sortedWeatherData?.values?.take(4)?.forEachIndexed { index, entry ->
            val minTemp = entry.minByOrNull { it.main.temp_min }?.main?.temp_min ?: 0.0
            val maxTemp = entry.maxByOrNull { it.main.temp_max }?.main?.temp_max ?: 0.0

            // Format the temperatures to display like "Min°C/Max°C"
            val formattedTemperatures = String.format("%.1f°C/%.1f°C", minTemp, maxTemp)

            // Get the TextView for the current day's temperatures
            val temperatureTextView = temperatureTextViews[index]

            // Update the TextView with the formatted temperatures
            temperatureTextView.text = formattedTemperatures

            // Get the date from the data and parse it using the inputDateFormat
            val date = entry.first().dt_txt
            val parsedDate =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date)
            // Get the day of the week using the dayOfWeekFormat
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(parsedDate)

            // Get the TextView for the current day's date
            val dayTextView = dayTextViews[index]

            // Update the TextView with the day of the week
            dayTextView.text = dayOfWeek
        }
    }

    private fun updateUI(firstInfo: info) {
        val tvTemp = findViewById<TextView>(R.id.tvTemperature)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val tvHumidity = findViewById<TextView>(R.id.tvHumidityPerc)
        val ivWeatherIcon = findViewById<ImageView>(R.id.imageView)
        val tvClouds = findViewById<TextView>(R.id.tvCloudPerc)
        val tvWind = findViewById<TextView>(R.id.tvWindPerc)
        val tvFeel = findViewById<TextView>(R.id.tvFeels)
        val tvPressure = findViewById<TextView>(R.id.tvPressurePerc)
        val temperature = firstInfo.main.temp
        val humidity = firstInfo.main.humidity
        val cloud = firstInfo.clouds.all
        val wind = String.format(
            "%.3f",
            firstInfo.wind.speed * 3.6
        ) // Format windSpeed to 3 decimal places
        val pressure = firstInfo.main.pressure
        val weatherDescription = firstInfo.weather[0].description
        val feel = firstInfo.main.feels_like.toString()
        tvTemp.text = temperature.toInt().toString() + "°C"
        tvHumidity.text = humidity.toString() + "%"
        tvDescription.text = weatherDescription
        tvClouds.text = cloud.toString() + "%"
        tvWind.text = wind + " km/h"
        tvPressure.text = pressure.toString() + " pha"
        tvFeel.text = "Feels like : " + feel + "°C"
        val weatherIcon = firstInfo.weather[0].icon

        // Set the weather icon
        ivWeatherIcon.setImageResource(mapIconToDrawable(weatherIcon))
    }

    private fun updateCity() {
        val tvCity = findViewById<EditText>(R.id.tvCity)
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            // Request the address information from the Geocoder
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses!!.isNotEmpty()) {
                val city = addresses[0].locality
                if (city != null) {
                    tvCity.setText(city)
                } else {
                    println("City not found")
                }
            } else {
                println("No address found")
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

    }

    fun mapIconToDrawable(icon: String): Int {
        return when (icon) {
            "01d" -> R.drawable.pic01d
            "01n" -> R.drawable.pic01n
            "02d" -> R.drawable.pic02d
            "02n" -> R.drawable.pic02n
            "03d", "03n" -> R.drawable.pic03d
            "04d", "04n" -> R.drawable.pic04d
            "09d", "09n" -> R.drawable.pic09d
            "10d" -> R.drawable.pic10d
            "10n" -> R.drawable.pic10n
            "11d" -> R.drawable.pic11d
            "11n" -> R.drawable.pic11n
            "13d", "13n" -> R.drawable.pic13d
            "50d", "50n" -> R.drawable.pic50d
            else -> R.drawable.pic // Default icon
        }
    }


}
