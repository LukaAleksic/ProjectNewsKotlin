package com.example.readnews.repository

import com.example.readnews.network.ErrorResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

abstract class AbsRepository() {

    sealed class ResultWrapper<out T> {
        data class Success<out T>(val value: T) : ResultWrapper<T>()
        data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
            ResultWrapper<Nothing>()

        object NetworkError : ResultWrapper<Nothing>()
    }

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(
                    apiCall.invoke()
                )
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(
                            code,
                            errorResponse
                        )
                    }
                    else -> {
                        ResultWrapper.GenericError(
                            null,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            Timber.e(exception.toString())
            return null
        }
    }
}