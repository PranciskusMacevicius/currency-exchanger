package currency.exchanger

import android.content.Context
import android.content.SharedPreferences
import java.math.BigDecimal
import java.math.RoundingMode

fun MainActivity.receive() {
    val sharedPrefs: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    var lastIndex = 0

    run repeatBlock@{
        repeat(currencies.size) {
            if (increasingCurrency == currencies[lastIndex]) {
                val balance = sharedPrefs.getString("balance$lastIndex", "")?.toDouble()
                val newBalance = balance!! + resultConversion
                val newBalanceCentesimal =
                    BigDecimal(newBalance).setScale(2, RoundingMode.HALF_EVEN)

                // Writes new value to textView
                massTextViews[lastIndex].text =
                    (newBalanceCentesimal.toString() + " ${currencies[lastIndex]}")
                balances.removeAt(lastIndex)
                balances.add(lastIndex, newBalance)
                editor.apply()
                println(newBalanceCentesimal.toString() + " ${currencies[lastIndex]}")
                return@repeatBlock
            }
            lastIndex++
        }
    }
    lastIndex = 0

    // Sorts balanceCurrencyTexts by ABC and after that by balance
    sorted = currencies.zip(balances)
        .sortedBy { it.first }
    currencies = sorted.map { it.first } as MutableList<String>
    balances = sorted.map { it.second } as MutableList<Double>
    sorted = currencies.zip(balances)
        .sortedByDescending { it.second }
    currencies = sorted.map { it.first } as MutableList<String>
    balances = sorted.map { it.second } as MutableList<Double>
    lastIndex = 0

    // Puts sorted currencies with values into textViews
    repeat(currencies.size) {
        val balancesCentesimal = BigDecimal(balances[lastIndex]).setScale(2, RoundingMode.HALF_EVEN)
        massTextViews[lastIndex].text = ("$balancesCentesimal ${currencies[lastIndex]}")
        editor.putString("balance${lastIndex}", "${balances[lastIndex]}")
        editor.putString("currency${lastIndex}", currencies[lastIndex])
        editor.apply()
        lastIndex++
    }

    resultConversion = .0
}