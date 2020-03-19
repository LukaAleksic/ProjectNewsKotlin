package com.example.readnews.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ReadNewsService {

    @GET("top-headlines")
    suspend fun getJournal(@Query("country") country: String,
                   @Query("category") category: String,
                   @Query("apiKey") apiKey : String) : NetworkNewsContainer

    @GET("top-headlines")
    suspend fun getJournal(@Query("apiKey") apiKey : String) : NetworkNewsContainer

    @GET("top-headlines")
    suspend fun getJournal(@Query("country") country: String,
                           @Query("apiKey") apiKey : String) : NetworkNewsContainer

}


object ReadNewsNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(
                "https://newsapi.org/v2/")
        .addConverterFactory(MoshiConverterFactory.create())
        //.addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val readnews = retrofit.create(ReadNewsService::class.java)

}
