package com.example.readnews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.readnews.database.NewsDatabase
import com.example.readnews.database.asDomainModel
import com.example.readnews.domain.Article
import com.example.readnews.network.ReadNewsNetwork
import com.example.readnews.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class NewsRepository(private val database: NewsDatabase) {

    val news: LiveData<List<Article>> = Transformations.map(database.newsDao.getNews()) {
        it.asDomainModel()
    }
    /**
     * Refresh the news stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    suspend fun refreshNews() {
        withContext(Dispatchers.IO) {
            Timber.d("getJournal start")
            try {
                val journal = ReadNewsNetwork.readnews.getJournal(
                    "fr",
                   //"business",
                    "2c64fe5d063645f58a5cd563308d0e7c"
                )
                Timber.d("getJournal done")
                database.newsDao.insertAll(journal.asDatabaseModel())
            }
            catch(e: Exception){
                Timber.e(e.toString())
            }

        }
    }
}