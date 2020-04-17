package com.example.readnews.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * NewsHolder holds a list of News.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "articles": []
 * }
 */

@JsonClass(generateAdapter = true)
data class NetworkNewsContainer(val articles: List<NetworkNews>)


@JsonClass(generateAdapter = true)
data class NetworkNews(
    @Json(name = "author") val author: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    val url: String,
    @Json(name = "urlToImage") val urlToImage: String? = null,
    @Json(name = "publishedAt") val publishedAt: String? = null,
    @Json(name = "content") val content: String? = null
)

