package com.hust.vvthang.easydine.ui

import androidx.lifecycle.MutableLiveData
import com.hust.vvthang.easydine.R
import com.module.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdHomeViewModel @Inject constructor() : BaseViewModel() {
    private val _selectedTab = MutableLiveData<Int>(R.id.salesFragment) // Khớp với startDestination
    val selectedTab: MutableLiveData<Int> = _selectedTab

    fun setSelectedTab(tabId: Int) {
        _selectedTab.value = tabId
    }
}