/*
 * Copyright (c) 2020. Heelo Mangola
 */

package com.ephemeride.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import com.ephemeride.R
import com.ephemeride.controllers.NotificationService
import com.ephemeride.controllers.sendEphemerideNotification
import com.ephemeride.wikipedia.WikipediaAPIClient
import com.ephemeride.wikipedia.WikipediaUtils
import java.util.*

class EphemerideReceiver: BroadcastReceiver() {
    private val TAG = "EphemerideReceiver"


    @Suppress("NAME_SHADOWING")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { context -> intent?.let { intent ->
            WikipediaAPIClient.requestEphemeride(context) { ephemeride ->
                NotificationService.sendEphemerideNotification(
                    context,
                    String.format(
                        Locale.FRENCH,
                        context.getString(R.string.ephemeride_title),
                        Date()
                    ),
                    // Add action on click
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(WikipediaUtils.wikiBaseURl+ WikipediaUtils.ephemeridePageTitle)
                    )
                    ,
                    // The content is a list of historical events, format HTML
                    ephemeride.events.map { event ->
                        fromHtmlCompat(event.toString(), 63)
                    })
            }
        }}
    }

    @Suppress("DEPRECATION")
    private fun fromHtmlCompat(source: String, flags: Int): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(source, flags)
        else Html.fromHtml(source)
}