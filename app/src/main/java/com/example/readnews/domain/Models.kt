package com.example.readnews.domain

import com.example.readnews.util.smartTruncate


data class Article(//val source: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
) {

    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String?
        get() = description?.smartTruncate(130)
}