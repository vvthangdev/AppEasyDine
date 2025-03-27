package com.module.core.network.model

import com.google.gson.annotations.SerializedName
import java.net.HttpURLConnection

data class BaseResponse<T>(
    @SerializedName("code") var code: Int,
    @SerializedName("message") var message: String,
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: T? = null,
) {
    fun isSuccess() = code in HttpURLConnection.HTTP_OK..HttpURLConnection.HTTP_ACCEPTED
}