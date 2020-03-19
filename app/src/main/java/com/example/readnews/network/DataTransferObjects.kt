
package com.example.readnews.network

import com.example.readnews.database.DatabaseNews
import com.example.readnews.domain.Article
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
    //val source: String,
    @Json(name="author") val author: String? = null,
    val title: String,
    @Json(name="description") val description: String? = null,
    val url: String,
    @Json(name="urlToImage") val urlToImage: String? = null,
    val publishedAt: String,
    @Json(name="content") val content: String? = null)


/**
 * Convert Network results to database objects
 */
fun NetworkNewsContainer.asDatabaseModel(): List<DatabaseNews> {
    return articles.map {
        DatabaseNews(
            //source = it.source,
            author = it.author,
            title = it.title,
            description = it.description,
            url = it.url,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt,
            content = it.content)
    }
}
