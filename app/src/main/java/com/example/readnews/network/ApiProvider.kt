package com.example.readnews.network


import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiProvider {
    fun <T> buildApi(baseUrl: String, interf: Class<T>): T {
        val retrofitBuilder = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofitBuilder.create(interf)
    }
}