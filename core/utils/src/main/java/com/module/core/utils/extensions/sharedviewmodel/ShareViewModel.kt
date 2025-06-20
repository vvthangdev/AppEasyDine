package com.module.core.utils.extensions.sharedviewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
}

@HiltViewModel
class ShareViewModel @Inject constructor(): BaseViewModel() {
    val selectedTableId = MutableLiveData<String?>()

    fun setSelectedTableId(tableId: String?) {
        selectedTableId.postValue(tableId)
    }
}