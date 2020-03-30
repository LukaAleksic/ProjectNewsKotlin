package com.example.readnews.database

import com.example.readnews.domain.Article
import com.example.readnews.network.NetworkNewsContainer

object NewsMapper {

    /**
     * Map DatabaseNews to domain entities
     */
    fun listDatabaseNewsAsDomainModel(news : List<DatabaseNews>): List<Article> {
        return news.map {
            Article(
                author = it.author,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content
            )
        }
    }

    /**
     * Convert Network results to database objects
     */
    fun networkNewsContainerAsDatabaseModel(news : NetworkNewsContainer): List<DatabaseNews> {
        return news.articles.map {
            DatabaseNews(
                author = it.author,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content)
        }
    }
}

