/*
 * Copyright (c) 2020. Heelo Mangola
 */

package com.ephemeride.wikipedia

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.*

object WikipediaAPIClient {
    private val TAG = "WikipediaAPIClient"

    internal fun makeRequest(query: WikipediaQuery, response: (JSONObject) -> Unit): JsonObjectRequest = JsonObjectRequest(
        Request.Method.GET,
        query.toString(),
        null,
        Response.Listener(response),
        Response.ErrorListener { error ->
            Log.e(
                TAG,
                "Unable to request lorem ipsum : ",
                error
            )
        }
    )

    fun requestEphemeride(context: Context, response: (Ephemeride) -> Unit ) {
        WikipediaQuery(
            action = "parse",
            page = WikipediaUtils.ephemeridePageTitle
        )
            .let { query ->
                Volley.newRequestQueue(context).apply {
                    add(makeRequest(
                        query
                    ) { json ->
                        response(
                            Ephemeride(
                                events = jsonToEventList(
                                    json
                                )
                            )
                        )
                    })
                }
            }


    }

    internal fun jsonToEventList(json: JSONObject): List<String> {
        return json
            .getJSONObject("parse")
            .getJSONObject("text")  // Text de la page en HTML
            .getString("*")?.let { html ->
                Jsoup.parse(html).apply {
                    allElements
                        .select("a")
                        .tagName("span")
                        .removeAttr("href")
                        .removeAttr("title")
                }.getElementsByTag("li") // Récupère chaque entrée de la liste d'évènements
                    .map { li -> li.html() }

            }?: listOf()
    }

}


data class Ephemeride(
    val date: Date = Date(),
    val events: List<CharSequence>
)

data class WikipediaQuery(
    val locale: String = "fr",
    val action: String = "query",
    val titles: String? = null,
    val prop: String? = null,
    val rvprop: String? = null,
    val page: String? = null,
    val text: String? = null,
    val contentmodel: String? = null,
    val format: String = "json"
) {
    private val baseUrl: String = "https://$locale.wikipedia.org/w/api.php"
    fun toUri(): Uri? {
        return Uri.parse(baseUrl).buildUpon().apply {
            appendQueryParameter("action", action)
            titles?.let{ appendQueryParameter("titles", it) }
            prop?.let { appendQueryParameter("prop", it) }
            rvprop?.let { appendQueryParameter("rvprop", it) }
            page?.let { appendQueryParameter("page", it) }
            text?.let { appendQueryParameter("text", it) }
            contentmodel?.let { appendQueryParameter("contentmodel", it) }
            appendQueryParameter("format", format)
        }.build()
    }
    override fun toString(): String = toUri().toString()
}

