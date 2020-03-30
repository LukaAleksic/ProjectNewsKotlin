package com.example.readnews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.readnews.BuildConfig.BASE_URL
import com.example.readnews.database.NewsDatabase
import com.example.readnews.database.NewsMapper
import com.example.readnews.domain.Article
import com.example.readnews.network.ApiProvider
import com.example.readnews.network.NetworkNewsContainer
import com.example.readnews.network.ReadNewsService
import com.example.readnews.util.APIKEY
import com.example.readnews.util.FRCOUNTRY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class NewsRepository(private val database: NewsDatabase, private val apiProvider: ApiProvider = ApiProvider()) {

    val news: LiveData<List<Article>> = Transformations.map(database.newsDao.getNews()) {
        NewsMapper.listDatabaseNewsAsDomainModel(it)
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

            val journal = apiProvider.buildApi(BASE_URL,ReadNewsService::class.java).getJournal(
                FRCOUNTRY,
                APIKEY
            )

            database.newsDao.insertAll(NewsMapper.networkNewsContainerAsDatabaseModel(journal))
        }
    }

    suspend fun FilterNews(businessFilter: String, countryFilter: String){
        withContext(Dispatchers.IO) {
            val journal: NetworkNewsContainer
            var cFilter = countryFilter
            when(countryFilter){
                    "Argentina" -> cFilter = "ar"
                    "Australia"-> cFilter = "au"
                   "Austria"-> cFilter = "at"
                   "Belgium"-> cFilter = "be"
                    "Brazil"-> cFilter = "br"
                    "Canada"-> cFilter = "ca"
                    "China"-> cFilter = "cn"
                    "France"-> cFilter = "fr"
                    "Germany"-> cFilter = "de"
                    "Greece"-> cFilter = "gr"
                    "India"-> cFilter = "in"
                    "Indonesia"-> cFilter = "id"
                    "Italy"-> cFilter = "it"
                    "Japan"-> cFilter = "jp"
                    "Mexico"-> cFilter = "mx"
                    "New Zealand"-> cFilter = "nz"
                    "Singapore"-> cFilter = "sg"
                    "United Kingdom"-> cFilter = "gb"
                    "United States"-> cFilter = "us"
            }

            if(businessFilter!= "" && countryFilter!="") {
                journal = apiProvider.buildApi(BASE_URL, ReadNewsService::class.java).getJournal(
                    cFilter,
                    businessFilter,
                    APIKEY
                )
            }
            else{
                journal = apiProvider.buildApi(BASE_URL,ReadNewsService::class.java).getJournal(
                    cFilter,
                    APIKEY
                )
            }

            database.newsDao.deleteAll()
            database.newsDao.insertAll(NewsMapper.networkNewsContainerAsDatabaseModel(journal))
        }
    }
}