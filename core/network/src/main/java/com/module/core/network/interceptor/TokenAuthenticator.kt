package com.module.core.network.interceptor

import com.module.core.utils.extensions.shared_preferences.AppPreferences
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val appPreferences: AppPreferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        TODO("Not yet implemented")
    }

    private suspend fun refreshAccessToken(): String = suspendCancellableCoroutine {

    }
}