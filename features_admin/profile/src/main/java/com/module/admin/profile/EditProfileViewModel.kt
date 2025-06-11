package com.module.admin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.UpdateUserRequest
import com.module.domain.api.model.User
import com.module.domain.api.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> = _userInfo

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _profileIsLoading = MutableLiveData<Boolean>()
    val profileIsLoading: LiveData<Boolean> = _profileIsLoading

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

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

    fun updateUser(name: String?, phone: String?, address: String?, avatar: String?) {
        viewModelScope.launch {
            _profileIsLoading.postValue(true)
            val request = UpdateUserRequest(
                name = name?.takeIf { it.isNotBlank() },
                bio = address?.takeIf { it.isNotBlank() }, // Sử dụng address làm bio theo UpdateUserRequest
                avatar = avatar?.takeIf { it.isNotBlank() }
            )
            userRepository.updateUser(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Updating user...")
                        _profileIsLoading.postValue(true)
                    }
                    is Result.Success -> {
                        _profileIsLoading.postValue(false)
                        _updateSuccess.postValue(true)
                        Timber.d("User updated successfully")
                        loadUserInfo() // Làm mới thông tin người dùng sau khi cập nhật
                    }
                    is Result.Error -> {
                        _profileIsLoading.postValue(false)
                        _errorMessage.postValue(result.message)
                        Timber.e(result.exception, "Error updating user: ${result.message}")
                    }
                }
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(null)
    }
}