package com.module.admin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.domain.api.model.User
import com.module.domain.api.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences,
    private val googleSignInClient: GoogleSignInClient
) : BaseViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> = _userInfo

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _profileIsLoading = MutableLiveData<Boolean>()
    val profileIsLoading: LiveData<Boolean> = _profileIsLoading

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
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

    fun logout() {
        viewModelScope.launch {
            _profileIsLoading.postValue(true)
            userRepository.logout().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Logging out...")
                        _profileIsLoading.postValue(true)
                    }
                    is Result.Success -> {
                        FirebaseAuth.getInstance().signOut()
                        googleSignInClient.signOut()
                        FirebaseAuth.getInstance().signOut()
                        clearUserPreferences()
                        _profileIsLoading.postValue(false)
                        _logoutSuccess.postValue(true)
                        Timber.d("Logout successful, user preferences cleared")
                    }
                    is Result.Error -> {
                        _profileIsLoading.postValue(false)
                        _errorMessage.postValue(result.message)
                        Timber.e(result.exception, "Error logging out: ${result.message}")
                    }
                }
            }
        }
    }

    private fun clearUserPreferences() {
        Timber.d("Clearing user preferences")
        appPreferences.remove(PreferenceKey.USER_ID)
        appPreferences.remove(PreferenceKey.USER_NAME)
        appPreferences.remove(PreferenceKey.USER_EMAIL)
        appPreferences.remove(PreferenceKey.USER_USERNAME)
        appPreferences.remove(PreferenceKey.USER_ROLE)
        appPreferences.remove(PreferenceKey.USER_ADDRESS)
        appPreferences.remove(PreferenceKey.USER_AVATAR)
        appPreferences.remove(PreferenceKey.USER_PHONE)
        appPreferences.remove(PreferenceKey.ACCESS_TOKEN)
        appPreferences.remove(PreferenceKey.REFRESH_TOKEN)
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(null)
    }
}