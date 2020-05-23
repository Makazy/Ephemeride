/*
 * Copyright (c) 2020. Heelo Mangola
 */

package com.ephemeride

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ephemeride.controllers.EphemerideController
import com.ephemeride.controllers.NotificationService
import com.ephemeride.receivers.BootReceiver
import com.ephemeride.receivers.EphemerideReceiver
import com.ephemeride.wikipedia.WikipediaUtils

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var channelId: String = ""
    private var channelName: String = ""
    private var channelDescription: String = ""

    companion object {
        lateinit var instance: MainActivity private set // Make app context available outside activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.main_activity)
        channelId = getString(R.string.ephemeride_channel_id)?: ""
        channelName = getString(R.string.ephemeride_channel_name)
        channelDescription = getString(R.string.ephemeride_channel_description)

        // Create notification channel for ephemeride
        NotificationService.createNotificationChannel(
            this,
            channelId,
            channelName,
            channelDescription,
            3
        )

        // Enable BootReceiver to restart ephemeride's notifications on reboot
        enableBootReceiver()

        // Start daily ephemeride notifications
        EphemerideController.startDailyEphemeride(this)
    }

    @Suppress("UNUSED_PARAMETER")
    fun notifyMe(view: View) {
        EphemerideReceiver().onReceive(this, Intent(
            Intent.ACTION_VIEW,
            Uri.parse(WikipediaUtils.wikiBaseURl+ WikipediaUtils.ephemeridePageTitle)
        ))
    }

    private fun enableBootReceiver() = ComponentName(this, BootReceiver::class.java).let { bootReceiver ->
        packageManager.setComponentEnabledSetting(
            bootReceiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}