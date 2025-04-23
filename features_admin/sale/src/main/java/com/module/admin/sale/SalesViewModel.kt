// SalesViewModel.kt
package com.module.admin.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.AdminHome
import com.module.domain.api.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val saleRepository: SaleRepository
) : BaseViewModel() {
    private val _categories = MutableLiveData<List<AdminHome.Category>>()
    val categories: LiveData<List<AdminHome.Category>> = _categories

    private val _items = MutableLiveData<List<AdminHome.Item>>()
    val items: LiveData<List<AdminHome.Item>> = _items

    private val _selectedItems = MutableLiveData<List<AdminHome.Item>>(emptyList())
    val selectedItems: LiveData<List<AdminHome.Item>> = _selectedItems

    private val _totalPrice = MutableLiveData<Long>(0)
    val totalPrice: LiveData<Long> = _totalPrice

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            saleRepository.getCategories().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Loading categories...")
                    }

                    is Result.Success -> {
                        _categories.postValue(result.data)
                    }

                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading categories")
                    }
                }
            }
        }
    }

    fun loadItemsForCategory(categoryId: String) {
        viewModelScope.launch {
            val criteria = mapOf("categories" to categoryId)
            saleRepository.getItemsByCriteria(criteria).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Loading items...")
                    }

                    is Result.Success -> {
                        _items.postValue(result.data)
                    }

                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading items")
                    }
                }
            }
        }
    }

    fun addItemToCart(item: AdminHome.Item) {
        val currentList = _selectedItems.value.orEmpty().toMutableList()
        currentList.add(item)
        _selectedItems.postValue(currentList)
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = _selectedItems.value?.sumOf { it.price } ?: 0
        _totalPrice.postValue(total)
    }
}