package com.module.features.login.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.SignUpRequest
import com.module.domain.api.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: UserRepository
) : BaseViewModel() {

    val uiState = MutableLiveData<SignUpState>()

    fun signUp(email: String, name: String, username: String, phone: String,  password: String) {
        // Basic input validation
        if (email.isBlank() || name.isBlank() || username.isBlank() || phone.isBlank() ||  password.isBlank()) {
            uiState.postValue(SignUpState.SignUpFailed(Exception("All fields except bio are required")))
            return
        }

        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val request = SignUpRequest(email, name, username, phone, password)
            repository.signUp(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading.postValue(true)
                    }
                    is Result.Success -> {
                        isLoading.postValue(false)
                        val data = result.data
                        if (data != null) {
                            Log.d("SignUpViewModel", "Sign up successful: ${data.username}")
                            uiState.postValue(SignUpState.SignUpSuccess(data.username))
                        } else {
                            uiState.postValue(SignUpState.SignUpFailed(Exception("No response data")))
                        }
                    }
                    is Result.Error -> {
                        isLoading.postValue(false)
                        uiState.postValue(SignUpState.SignUpFailed(result.exception))
                    }
                }
            }
        }
    }
}

sealed class SignUpState {
    data class SignUpSuccess(val username: String) : SignUpState()
    data class SignUpFailed(val e: Throwable?) : SignUpState()
}