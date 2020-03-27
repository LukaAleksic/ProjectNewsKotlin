package com.example.readnews.headlines

import com.example.readnews.domain.Article

/**
 * Click listener for Articles. By giving the block a name it helps a reader understand what it does.
 *
 */
class NewsClick(val block: (Article) -> Unit) {
    /**
     * Called when an article is clicked
     *
     * @param news the news that was clicked
     */
    fun onClick(news: Article) = block(news)
}