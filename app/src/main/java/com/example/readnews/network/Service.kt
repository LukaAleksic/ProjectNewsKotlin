package com.example.readnews.network

import retrofit2.http.GET
import retrofit2.http.Query


interface ReadNewsService {

    @GET("top-headlines")
    suspend fun getJournal(@Query("country") country: String,
                   @Query("category") category: String,
                   @Query("apiKey") apiKey : String) : NetworkNewsContainer


    @GET("top-headlines")
    suspend fun getJournal(@Query("country") country: String,
                           @Query("apiKey") apiKey : String) : NetworkNewsContainer
    @GET("everything")
    suspend fun getJournalEverything(@Query("apiKey") apiKey : String) : NetworkNewsContainer

}

