package com.module.features.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.module.core.ui.base.BaseViewModel
import javax.inject.Inject

class AreaSaleViewModel @Inject constructor() : BaseViewModel() {
    private val _selectedTableId = MutableLiveData<String?>()
    val selectedTableId: LiveData<String?> = _selectedTableId

    private val _selectedTableNumber = MutableLiveData<Int?>()
    val selectedTableNumber: LiveData<Int?> = _selectedTableNumber

    fun setSelectedTableId(tableId: String?) {
        _selectedTableId.value = tableId
    }

    fun setSelectedTableNumber(tableNumber: Int?) {
        _selectedTableNumber.value = tableNumber
    }

    fun clearSelectedTableId() {
        _selectedTableId.value = null
    }

    fun clearSelectedTableNumber() {
        _selectedTableNumber.value = null
    }
}