/*
 * Copyright (c) 2020. Heelo Mangola
 */

package com.ephemeride.wikipedia

import com.ephemeride.R
import com.ephemeride.utils.ResourcesUtils
import com.ephemeride.utils.getLocal
import java.util.*

object WikipediaUtils {
    val ephemeridePageTitle: String get() = ResourcesUtils.getLocal().getString(R.string.ephemeride_page_title, Date())
    val wikiBaseURl: String get() = ResourcesUtils.getLocal().getString(R.string.wikipedia_base_url)
}
