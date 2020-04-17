package com.example.readnews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.readnews.BuildConfig.BASE_URL
import com.example.readnews.database.DatabaseNews
import com.example.readnews.database.NewsDatabase
import com.example.readnews.database.NewsMapper
import com.example.readnews.domain.Article
import com.example.readnews.network.*
import com.example.readnews.util.APIKEY
import com.example.readnews.util.FRCOUNTRY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class NewsRepository(
    private val database: NewsDatabase,
    private val apiProvider: ApiProvider = ApiProvider()
) : AbsRepository() {

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

            val journal = apiProvider.buildApi(BASE_URL, ReadNewsService::class.java).getJournal(
                FRCOUNTRY,
                APIKEY
            )

            database.newsDao.insertAll(NewsMapper.networkNewsContainerAsDatabaseModel(journal))
        }
    }

    suspend fun updateNews(
        businessFilter: String = "",
        countryFilter: String = ""
    ): ResultWrapper<List<DatabaseNews>> {
        return safeApiCall(Dispatchers.IO) {
            NewsMapper.networkNewsContainerAsDatabaseModel(apiAccess(businessFilter, countryFilter))
        }
    }

    private suspend fun apiAccess(
        businessFilter: String,
        countryFilter: String
    ): NetworkNewsContainer {
        val journal: NetworkNewsContainer
        val cFilter = getCountryCode(countryFilter)

        journal = if (businessFilter.isNotEmpty()) {
            apiProvider.buildApi(BASE_URL, ReadNewsService::class.java).getJournal(
                cFilter,
                businessFilter,
                APIKEY
            )
        } else {
            apiProvider.buildApi(BASE_URL, ReadNewsService::class.java).getJournal(
                cFilter,
                APIKEY
            )
        }
        return journal
    }

    suspend fun updateDatabase(journal: List<DatabaseNews>) {
        withContext(Dispatchers.IO) {
            database.newsDao.deleteAll()
            database.newsDao.insertAll(journal)
        }
    }

    private fun getCountryCode(countryName: String): String {
        val cFilter: String
        when (countryName) {
            "Argentina" -> cFilter = "ar"
            "Australia" -> cFilter = "au"
            "Austria" -> cFilter = "at"
            "Belgium" -> cFilter = "be"
            "Brazil" -> cFilter = "br"
            "Canada" -> cFilter = "ca"
            "China" -> cFilter = "cn"
            "France" -> cFilter = "fr"
            "Germany" -> cFilter = "de"
            "Greece" -> cFilter = "gr"
            "India" -> cFilter = "in"
            "Indonesia" -> cFilter = "id"
            "Italy" -> cFilter = "it"
            "Japan" -> cFilter = "jp"
            "Mexico" -> cFilter = "mx"
            "New Zealand" -> cFilter = "nz"
            "Singapore" -> cFilter = "sg"
            "United Kingdom" -> cFilter = "gb"
            "United States" -> cFilter = "us"
            else -> cFilter = "fr"
        }
        return cFilter
    }

    suspend fun updateEverything(
        language: String,
        sortBy: String,
        from: String,
        to: String,
        keyword: String
    ): ResultWrapper<List<DatabaseNews>> {
        val goodKeyword = keyword.replace(";", "+")
        if (language.isNotEmpty()) {
            return safeApiCall(Dispatchers.IO) {
                NewsMapper.networkNewsContainerAsDatabaseModel(
                    apiProvider.buildApi(BASE_URL, ReadNewsService::class.java)
                        .getJournalEverything(
                            getCountryCode(language),
                            sortBy,
                            from,
                            to,
                            goodKeyword,
                            APIKEY
                        )
                )
            }
        } else {
            return safeApiCall(Dispatchers.IO) {
                NewsMapper.networkNewsContainerAsDatabaseModel(
                    apiProvider.buildApi(BASE_URL, ReadNewsService::class.java)
                        .getAllJournalEverything(
                            sortBy,
                            from,
                            to,
                            goodKeyword,
                            APIKEY
                        )
                )
            }
        }
    }
}