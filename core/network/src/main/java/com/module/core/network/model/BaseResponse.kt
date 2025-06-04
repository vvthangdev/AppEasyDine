package com.module.core.network.model
//
//import com.google.gson.annotations.SerializedName
//
//data class BaseResponse<T>(
//    @SerializedName("status") val status: String,
//    @SerializedName("message") val message: String,
//    @SerializedName("data") val data: T? = null
//) {
//    fun isSuccess(): Boolean = status.equals("SUCCESS", ignoreCase = true)
//}

import com.google.gson.annotations.SerializedName

enum class ApiStatus {
    SUCCESS,
    ERROR
}

data class BaseResponse<T>(
    @SerializedName("status") private val rawStatus: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null
) {
    private val status: ApiStatus
        get() = when (rawStatus.uppercase()) {
            "SUCCESS" -> ApiStatus.SUCCESS
            else -> ApiStatus.ERROR // Mặc định là ERROR thay vì throw
        }

    fun isSuccess(): Boolean = status == ApiStatus.SUCCESS
}