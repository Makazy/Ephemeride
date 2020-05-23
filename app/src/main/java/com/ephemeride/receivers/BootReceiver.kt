/*
 * Copyright (c) 2020. Heelo Mangola
 */

package com.ephemeride.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ephemeride.controllers.EphemerideController

class BootReceiver: BroadcastReceiver() {
    @Suppress("NAME_SHADOWING")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { context -> intent?.let { intent ->
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                EphemerideController.startDailyEphemeride(context)
            }
        }}
    }
}