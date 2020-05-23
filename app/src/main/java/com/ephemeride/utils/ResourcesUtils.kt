/*
 * Copyright (c) 2020. Heelo Mangola
 */

@file:Suppress("unused")

package com.ephemeride.utils

import android.content.res.Resources
import com.ephemeride.MainActivity

val ResourcesUtils: Resources? = null

fun Resources?.getLocal(): Resources = MainActivity.instance.resources
fun Resources?.getSystem(): Resources = Resources.getSystem()