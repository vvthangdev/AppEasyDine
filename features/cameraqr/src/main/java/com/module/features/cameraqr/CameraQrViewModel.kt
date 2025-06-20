package com.module.features.cameraqr

import androidx.lifecycle.MutableLiveData
import com.module.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraQrViewModel @Inject constructor() : BaseViewModel() {
    val qrCodeContent = MutableLiveData<String?>()

    fun setQrCodeContent(content: String?) {
        qrCodeContent.postValue(content)
    }
}