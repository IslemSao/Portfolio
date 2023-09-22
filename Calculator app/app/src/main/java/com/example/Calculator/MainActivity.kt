package com.example.Calculator

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Calculator.databinding.ActivityMainBinding
import com.google.gson.Gson
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private var equation = ""
    private var result = ""
    private var cibon = false
    private var historyList = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Replace with the actual layout name


        val btn0 = findViewById<Button>(R.id.btn_0)
        val btn1 = findViewById<Button>(R.id.btn_1)
        val btn2 = findViewById<Button>(R.id.btn_2)
        val btn3 = findViewById<Button>(R.id.btn_3)
        val btn4 = findViewById<Button>(R.id.btn_4)
        val btn5 = findViewById<Button>(R.id.btn_5)
        val btn6 = findViewById<Button>(R.id.btn_6)
        val btn7 = findViewById<Button>(R.id.btn_7)
        val btn8 = findViewById<Button>(R.id.btn_8)
        val btn9 = findViewById<Button>(R.id.btn_9)
        val btnx = findViewById<Button>(R.id.btn_x)
        val btnc = findViewById<Button>(R.id.btn_c)
        val btnsigne = findViewById<Button>(R.id.btn_signe)
        val btnpoint = findViewById<Button>(R.id.btn_point)
        val btnpercentage = findViewById<Button>(R.id.btn_percentage)
        val btnequals = findViewById<Button>(R.id.btn_equals)
        val btndevision = findViewById<Button>(R.id.btn_devision)
        val btnplus = findViewById<Button>(R.id.btn_plus)
        val btndelete = findViewById<ImageButton>(R.id.btn_delete)
        val btnminus = findViewById<Button>(R.id.btn_minus)
        val btnHistory = findViewById<Button>(R.id.btn_history)
        val btnClearHistory = findViewById<Button>(R.id.btnClearHistory)
        val tvequation = findViewById<TextView>(R.id.tv_equation)
        val tvresult = findViewById<TextView>(R.id.tv_result)
        val history = findViewById<ConstraintLayout>(R.id.rvContainer)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.rv)



        history.visibility = View.GONE


        btn0.setOnClickListener {
            appendToEquation("0")
        }
        btn1.setOnClickListener {
            appendToEquation("1")
        }
        btn2.setOnClickListener {
            appendToEquation("2")
        }
        btn3.setOnClickListener {
            appendToEquation("3")
        }
        btn4.setOnClickListener {
            appendToEquation("4")
        }
        btn5.setOnClickListener {
            appendToEquation("5")
        }
        btn6.setOnClickListener {
            appendToEquation("6")
        }
        btn7.setOnClickListener {
            appendToEquation("7")
        }
        btn8.setOnClickListener {
            appendToEquation("8")
        }
        btn9.setOnClickListener {
            appendToEquation("9")
        }
        btnplus.setOnClickListener {
            if (cibon) {
                cibon = false
                equation = result
            }
            if (equation.isNotEmpty() && isOperator(equation.last())) {
                equation = equation.dropLast(1) // Remove the last character
            }
            appendToEquation("+")
        }
        btnsigne.setOnClickListener {
            equation = reverseLastNumberSign(equation)
            tvequation.text = equation
        }
        btndelete.setOnClickListener {
            if (equation.isNotEmpty()) {
                equation = equation.substring(0, equation.length - 1)
                tvequation.text = equation
            }
        }
        btndevision.setOnClickListener {
            if (cibon) {
                cibon = false
                equation = result
            }
            if (equation.isNotEmpty() && isOperator(equation.last())) {
                equation = equation.dropLast(1) // Remove the last character
            }
            appendToEquation("/")
        }
        btnx.setOnClickListener {
            if (cibon) {
                cibon = false
                equation = result
            }
            if (equation.isNotEmpty() && isOperator(equation.last())) {
                equation = equation.dropLast(1) // Remove the last character
            }
            appendToEquation("*")
        }
        btnminus.setOnClickListener {
            if (cibon) {
                cibon = false
                equation = result
            }
            if (equation.isNotEmpty() && isOperator(equation.last())) {
                equation = equation.dropLast(1) // Remove the last character
            }
            appendToEquation("-")
        }
        btnpercentage.setOnClickListener {
            if (cibon) {
                cibon = false
                equation = result
            }
            if (equation.isNotEmpty() && isOperator(equation.last())) {
                equation = equation.dropLast(1) // Remove the last character
            }
            appendToEquation("%")
        }
        btnpoint.setOnClickListener {
            if (equation.isNotEmpty() && (equation.last()) == '.') {
                equation = equation.dropLast(1) // Remove the last character
            }
            appendToEquation(".")
        }
        btnc.setOnClickListener {
            equation = ""
            result = "0"
            tvequation.text = ""
            tvresult.text = "0"
            cibon = false
        }


        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        val historyAdapter = historyAdapter(historyList)
        historyRecyclerView.adapter = historyAdapter

        btnequals.setOnClickListener {
            try {
                val expression = ExpressionBuilder(equation).build()
                if (!isDouble(expression.evaluate())) {
                    expression.evaluate().toInt()
                    result = expression.evaluate().toInt().toString()
                } else {
                    result = expression.evaluate().toString()
                }
                tvresult.text = result

                historyList.add("$equation = $result")

                // Convert history list to JSON string
                val historyJson = Gson().toJson(historyList)

                // Save history JSON string in SharedPreferences
                val sharedPrefs = getSharedPreferences("CalculatorPrefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putString("history", historyJson).apply()

                cibon = true
            } catch (e: Exception) {
                tvresult.text = "Error"
            }
        }

        val sharedPrefs = getSharedPreferences("CalculatorPrefs", Context.MODE_PRIVATE)
        val historyJson = sharedPrefs.getString("history", null)
        if (historyJson != null) {
            val gson = Gson()
            historyList.addAll(
                gson.fromJson(historyJson, Array<String>::class.java)?.toList() ?: emptyList()
            )
        }
        btnHistory.setOnClickListener {
            historyList.reverse() // Reverse the historyList
            historyAdapter.notifyDataSetChanged() // Update the adapter
            if (history.visibility == View.VISIBLE) {
                history.visibility = View.GONE
            } else {
                history.visibility = View.VISIBLE
            }
        }
        btnClearHistory.setOnClickListener {
            historyList.clear() // Clear the historyList
            historyAdapter.notifyDataSetChanged() // Update the adapter
            val historyJson = Gson().toJson(historyList)

            // Save history JSON string in SharedPreferences
            val sharedPrefs = getSharedPreferences("CalculatorPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("history", historyJson).apply()
        }


    }

    private fun appendToEquation(value: String) {
        val tvequation = findViewById<TextView>(R.id.tv_equation)
        equation += value
        tvequation.text = equation
    }

    private fun isDouble(number: Number): Boolean {
        return number.toDouble() != number.toInt().toDouble()
    }


    private fun reverseLastNumberSign(equation: String): String {
        val operators = arrayOf('+', '-', '*', '/', '%')
        val reversedEquation = StringBuilder(equation.trim())

        var operatorIndex = -1
        operators.forEach { operator ->
            val index = reversedEquation.lastIndexOf(operator)
            if (index >= 0 && index > operatorIndex) {
                operatorIndex = index
            }
        }

        if (operatorIndex >= 0) {
            val operator = reversedEquation[operatorIndex]
            if (operator == '-') {
                // Replace the minus with a plus
                reversedEquation.replace(operatorIndex, operatorIndex + 1, "+")
            } else if (operator == '+') {
                // Replace the plus with a minus
                reversedEquation.replace(operatorIndex, operatorIndex + 1, "-")
            } else {
                // Reverse the sign of the last number
                val lastNumber = reversedEquation.substring(operatorIndex + 1)
                val reversedNumber = changeNumberSign(lastNumber)
                reversedEquation.replace(operatorIndex + 1, reversedEquation.length, reversedNumber)
            }
        } else if (reversedEquation.isNotEmpty()) {
            // Only reverse the sign of the last number if there's no operator
            val reversedNumber = changeNumberSign(reversedEquation.toString())
            reversedEquation.setLength(0)
            reversedEquation.append(reversedNumber)
        }

        return reversedEquation.toString()
    }

    private fun changeNumberSign(number: String): String {
        return if (number.startsWith('-')) {
            number.substring(1)
        } else {
            "-$number"
        }
    }


    private fun isOperator(char: Char): Boolean {
        return char == '+' || char == '-' || char == '*' || char == '/' || char == '%'
    }

}