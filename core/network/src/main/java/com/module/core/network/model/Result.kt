package com.module.core.network.model

//sealed class Result<T>(
//    val data: T?,
//    val exception: Throwable?,
//    val status: Status
//) {
//    class Loading<T> : Result<T>(null, null, Status.LOADING)
//    class Error<T>(exception: Throwable) : Result<T>(null, exception, Status.ERROR)
//    class Success<T>(data: T?) : Result<T>(data, null, Status.SUCCESS)
//}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data class Loading(val isLoading: Boolean = true) : Result<Nothing>()
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}