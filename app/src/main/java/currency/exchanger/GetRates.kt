package currency.exchanger

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import kotlin.concurrent.thread

fun MainActivity.retrieveRates() {
    val sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()

    val httpClient = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.exchangerate.host/latest")
        .build()

    // Updates rates every 5s
    thread {
        while (true) {
            try {
                httpClient.newCall(request)
                    .execute()
                    .use { response ->
                        stringJson = (response.body()?.string().toString())
                        val rates = JSONObject(stringJson).getJSONObject("rates")
                        var lastIndex = 0
                        repeat(currencies.size) {
                            editor.putString(
                                currencies[lastIndex],
                                rates.getString(currencies[lastIndex++])
                            )
                        }
                        editor.apply()

                        thread {
                            this.runOnUiThread {
                                refreshEstimatedSum()
                            }
                        }
                    }
            } catch (e: IOException) {
            }
            Thread.sleep(5000)
        }
    }
}