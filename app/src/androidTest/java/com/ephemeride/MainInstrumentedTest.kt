package com.ephemeride

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.toolbox.Volley
import com.ephemeride.wikipedia.WikipediaAPIClient
import com.ephemeride.wikipedia.WikipediaQuery

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.logging.Logger

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainInstrumentedTest {
    val logger = Logger.getLogger("MainInstrumentedTest")

    var queryResult = ""

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.ephemeride", appContext.packageName)
    }


    @Test
    fun WikipediaClientSandbox() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val query = WikipediaQuery(
            action = "parse",
            page = "Wikipédia:Éphéméride/22 mai"
        ).also { logger.info(it.toString()) }


        Volley.newRequestQueue(appContext).apply {
            add(WikipediaAPIClient.makeRequest(query) {
                val result = WikipediaAPIClient
                    .jsonToEventList(it)
                queryResult = result.toString()
            })
        }

        Thread.sleep(5000)
        logger.info(queryResult)
    }

    @Test
    fun WikipediaAPIClientTestEphemeride() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        WikipediaAPIClient.requestEphemeride(appContext) {

        }
        Thread.sleep(5000)
    }
}
