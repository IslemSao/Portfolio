package com.example.saotest

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class historyAdapter(var historyList : List<String>) : RecyclerView.Adapter<historyAdapter.todoViewHolder>() {
    inner class todoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item ,parent , false)
        return todoViewHolder(view)
    }

    override fun onBindViewHolder(holder: todoViewHolder, position: Int) {
        holder.itemView.apply {
            val tvItem = findViewById<TextView>(R.id.tvHistoryItem)
            tvItem.text = historyList[position]
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}