package currency.exchanger

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import currency.exchanger.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    var stringJson = ""
    var increasingCurrency = ""
    var decreasingCurrency = ""
    var toastText = ""

    var resultConversion = .0
    var amountSoldCurrency = .0

    var currencies = mutableListOf<String>()
    var balances = mutableListOf<Double>()
    var massTextViews = mutableListOf<TextView>()

    var sorted = listOf<Pair<String, Double>>()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPrefs =
            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val sellCurrencyText = sharedPrefs.getString("sellCurrencyText", "EUR")
        val receiveCurrencyText = sharedPrefs.getString("receiveCurrencyText", "USD")

        binding.sellCurrencyText.text = sellCurrencyText
        binding.receiveCurrencyText.text = receiveCurrencyText

        val value = sharedPrefs.getString("balance0", null)
        var lastIndex = 0

// Creates textView instances programmatically
        repeat(myCurrencies.size) {
            val textView = TextView(this)

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Converters px to dp
            val r: Resources = this.resources
            val fortyToDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40F,
                r.displayMetrics
            ).toInt()
            val tenToDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10F,
                r.displayMetrics
            ).toInt()

            when (lastIndex) {
                0 -> params.setMargins(tenToDp, 0, fortyToDp, 0)
                myCurrencies.size - 1 -> params.setMargins(0, 0, tenToDp, 0)
                else -> params.setMargins(0, 0, fortyToDp, 0)
            }

            textView.layoutParams = params
            textView.text = lastIndex.toString()
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
            textView.setTextColor(Color.parseColor("#000000"))
            textView.typeface = Typeface.create("yantramanav", Typeface.NORMAL)
            binding.linearLayout.addView(textView)
            massTextViews.add(textView)
            lastIndex++
        }
        lastIndex = 0

        // Fills lists and sharedPrefs according to defined configuration.
        if (value == null) {
            repeat(myCurrencies.size) {
                if (myCurrencies[lastIndex] == "EUR") {
                    balances.add(1000.0)
                } else {
                    balances.add(0.0)
                }
                currencies.add(myCurrencies[lastIndex])
                lastIndex++
            }

            sorted = currencies.zip(balances)
                .sortedBy { it.first }
            currencies = sorted.map { it.first } as MutableList<String>
            balances = sorted.map { it.second } as MutableList<Double>
            sorted = currencies.zip(balances)
                .sortedByDescending { it.second }
            currencies = sorted.map { it.first } as MutableList<String>
            balances = sorted.map { it.second } as MutableList<Double>
            lastIndex = 0

            repeat(myCurrencies.size) {
                editor.putString("currency${lastIndex}", currencies[lastIndex])
                editor.putString("balance${lastIndex}", balances[lastIndex].toString())
                val balanceCentesimal =
                    BigDecimal(balances[lastIndex]).setScale(2, RoundingMode.HALF_EVEN)
                massTextViews[lastIndex].text = ("$balanceCentesimal ${currencies[lastIndex]}")
                lastIndex++
            }

            editor.apply()
        } else {

            //Retrieves and fills textViews with balances and currencies from sharedPrefs.
            repeat(myCurrencies.size) {
                val balance = sharedPrefs.getString("balance$lastIndex", "")
                val balanceCentesimal = BigDecimal(balance).setScale(2, RoundingMode.HALF_EVEN)
                val currency = sharedPrefs.getString("currency${lastIndex}", "")
                massTextViews[lastIndex].text = ("$balanceCentesimal $currency")

                if (balance != null) {
                    balances.add(balance.toDouble())
                }

                if (currency != null) {
                    currencies.add(currency)
                }
                lastIndex++
            }
        }


        binding.sellEditText.doAfterTextChanged {
            refreshEstimatedSum()
            editor.putString("sellEditText", binding.sellEditText.text.toString())
        }

        binding.sellEditText.filters =
            arrayOf(DecimalDigitsInputFilter(2), InputFilter.LengthFilter(14))
        getRates()
        retrieveRates()
        setPickers()
        binding.sellEditText.requestFocus()

        binding.submitButton.setOnClickListener {
            responseSubmit()
        }
    }

    /* If you want to add a new currency, feel free to add it anywhere in the array.
    It will be sorted at first by ABC, then by balance amounts from the list.
    If after adding a new currency you want to use this app for the first time without internet connection,
    don't forget to add its default rate to the list in SetDefaultRates.kt file, otherwise, it will be 1 */
    private var myCurrencies = arrayOf(
        "AED",
        "AFN",
        "ALL",
        "AMD",
        "ANG",
        "AOA",
        "ARS",
        "AUD",
        "AWG",
        "AZN",
        "BAM",
        "BBD",
        "BDT",
        "BGN",
        "BHD",
        "BIF",
        "BMD",
        "BND",
        "BOB",
        "BRL",
        "BSD",
        "BTC",
        "BTN",
        "BWP",
        "BYN",
        "BZD",
        "CAD",
        "CDF",
        "CHF",
        "CLF",
        "CLP",
        "CNH",
        "CNY",
        "COP",
        "CRC",
        "CUC",
        "CUP",
        "CVE",
        "CZK",
        "DJF",
        "DKK",
        "DOP",
        "DZD",
        "EGP",
        "ERN",
        "ETB",
        "EUR",
        "FJD",
        "FKP",
        "GBP",
        "GEL",
        "GGP",
        "GHS",
        "GIP",
        "GMD",
        "GNF",
        "GTQ",
        "GYD",
        "HKD",
        "HNL",
        "HRK",
        "HTG",
        "HUF",
        "IDR",
        "ILS",
        "IMP",
        "INR",
        "IQD",
        "IRR",
        "ISK",
        "JEP",
        "JMD",
        "JOD",
        "JPY",
        "KES",
        "KGS",
        "KHR",
        "KMF",
        "KPW",
        "KRW",
        "KWD",
        "KYD",
        "KZT",
        "LAK",
        "LBP",
        "LKR",
        "LRD",
        "LSL",
        "LYD",
        "MAD",
        "MDL",
        "MGA",
        "MKD",
        "MMK",
        "MNT",
        "MOP",
        "MRO",
        "MRU",
        "MUR",
        "MVR",
        "MWK",
        "MXN",
        "MYR",
        "MZN",
        "NAD",
        "NGN",
        "NIO",
        "NOK",
        "NPR",
        "NZD",
        "OMR",
        "PAB",
        "PEN",
        "PGK",
        "PHP",
        "PKR",
        "PLN",
        "PYG",
        "QAR",
        "RON",
        "RSD",
        "RUB",
        "RWF",
        "SAR",
        "SBD",
        "SCR",
        "SDG",
        "SEK",
        "SGD",
        "SHP",
        "SLL",
        "SOS",
        "SRD",
        "SSP",
        "STD",
        "STN",
        "SVC",
        "SYP",
        "SZL",
        "THB",
        "TJS",
        "TMT",
        "TND",
        "TOP",
        "TRY",
        "TTD",
        "TWD",
        "TZS",
        "UAH",
        "UGX",
        "USD",
        "UYU",
        "UZS",
        "VES",
        "VND",
        "VUV",
        "WST",
        "XAF",
        "XAG",
        "XAU",
        "XCD",
        "XDR",
        "XOF",
        "XPD",
        "XPF",
        "XPT",
        "YER",
        "ZAR",
        "ZMW",
        "ZWL"
    )
}




