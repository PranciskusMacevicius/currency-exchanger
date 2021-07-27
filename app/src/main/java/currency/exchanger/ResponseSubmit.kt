package currency.exchanger

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import java.math.BigDecimal
import java.math.RoundingMode


fun MainActivity.responseSubmit() {
    val sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()

    try {
        val valueCurrencyBeingSold =
            sharedPrefs.getString(binding.sellCurrencyText.text.toString(), "1")

        val valueCurrencyBeingReceived =
            sharedPrefs.getString(binding.receiveCurrencyText.text.toString(), "1")

        amountSoldCurrency = binding.sellEditText.text.toString().toDouble()
        val amountSoldCurrencyCentesimal =
            BigDecimal(amountSoldCurrency).setScale(2, RoundingMode.HALF_EVEN)

        if (valueCurrencyBeingReceived != null && valueCurrencyBeingSold != null) {
            resultConversion =
                binding.sellEditText.text.toString()
                    .toDouble() / valueCurrencyBeingSold.toDouble() * valueCurrencyBeingReceived.toDouble()

        }
        val resultConversionCentesimal =
            BigDecimal(resultConversion).setScale(2, RoundingMode.HALF_EVEN)

        toastText =
            "You have converted $amountSoldCurrencyCentesimal ${binding.sellCurrencyText.text} to $resultConversionCentesimal ${binding.receiveCurrencyText.text}."

        if (resultConversion != .0) {
            var timesConversion = sharedPrefs.getInt("timesConversion", 0)

            // First 5 conversions are free of charge. The next ones are paid.
            if (timesConversion > 4) {
                val fee = amountSoldCurrency * 0.007
                val feeCentesimal = BigDecimal(fee).setScale(2, RoundingMode.HALF_EVEN)
                amountSoldCurrency += amountSoldCurrency * 0.007
                toastText =
                    "You have converted $amountSoldCurrencyCentesimal ${binding.sellCurrencyText.text} to $resultConversionCentesimal ${binding.receiveCurrencyText.text}. Commission Fee - $feeCentesimal ${binding.sellCurrencyText.text}."
            } else {
                timesConversion++
                editor.putInt("timesConversion", timesConversion)
                editor.apply()
            }

        } else {
            throw java.lang.NumberFormatException()
        }

        decreasingCurrency = binding.sellCurrencyText.text.toString()
        increasingCurrency = binding.receiveCurrencyText.text.toString()
        var lastIndex = 0

        run repeatBlock@{
            repeat(currencies.size) {
                if (decreasingCurrency == currencies[lastIndex]) {
                    val balance = sharedPrefs.getString("balance$lastIndex", "")?.toDouble()
                    if (amountSoldCurrency <= balance!!) {
                        val newBalance = balance - amountSoldCurrency
                        val newBalanceCentesimal =
                            BigDecimal(newBalance).setScale(2, RoundingMode.HALF_EVEN)

                        // Writes new value to textView
                        massTextViews[lastIndex].text =
                            (newBalanceCentesimal.toString() + " ${currencies[lastIndex]}")
                        balances.removeAt(lastIndex)
                        balances.add(lastIndex, newBalanceCentesimal.toDouble())

                        receive()

                        val toast = Toast.makeText(
                            this,
                            toastText, Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        toast.show()
                    } else {
                        var timesConversion = sharedPrefs.getInt("timesConversion", 0)
                        timesConversion--
                        editor.putInt("timesConversion", timesConversion)
                        editor.apply()
                        val toast = Toast.makeText(
                            this,
                            "Not enough balance", Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        toast.show()
                    }
                    return@repeatBlock
                }
                lastIndex++
            }
        }
    } catch (nfe: NumberFormatException) {
        val toast = Toast.makeText(
            this,
            "Invalid amount", Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }
}
