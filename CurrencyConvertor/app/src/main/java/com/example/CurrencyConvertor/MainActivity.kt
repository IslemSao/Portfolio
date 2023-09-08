package com.example.CurrencyConvertor

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.CurrencyConvertor.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.collections.MutableMap
import kotlin.collections.MutableMap as MutableMapOf

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var currencyMap: MutableMap<String, Double>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultCurrencyRates()
        initializeUI()
    }


    private fun initializeUI() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            // Handle no network connection scenario
            val exchangeRate: TextView = findViewById(R.id.textView4)
            Snackbar.make(exchangeRate, "No connection!", Snackbar.LENGTH_LONG).setAction("retry") {
                initializeUI()
            }.show()
            setDefaultCurrencyRates()
        } else {
            fetchData()
        }
        setupSpinners()
        setupAmountEditText()
        setupRoundButton()
    }

    private fun fetchData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://open.er-api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val currencyApiService = retrofit.create(CurrencyApiService::class.java)

        lifecycleScope.launch {
            val response = try {
                currencyApiService.getCurrencyRates()
            } catch (e: IOException) {
                Log.e("ERROR", "NO CONNECTION")
                return@launch
            } catch (e: HttpException) {
                Log.e("ERROR", "Http exception")
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val myRequest = response.body()
                if (myRequest != null) {
                    // Update currencyMap with the fetched data
                    currencyMap = mutableMapOf(
                        "USD" to 1.0,
                        "EUR" to myRequest.rates.EUR,
                        "DZD" to myRequest.rates.DZD,
                        "SAR" to myRequest.rates.SAR,
                        "GBP" to myRequest.rates.GBP
                    )
                }
            }
        }
    }

    private fun performCurrencyConversion(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        exchangeRateMap: MutableMapOf<String, Double>
    ): Double {
        // Replace with your actual exchange rate lookup logic

        val fromRate = exchangeRateMap[fromCurrency] ?: return 0.0
        val toRate = exchangeRateMap[toCurrency] ?: return 0.0

        // Perform the conversion
        return (amount * toRate) / fromRate
    }

    private fun updateConvertedAmount(
        currencyMap: MutableMap<String, Double>,
        amountEditText: EditText,
        convertedAmountEditText: TextView,
        exchangeRate: TextView,
        currencySpinner: Spinner,
        currencySpinner2: Spinner
    ) {
        val fromCurrency = (currencySpinner.selectedItem as CurrencyItem).currencyCode
        val toCurrency = (currencySpinner2.selectedItem as CurrencyItem).currencyCode

        val amountString = amountEditText.text.toString()

        if (amountString.isEmpty()) {
            amountEditText.error = "Amount field required"
            convertedAmountEditText.text = ""
            exchangeRate.text = ""
            return
        }

        val amount = amountString.toDoubleOrNull() ?: return

        val convertedAmount =
            performCurrencyConversion(amount, fromCurrency, toCurrency, currencyMap)

        convertedAmountEditText.setText(String.format("%.4f", convertedAmount))
        val result = String.format("%.4f", convertedAmount / amount)
        val str = "1 $fromCurrency = $result $toCurrency"
        exchangeRate.text = str
    }

    private fun setDefaultCurrencyRates() {
        currencyMap = mutableMapOf(
            "USD" to 1.0,
            "EUR" to 0.913137,
            "DZD" to 135.718646,
            "SAR" to 3.75,
            "GBP" to 0.787707
        )
    }

    private fun setupSpinners() {

        val currencyItems = listOf(
            CurrencyItem(R.drawable.flag_usa, "USD"),
            CurrencyItem(R.drawable.flag_eu, "EUR"),
            CurrencyItem(R.drawable.flag_dzd, "DZD"),
            CurrencyItem(R.drawable.flag_sar, "SAR"),
            CurrencyItem(R.drawable.flag_gpb, "GBP"),
        )
        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val currencySpinner2 = findViewById<Spinner>(R.id.currencySpinner2)
        val exchangeRate: TextView = findViewById(R.id.textView4)
        val amountEditText: EditText = findViewById(R.id.amountEditText)
        val convertedAmountEditText: TextView = findViewById(R.id.convertedAmountEditText)

        val spinnerAdapter = CustomSpinnerAdapter(this, currencyItems)
        currencySpinner.adapter = spinnerAdapter
        currencySpinner2.adapter = spinnerAdapter

        val defaultCurrencyPosition = currencyItems.indexOfFirst { it.currencyCode == "EUR" }
        currencySpinner2.setSelection(defaultCurrencyPosition)

        // Set up listener for currencySpinner
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateConvertedAmount(
                    currencyMap,
                    amountEditText,
                    convertedAmountEditText,
                    exchangeRate,
                    currencySpinner,
                    currencySpinner2
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set up listener for currencySpinner2
        currencySpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateConvertedAmount(
                    currencyMap,
                    amountEditText,
                    convertedAmountEditText,
                    exchangeRate,
                    currencySpinner,
                    currencySpinner2
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupAmountEditText() {
        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val currencySpinner2 = findViewById<Spinner>(R.id.currencySpinner2)
        val exchangeRate: TextView = findViewById(R.id.textView4)
        val amountEditText: EditText = findViewById(R.id.amountEditText)
        val convertedAmountEditText: TextView = findViewById(R.id.convertedAmountEditText)

        amountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConvertedAmount(
                    currencyMap,
                    amountEditText,
                    convertedAmountEditText,
                    exchangeRate,
                    currencySpinner,
                    currencySpinner2
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        amountEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Clear the focus from the EditText
                amountEditText.clearFocus()
                // Hide the keyboard
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(amountEditText.windowToken, 0)
                // Return true to indicate that we've handled the action
                true
            } else {
                false
            }
        }
    }

    private fun setupRoundButton() {

        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val currencySpinner2 = findViewById<Spinner>(R.id.currencySpinner2)
        val roundButton = binding.roundButton

        roundButton.setOnClickListener {

            val tempPosition = currencySpinner.selectedItemPosition
            currencySpinner.setSelection(currencySpinner2.selectedItemPosition)
            currencySpinner2.setSelection(tempPosition)
        }
    }
}