package com.module.core.network.interceptor

import android.content.Context
import com.module.core.network.isNetworkAvailable
import com.module.core.network.model.NoInternetException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!context.isNetworkAvailable()) {
            throw NoInternetException()
        }
        return chain.proceed(request)
    }
}