package com.example.TaskManager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ImportanceSpinnerAdapter(private val context: Context, private val letters: List<Char>) : BaseAdapter() {

    override fun getCount(): Int = letters.size

    override fun getItem(position: Int): Any = letters[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.spinner_importance, parent, false)
        val tv = view.findViewById<TextView>(R.id.tvSpinnerImportance)
        tv.setText(letters[position].toString())

        return view
    }
}
