package com.module.admin.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.CartItem
import com.module.domain.api.model.Category
import com.module.domain.api.model.Item
import com.module.domain.api.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : BaseViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    // Lưu trữ danh sách các item đã chọn trực tiếp trong ViewModel để khắc phục vấn đề timing
    private val cartItems = mutableListOf<CartItem>()

    private val _selectedItems = MutableLiveData<List<CartItem>>(emptyList())
    val selectedItems: LiveData<List<CartItem>> = _selectedItems

    private val _totalPrice = MutableLiveData<Long>(0)
    val totalPrice: LiveData<Long> = _totalPrice

    private var allItems: List<Item> = emptyList()

    init {
        loadCategories()
        loadAllItems()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            itemRepository.getAllCategories().collect { result ->
                when (result) {
                    is Result.Loading -> Timber.d("Loading categories...")
                    is Result.Success -> {
                        val allItemsCategory = Category(
                            id = "all_items",
                            name = "Tất cả món ăn",
                            createdAt = "",
                            updatedAt = "",
                            version = 0
                        )
                        _categories.postValue(listOf(allItemsCategory) + (result.data ?: emptyList()))
                    }
                    is Result.Error -> Timber.e(result.exception, "Error loading categories")
                }
            }
        }
    }

    fun loadItemsForCategory(categoryId: String) {
        if (categoryId == "all_items") {
            _items.postValue(allItems)
        } else {
            val filteredItems = allItems.filter { item ->
                item.categories.any { it.id == categoryId }
            }
            _items.postValue(filteredItems)
        }
    }

    private fun loadAllItems() {
        viewModelScope.launch {
            itemRepository.getAllItems().collect { result ->
                when (result) {
                    is Result.Loading -> Timber.d("Loading all items...")
                    is Result.Success -> {
                        allItems = result.data ?: emptyList()
                        _items.postValue(allItems)
                    }
                    is Result.Error -> Timber.e(result.exception, "Error loading all items")
                }
            }
        }
    }

    fun searchItems(query: String) {
        if (query.isBlank()) {
            _items.postValue(allItems)
        } else {
            val filteredItems = allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.nameNoAccents.contains(query, ignoreCase = true)
            }
            _items.postValue(filteredItems)
        }
    }

    fun addItemToCart(item: Item) {
        val existingItemIndex = cartItems.indexOfFirst { it.item.id == item.id }

        if (existingItemIndex != -1) {
            // Update existing item quantity
            val existingItem = cartItems[existingItemIndex]
            val newQuantity = existingItem.quantity + 1
            cartItems[existingItemIndex] = CartItem(existingItem.item, newQuantity)
            Timber.d("CART UPDATED: Increased quantity for ${item.name} to $newQuantity")
        } else {
            // Add new item
            cartItems.add(CartItem(item, 1))
            Timber.d("CART UPDATED: Added new item ${item.name} with quantity 1")
        }

        // Update LiveData with a NEW copy of the list
        _selectedItems.postValue(ArrayList(cartItems))

        // Calculate and update the total price using the updated cartItems list
        updateTotalPrice()
    }

    fun updateItemQuantity(itemId: String, quantity: Int) {
        val index = cartItems.indexOfFirst { it.item.id == itemId }

        if (index != -1) {
            val cartItem = cartItems[index]
            val validQuantity = quantity.coerceAtLeast(0)

            if (validQuantity > 0) {
                // Update the quantity
                cartItems[index] = CartItem(cartItem.item, validQuantity)
                Timber.d("CART UPDATED: Changed quantity to $validQuantity for ${cartItem.item.name}")
            } else {
                // Remove the item
                cartItems.removeAt(index)
                Timber.d("CART UPDATED: Removed item ${cartItem.item.name} from cart")
            }

            // Update LiveData with a NEW copy of the list
            _selectedItems.postValue(ArrayList(cartItems))

            // Calculate and update total price with the updated cartItems
            updateTotalPrice()
        }
    }

    fun updateTotalPrice() {
        // Use the directly maintained cartItems list instead of getting it from LiveData
        Timber.d("Calculating total price from ${cartItems.size} items")

        // Log each item in the cart for debugging
        cartItems.forEach { cartItem ->
            Timber.d("Item: ${cartItem.item.name}, Quantity: ${cartItem.quantity}, Price: ${cartItem.item.price}")
        }

        // Calculate the total price
        val total = if (cartItems.isEmpty()) {
            0L
        } else {
            cartItems.sumOf { cartItem ->
                val itemTotal = cartItem.item.price * cartItem.quantity
                Timber.d("Item subtotal: ${cartItem.item.name} - $itemTotal")
                itemTotal
            }
        }

        Timber.d("Final total price calculation: $total")
        _totalPrice.postValue(total)
    }
}