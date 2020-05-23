package com.ephemeride.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.TextUtilsCompat
import com.ephemeride.R

object NotificationService {
    internal val TAG = "NotificationService"

    public fun createNotificationChannel(context: Context,
                                         channel_id: String,
                                         name: String,
                                         description: String,
                                         importance: Int
    ): Unit {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channel_id, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            getSystemService(context, NotificationManager::class.java)?.createNotificationChannel(
                channel
            )
        }
    }

    internal fun sendNotification(
        context: Context,
        channelId: String,
        notification_id: Int,
        content: NotificationContent,
        decorate: (NotificationCompat.Builder) -> NotificationCompat.Builder = fun (builder: NotificationCompat.Builder) = builder
    ) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(
                notification_id,
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(content.title)
                    .setContentText(content.content)
                    .let { builder -> decorate(builder) }
                    .build()
                    .also { notif ->
                        Log.d(TAG, "sendNotification: $notif" )
                    })
    }

    data class NotificationContent(
        val title: String,
        val content: String
    )
}

fun NotificationService.sendEphemerideNotification(context: Context, title: String, events: List<Spanned>) {
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
            .setStyle(NotificationCompat
                .BigTextStyle().bigText(
                    SpannedString(
                        events.fold(SpannableStringBuilder()) { acc, event ->
                            acc.append(event, "\n")
                        })
                        .also { result ->
                            Log.d(TAG, result.toString())
                        }
                ))
    }
}