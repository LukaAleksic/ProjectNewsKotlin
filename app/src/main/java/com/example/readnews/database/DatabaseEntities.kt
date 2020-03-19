package com.example.readnews.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.readnews.domain.Article

/**
 * DatabaseNews represents a news entity in the database.
 */
@Entity
data class DatabaseNews(

    //val source: String,
    val author: String?,
    val title: String,
    val description: String?,
    @PrimaryKey
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

/**
 * Map DatabaseNews to domain entities
 */
fun List<DatabaseNews>.asDomainModel(): List<Article> {
    return map {
        Article(
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
