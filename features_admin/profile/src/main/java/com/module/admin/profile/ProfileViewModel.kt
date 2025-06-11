package com.module.admin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.User
import com.module.domain.api.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> = _userInfo

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _profileIsLoading = MutableLiveData<Boolean>()
    val profileIsLoading: LiveData<Boolean> = _profileIsLoading

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _profileIsLoading.postValue(true)
            userRepository.getUserInfo().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Loading user info...")
                        _profileIsLoading.postValue(true)
                    }
                    is Result.Success -> {
                        _profileIsLoading.postValue(false)
                        _userInfo.postValue(result.data)
                        Timber.d("User info loaded: ${result.data?.username}")
                    }
                    is Result.Error -> {
                        _profileIsLoading.postValue(false)
                        _errorMessage.postValue(result.message)
                        Timber.e(result.exception, "Error loading user info: ${result.message}")
                    }
                }
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(null)
    }
}