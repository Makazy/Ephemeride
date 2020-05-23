package com.ephemeride

import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.SpannedString
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ephemeride.services.NotificationService
import com.ephemeride.services.WikipediaAPIClient
import com.ephemeride.services.sendEphemerideNotification
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var channelId: String = ""
    private var channelName: String = ""
    private var channelDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        channelId = getString(R.string.ephemeride_channel_id)?: ""
        channelName = getString(R.string.ephemeride_channel_name)
        channelDescription = getString(R.string.ephemeride_channel_description)

        NotificationService.createNotificationChannel(
            this,
            channelId,
            channelName,
            channelDescription,
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    fun notifyMe(view: View) {
        WikipediaAPIClient.requestEphemeride(this) { ephemeride ->
            NotificationService.sendEphemerideNotification(
                this,
                String.format(
                    Locale.FRENCH,
                    getString(R.string.ephemeride_title),
                    GregorianCalendar()
                        .apply { time = ephemeride.date }
                ),
                ephemeride.events.map { event ->
                    fromHtmlCompat(event.toString(), Html.FROM_HTML_MODE_COMPACT)
                })
        }
    }

    private fun loremIpsumCall() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://loripsum.net/api/1/short/headers/plaintext"
        Log.d(TAG, "loremIpsumCall: $url")
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response ->
                Log.d(TAG, "Received: $response")
                val title = response.split("\n").toTypedArray()[0]
                val description = response.split("\n").toTypedArray()[1]

                NotificationService.sendEphemerideNotification(this, title, listOf(SpannedString.valueOf(description)))
            },
            Response.ErrorListener { error ->
                Log.e(
                    TAG,
                    "Unable to request lorem ipsum : ",
                    error
                )
            })
        queue.add(stringRequest)
    }

    @Suppress("DEPRECATION")
    private fun fromHtmlCompat(source: String, flags: Int): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(source, flags)
        else Html.fromHtml(source)
}