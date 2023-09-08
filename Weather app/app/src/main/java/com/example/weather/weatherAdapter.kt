package com.example.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class weatherAdapter(var weatherList : List<wetherInfo>) : RecyclerView.Adapter<weatherAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weatheritem ,parent , false)
        return todoViewHolder(view)
    }

    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {
            val tvDate = findViewById<TextView>(R.id.tvRCDate)
            tvDate.text = weatherList[position].date
            val tvTemp = findViewById<TextView>(R.id.tvRCTemp)
            tvTemp.text = weatherList[position].temp.toString() + " °c"
            val tvCondition = findViewById<TextView>(R.id.tvRCCondition)
            tvCondition.text = weatherList[position].condition
            val img = findViewById<ImageView>(R.id.iv)
            img.setImageResource(mapIconToDrawable(weatherList[position].img))
        }
    }

    override fun getItemCount(): Int {
        return weatherList.size
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