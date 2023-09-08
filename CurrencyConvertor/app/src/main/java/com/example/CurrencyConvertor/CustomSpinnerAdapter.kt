package com.example.CurrencyConvertor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomSpinnerAdapter(private val context: Context, private val currencies: List<CurrencyItem>) : BaseAdapter() {

    override fun getCount(): Int = currencies.size

    override fun getItem(position: Int): Any = currencies[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.spinner_item, parent, false)

        val flagImageView = view.findViewById<ImageView>(R.id.flagImageView)
        val currencyTextView = view.findViewById<TextView>(R.id.currencyTextView)

        val currencyItem = currencies[position]
        flagImageView.setImageResource(currencyItem.flagResource)
        currencyTextView.text = currencyItem.currencyCode

        return view
    }
}
