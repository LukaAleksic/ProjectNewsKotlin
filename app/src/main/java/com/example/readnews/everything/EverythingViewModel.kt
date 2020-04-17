package com.example.readnews.everything

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.readnews.database.getDatabase
import com.example.readnews.repository.AbsRepository
import com.example.readnews.repository.NewsRepository
import kotlinx.coroutines.*
import timber.log.Timber


class EverythingViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    private var _eventGenericError = MutableLiveData(false)
    val eventGenericError: LiveData<Boolean>
        get() = _eventGenericError

    private var _isGenericErrorShown = MutableLiveData(false)

    val isGenericErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    /**
     * The data source this ViewModel will fetch results from.
     */
    private val newsRepository = NewsRepository(getDatabase(application))

    /**
     * A journal of news displayed on the screen.
     */
    val journal = newsRepository.news

    fun filter(countryFilter: String, sortBy: String, from: String, to: String, keyword: String) {
        viewModelScope.launch {
            val apiResponse =
                newsRepository.updateEverything(countryFilter, sortBy, from, to, keyword)
            when (apiResponse) {
                is AbsRepository.ResultWrapper.NetworkError ->                 // Show a Toast error message and hide the progress bar.
                    _eventNetworkError.value = true
                is AbsRepository.ResultWrapper.GenericError -> {
                    Timber.e(apiResponse.error.toString())
                    _eventGenericError.value = true
                }
                is AbsRepository.ResultWrapper.Success -> {
                    newsRepository.updateDatabase(apiResponse.value)
                    _isNetworkErrorShown.value = false
                    _eventNetworkError.value = false
                    _isGenericErrorShown.value = false
                    _eventGenericError.value = false
                }
            }
        }
    }

}
