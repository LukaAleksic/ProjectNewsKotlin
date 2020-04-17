package com.example.readnews.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DatabaseNews represents a news entity in the database.
 */
@Entity
data class DatabaseNews(

    val author: String?,
    val title: String?,
    val description: String?,
    @PrimaryKey
    val url: String,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

