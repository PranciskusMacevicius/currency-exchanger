package currency.exchanger

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun MainActivity.setPickers() {
    val sharedPrefs =
        getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()

    val builder = AlertDialog.Builder(this)

    binding.sellPickerImage.setOnClickListener {
        var lastIndex = -1
        builder.setItems(currencies.toTypedArray()) { _, which ->
            run repeatBlock@{
                repeat(currencies.size) {
                    when (which) {
                        ++lastIndex -> {
                            val selectedCurrency = currencies[lastIndex]

                            if (selectedCurrency == binding.receiveCurrencyText.text)
                                binding.receiveCurrencyText.text = binding.sellCurrencyText.text
                            binding.sellCurrencyText.text = (currencies[lastIndex])
                            editor.putString("sellCurrencyText", selectedCurrency)
                            editor.putString(
                                "receiveCurrencyText",
                                binding.receiveCurrencyText.text.toString()
                            )
                            editor.apply()
                            refreshEstimatedSum()
                            return@repeatBlock
                        }
                    }
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
        decreasingCurrency = binding.sellCurrencyText.text.toString()
    }

    binding.receivePickerImage.setOnClickListener {
        var lastIndex = -1
        builder.setItems(currencies.toTypedArray()) { _, which ->
            run repeatBlock@{
                repeat(currencies.size) {
                    when (which) {
                        ++lastIndex -> {
                            val selectedCurrency = currencies[lastIndex]
                            editor.putString("receiveCurrencyText", selectedCurrency)
                            editor.apply()

                            if (selectedCurrency == binding.sellCurrencyText.text)
                                binding.sellCurrencyText.text = binding.receiveCurrencyText.text
                            binding.receiveCurrencyText.text = (currencies[lastIndex])
                            editor.putString("receiveCurrencyText", selectedCurrency)
                            editor.putString(
                                "sellCurrencyText", binding.sellCurrencyText.text.toString()
                            )
                            editor.apply()
                            refreshEstimatedSum()
                            return@repeatBlock
                        }
                    }
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }
}

