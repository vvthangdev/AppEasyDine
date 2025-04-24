package com.module.admin.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.AdminHome
import com.module.domain.api.model.CartItem
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

    private val _selectedItems = MutableLiveData<List<CartItem>>(emptyList())
    val selectedItems: LiveData<List<CartItem>> = _selectedItems

    private val _totalPrice = MutableLiveData<Long>(0)
    val totalPrice: LiveData<Long> = _totalPrice

    init {
        loadCategories()
        loadAllItems()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            saleRepository.getCategories().collect { result ->
                when (result) {
                    is Result.Loading -> Timber.d("Loading categories...")
                    is Result.Success -> {
                        val allItemsCategory = AdminHome.Category(
                            _id = "all_items",
                            name = "Tất cả món ăn",
                            description = "Hiển thị toàn bộ món ăn",
                            createdAt = "",
                            updatedAt = "",
                            __v = 0
                        )
                        _categories.postValue(listOf(allItemsCategory) + result.data)
                    }
                    is Result.Error -> Timber.e(result.exception, "Error loading categories")
                }
            }
        }
    }

    fun loadItemsForCategory(categoryId: String) {
        if (categoryId == "all_items") {
            loadAllItems()
        } else {
            val criteria = mapOf("categories" to categoryId)
            viewModelScope.launch {
                saleRepository.getItemsByCriteria(criteria).collect { result ->
                    when (result) {
                        is Result.Loading -> Timber.d("Loading items...")
                        is Result.Success -> _items.postValue(result.data)
                        is Result.Error -> Timber.e(result.exception, "Error loading items")
                    }
                }
            }
        }
    }

    private fun loadAllItems() {
        viewModelScope.launch {
            saleRepository.getAllItems().collect { result ->
                when (result) {
                    is Result.Loading -> Timber.d("Loading all items...")
                    is Result.Success -> _items.postValue(result.data)
                    is Result.Error -> Timber.e(result.exception, "Error loading all items")
                }
            }
        }
    }

    fun addItemToCart(item: AdminHome.Item) {
        val currentList = _selectedItems.value.orEmpty().toMutableList()
        val existingItem = currentList.find { it.item._id == item._id }
        if (existingItem != null) {
            existingItem.quantity += 1 // Increase quantity
        } else {
            currentList.add(CartItem(item, 1)) // Add new with quantity 1
        }
        _selectedItems.postValue(currentList)
        updateTotalPrice()
    }

    fun updateItemQuantity(itemId: String, quantity: Int) {
        val currentList = _selectedItems.value.orEmpty().toMutableList()
        val cartItem = currentList.find { it.item._id == itemId }
        if (cartItem != null) {
            if (quantity > 0) {
                cartItem.quantity = quantity // Update quantity
            } else {
                currentList.remove(cartItem) // Remove if quantity <= 0
            }
            _selectedItems.postValue(currentList)
            updateTotalPrice()
        }
    }

    private fun updateTotalPrice() {
        val total = _selectedItems.value?.sumOf { it.item.price * it.quantity } ?: 0
        _totalPrice.postValue(total)
    }
}