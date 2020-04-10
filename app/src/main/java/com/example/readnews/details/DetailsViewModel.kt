package com.example.readnews.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.readnews.database.getDatabase
import com.example.readnews.repository.NewsRepository

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val newsRepository = NewsRepository(getDatabase(application))

    val journal = newsRepository.news
}