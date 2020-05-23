/*
 * Copyright (c) 2020. Heelo Mangola
 */

package com.ephemeride.controllers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ephemeride.R
import com.ephemeride.receivers.EphemerideReceiver
import java.util.*

object EphemerideController {
    private val TAG = "EphemerideController"

    private val calendar = GregorianCalendar(Locale.FRENCH).apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 7)
        set(Calendar.MINUTE, 30)
    }

    fun startDailyEphemeride(context: Context) {
        (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.let { alarmManager ->
            Intent(context, EphemerideReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }.let { alarmIntent ->
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
                )
            }
        }?: Log.e(TAG, "Unable to get ALARM_SERVICE")
    }
}

/**
 * Sends a low priority notification with historical facts about today
 */
fun NotificationService.sendEphemerideNotification(context: Context, title: String, action: Intent, events: List<Spanned>) {
    val channelId = context.getString(R.string.ephemeride_channel_id)

    sendNotification(
        context,
        channelId,
        1,
        NotificationService.NotificationContent(
            title,
            events[0].toString()
        )
    ) { builder ->
        builder
            .setContentIntent(
                PendingIntent.getActivity(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)
            )
            .setPriority(2)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat
                .BigTextStyle().bigText(
                    SpannedString(
                        events.fold(SpannableStringBuilder()) { acc, event ->
                            acc.append(event, "\n")
                        })
                        .also { result ->
                            Log.d(
                                TAG,
                                result.toString()
                            )
                        }
                ))
    }
}