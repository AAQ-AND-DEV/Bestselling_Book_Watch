package com.aaqanddev.bestsellingbookwatch.data

sealed class AppResult<out T: Any> {
    data class Success<out T: Any>(val data: T) : AppResult<T>()
    data class Error(val message: String?, val statusCode: Int? = null):
            AppResult<Nothing>()
}

val AppResult<*>.succeeded
    get() = this is AppResult.Success


