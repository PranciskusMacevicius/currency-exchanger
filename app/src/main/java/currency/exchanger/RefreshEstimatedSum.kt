package currency.exchanger

import android.content.Context
import java.math.BigDecimal
import java.math.RoundingMode

fun MainActivity.refreshEstimatedSum() {
    val sharedPrefs =
        getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

    try {
        val amountCurrencyBeingSold =
            sharedPrefs.getString(binding.sellCurrencyText.text.toString(), "1")
        val amountCurrencyBeingReceived =
            sharedPrefs.getString(binding.receiveCurrencyText.text.toString(), "1")

        if (amountCurrencyBeingReceived != null && amountCurrencyBeingSold != null) {
            val estimatedSum =
                binding.sellEditText.text.toString()
                    .toDouble() / amountCurrencyBeingSold.toDouble() * amountCurrencyBeingReceived.toDouble()
            val estimatedSumCentesimal =
                BigDecimal(estimatedSum).setScale(2, RoundingMode.HALF_EVEN)
            binding.receiveCalculatedText.text = ("+$estimatedSumCentesimal")
        }
    } catch (nfe: NumberFormatException) {
        binding.receiveCalculatedText.text = ("+0.00")
    }
}