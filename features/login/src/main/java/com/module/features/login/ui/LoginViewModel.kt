package com.module.features.login.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.domain.api.model.GoogleLoginRequest
import com.module.domain.api.model.LoginRequest
import com.module.domain.api.model.LoginResponse
import com.module.domain.api.model.UserRole
import com.module.domain.api.repository.UserRepository
import com.module.features.login.R

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {

    val uiState = MutableLiveData<LoginState>()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun initGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("355702919962-avftoucolas8hb8nnp609h4go66ul9cm.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun startGoogleSignIn(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun googleLogin(idToken: String) {
        Timber.d("Starting Google Login with idToken: ${idToken.take(10)}...")
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            try {
                Timber.d("Authenticating with Firebase...")
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                Timber.d("Firebase authentication successful")

                // Lấy idToken từ Firebase Authentication
                val firebaseIdToken = authResult.user?.getIdToken(false)?.await()?.token
                if (firebaseIdToken == null) {
                    Timber.e("Failed to get Firebase idToken")
                    isLoading.postValue(false)
                    uiState.postValue(LoginState.LoginFailed(Exception("Failed to get Firebase idToken")))
                    return@launch
                }
                Timber.d("Firebase idToken: ${firebaseIdToken.take(10)}...")

                // Gửi Firebase idToken đến backend
                Timber.d("Sending Firebase idToken to backend...")
                repository.googleLogin(GoogleLoginRequest(firebaseIdToken)).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            Timber.d("Google Login: Loading")
                            isLoading.postValue(true)
                        }
                        is Result.Success -> {
                            Timber.d("Google Login: Success - $result")
                            isLoading.postValue(false)
                            val data = result.data
                            if (data != null) {
                                saveUserInformation(data)
                                val role = UserRole.fromString(data.role)
                                when (role) {
                                    UserRole.ADMIN -> uiState.postValue(LoginState.LoginSuccess(UserRole.ADMIN))
                                    UserRole.STAFF -> uiState.postValue(LoginState.LoginSuccess(UserRole.STAFF))
                                    UserRole.CUSTOMER -> uiState.postValue(LoginState.LoginSuccess(UserRole.CUSTOMER))
                                    null -> uiState.postValue(LoginState.LoginFailed(Exception("Invalid role")))
                                }
                            } else {
                                uiState.postValue(LoginState.LoginFailed(Exception("No user data received")))
                            }
                        }
                        is Result.Error -> {
                            Timber.e(result.exception, "Google Login: Error - ${result.message}")
                            isLoading.postValue(false)
                            uiState.postValue(LoginState.LoginFailed(result.exception))
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Firebase Auth Error: ${e.message}")
                isLoading.postValue(false)
                uiState.postValue(LoginState.LoginFailed(e))
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState.postValue(LoginState.LoginFailed(Exception("Email or password cannot be empty")))
            return
        }

        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.login(LoginRequest(email, password)).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading.postValue(true)
                    }
                    is Result.Success -> {
                        isLoading.postValue(false)
                        val data = result.data
                        if (data != null) {
                            saveUserInformation(data)
                            val role = UserRole.fromString(data.role)
                            when (role) {
                                UserRole.ADMIN -> uiState.postValue(LoginState.LoginSuccess(UserRole.ADMIN))
                                UserRole.STAFF -> uiState.postValue(LoginState.LoginSuccess(UserRole.STAFF))
                                UserRole.CUSTOMER -> uiState.postValue(LoginState.LoginSuccess(UserRole.CUSTOMER))
                                null -> uiState.postValue(LoginState.LoginFailed(Exception("Invalid role")))
                            }
                        } else {
                            uiState.postValue(LoginState.LoginFailed(Exception("No user data received")))
                        }
                    }
                    is Result.Error -> {
                        isLoading.postValue(false)
                        uiState.postValue(LoginState.LoginFailed(result.exception))
                    }
                }
            }
        }
    }

    private fun saveUserInformation(result: LoginResponse) {
        Log.d("LoginViewModel", "saveUserInformation: $result")
        appPreferences.put(PreferenceKey.USER_ID, result.id ?: "")
        appPreferences.put(PreferenceKey.USER_NAME, result.name ?: "")
        appPreferences.put(PreferenceKey.USER_EMAIL, result.email ?: "")
        appPreferences.put(PreferenceKey.USER_USERNAME, result.username ?: "")
        appPreferences.put(PreferenceKey.USER_ROLE, result.role ?: "")
        appPreferences.put(PreferenceKey.USER_ADDRESS, result.address ?: "")
        appPreferences.put(PreferenceKey.USER_AVATAR, result.avatar ?: "")
        appPreferences.put(PreferenceKey.USER_PHONE, result.phone ?: "")
        appPreferences.put(PreferenceKey.ACCESS_TOKEN, result.accessToken ?: "")
        appPreferences.put(PreferenceKey.REFRESH_TOKEN, result.refreshToken ?: "")
        Log.d("LoginViewModel", "saveUserInformation: ${appPreferences.get(PreferenceKey.USER_NAME, "")}")
    }
}

sealed class LoginState {
    data class LoginSuccess(val role: UserRole) : LoginState()
    data class LoginFailed(val e: Throwable?) : LoginState()
}