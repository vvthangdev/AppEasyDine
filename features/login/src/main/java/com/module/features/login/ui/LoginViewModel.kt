package com.module.features.login.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.domain.api.model.Login
import com.module.domain.api.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {

    val uiState = MutableLiveData<LoginState>()

    fun login(username: String, password: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.login(Login.Request(username, password)).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading.postValue(true)
                    }

                    is Result.Success -> {
                        isLoading.postValue(false)
                        saveUserInformation(result.data)
                        uiState.postValue(LoginState.LoginSuccess)
                    }

                    is Result.Error -> {
                        isLoading.postValue(false)
                        uiState.postValue(LoginState.LoginFailed(result.exception))
                    }
                }
            }
        }
    }

    private fun saveUserInformation(result: Login.Response) {
        Log.d("LoginViewModel", "saveUserInformation: $result")
        // Lưu thông tin user vào SharedPreferences
        appPreferences.put(PreferenceKey.USER_ID, result.id)
        appPreferences.put(PreferenceKey.USER_NAME, result.name)
        appPreferences.put(PreferenceKey.USER_EMAIL, result.email)
        appPreferences.put(PreferenceKey.USER_USERNAME, result.username)
        appPreferences.put(PreferenceKey.USER_ROLE, result.role)
        appPreferences.put(PreferenceKey.USER_ADDRESS, result.address)
        appPreferences.put(PreferenceKey.USER_AVATAR, result.avatar)
        appPreferences.put(PreferenceKey.USER_PHONE, result.phone)
        appPreferences.put(PreferenceKey.ACCESS_TOKEN, result.accessToken)
        appPreferences.put(PreferenceKey.REFRESH_TOKEN, result.refreshToken)

        Log.d("LoginViewModel", "saveUserInformation: ${appPreferences.get(PreferenceKey.USER_NAME, "")}")
    }

}

sealed class LoginState {
    data object LoginSuccess : LoginState()
    data class LoginFailed(val e: Throwable?) : LoginState()
}