package com.example.readnews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.readnews.BuildConfig
import com.example.readnews.BuildConfig.BASE_URL
import com.example.readnews.database.NewsDatabase
import com.example.readnews.database.NewsMapper
import com.example.readnews.domain.Article
import com.example.readnews.network.ApiProvider
import com.example.readnews.network.ReadNewsService
import com.example.readnews.util.APIKEY
import com.example.readnews.util.FRCOUNTRY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class NewsRepository(private val database: NewsDatabase, private val apiProvider: ApiProvider = ApiProvider()) {

    val news: LiveData<List<Article>> = Transformations.map(database.newsDao.getNews()) {
        NewsMapper.listDatabaseNewsasDomainModel(it)
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
                val journal = apiProvider.buildApi(BASE_URL,ReadNewsService::class.java).getJournal(
                    FRCOUNTRY,
                    APIKEY
                )
                Timber.d("getJournal done")
                database.newsDao.insertAll(NewsMapper.networkNewsContainerasDatabaseModel(journal))
            }
            catch(e: Exception){
                Timber.e(e.toString())
            }

        }
    }
}