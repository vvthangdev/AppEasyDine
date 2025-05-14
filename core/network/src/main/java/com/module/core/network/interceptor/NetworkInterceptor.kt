package com.module.core.network.interceptor

import android.content.Context
import android.util.Log
import com.module.core.network.isNetworkAvailable
import com.module.core.network.model.NoInternetException
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Kiểm tra kết nối mạng
        if (!context.isNetworkAvailable()) {
            throw NoInternetException()
        }

        // Lấy accessToken từ AppPreferences
        val accessToken = appPreferences.get(PreferenceKey.ACCESS_TOKEN, "")
        Timber.d("accessToken: $accessToken")

        // Danh sách các endpoint không cần accessToken
        val publicEndpoints = listOf("/users/login")

        // Xây dựng request mới
        val newRequest = if (accessToken.isNotBlank() && !publicEndpoints.any { request.url.encodedPath.contains(it) }) {
            val modifiedRequest = request.newBuilder()
                .addHeader("Authorization", "$accessToken")
                .build()

            // Log header của yêu cầu
            Log.d("NetworkInterceptor", "Request URL: ${request.url}")
            Log.d("NetworkInterceptor", "Request Headers: ${modifiedRequest.headers}")
            modifiedRequest
        } else {
            // Log header của yêu cầu không có accessToken
            Log.d("NetworkInterceptor", "Request URL: ${request.url}")
            Log.d("NetworkInterceptor", "Request Headers: ${request.headers} (No Authorization header added)")
            request
        }

        return chain.proceed(newRequest)
    }
}