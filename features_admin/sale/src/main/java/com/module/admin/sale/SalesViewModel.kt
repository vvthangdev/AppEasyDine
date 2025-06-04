package com.module.admin.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.CartItem
import com.module.domain.api.model.Category
import com.module.domain.api.model.CreateOrderRequest
import com.module.domain.api.model.Item
import com.module.domain.api.model.OrderInfoResponse
import com.module.domain.api.model.OrderItemRequest
import com.module.domain.api.model.Size
import com.module.domain.api.repository.ItemRepository
import com.module.domain.api.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class OrderResultState {
    data class ResultState(val result: Result<Unit>) : OrderResultState()
    object Reset : OrderResultState()
}

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val itemRepository: ItemRepository
) : BaseViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    private val cartItems = mutableListOf<CartItem>()

    private val _selectedItems = MutableLiveData<List<CartItem>>(emptyList())
    val selectedItems: LiveData<List<CartItem>> = _selectedItems

    private val _totalPrice = MutableLiveData<Long>(0)
    val totalPrice: LiveData<Long> = _totalPrice

    private val _orderResult = MutableLiveData<OrderResultState>()
    val orderResult: LiveData<OrderResultState> = _orderResult

    private val _orderInfoResult = MutableLiveData<Result<OrderInfoResponse>>()
    val orderInfoResult: LiveData<Result<OrderInfoResponse>> = _orderInfoResult

    private var allItems: List<Item> = emptyList()

    init {
        loadCategories()
        loadAllItems()
    }

    fun loadOrderInfo(tableId: String? = null, orderId: String? = null) {
        viewModelScope.launch {
            orderRepository.getOrderInfo(orderId, tableId).collect { result ->
                _orderInfoResult.postValue(result)
                if (result is Result.Success) {
                    val orderInfo = result.data
                    cartItems.clear()
                    orderInfo?.itemOrders?.forEach { itemOrder ->
                        val item = allItems.find { it.id == itemOrder.itemId }
                        if (item != null) {
                            val selectedSize = item.sizes.find { it.name == itemOrder.size }
                            val cartItem = CartItem(
                                item = item,
                                quantity = itemOrder.quantity,
                                selectedSize = selectedSize,
                                note = itemOrder.note.takeIf { it?.isNotBlank() == true }
                            )
                            cartItems.add(cartItem)
                        }
                    }
                    _selectedItems.postValue(ArrayList(cartItems))
                    updateTotalPrice()
                }
            }
        }
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

    fun addItemToCart(item: Item, selectedSize: Size? = null) {
        if (item.sizes.isNotEmpty() && selectedSize == null) {
            Timber.d("Item ${item.name} has sizes but no size selected. Trigger dialog.")
            return
        }

        val uniqueKey = "${item.id}_${selectedSize?.id ?: "no_size"}"
        val existingItemIndex = cartItems.indexOfFirst { it.uniqueKey == uniqueKey }

        if (existingItemIndex != -1) {
            val existingItem = cartItems[existingItemIndex]
            val newQuantity = existingItem.quantity + 1
            cartItems[existingItemIndex] = CartItem(existingItem.item, newQuantity, selectedSize, existingItem.note)
            Timber.d("CART UPDATED: Increased quantity for ${item.name} (size: ${selectedSize?.name ?: "default"}) to $newQuantity")
        } else {
            cartItems.add(CartItem(item, 1, selectedSize, null))
            Timber.d("CART UPDATED: Added new item ${item.name} (size: ${selectedSize?.name ?: "default"}) with quantity 1")
        }

        _selectedItems.postValue(ArrayList(cartItems))
        updateTotalPrice()
    }

    fun decreaseItemQuantity(item: Item, selectedSize: Size? = null) {
        val uniqueKey = "${item.id}_${selectedSize?.id ?: "no_size"}"
        val existingItemIndex = cartItems.indexOfFirst { it.uniqueKey == uniqueKey }

        if (existingItemIndex != -1) {
            val existingItem = cartItems[existingItemIndex]
            val newQuantity = existingItem.quantity - 1
            if (newQuantity > 0) {
                cartItems[existingItemIndex] = CartItem(existingItem.item, newQuantity, selectedSize, existingItem.note)
                Timber.d("CART UPDATED: Decreased quantity for ${item.name} to $newQuantity")
            } else {
                cartItems.removeAt(existingItemIndex)
                Timber.d("CART UPDATED: Removed ${item.name} from cart")
            }
            _selectedItems.postValue(ArrayList(cartItems))
            updateTotalPrice()
        }
    }

    fun updateItemQuantity(uniqueKey: String, quantity: Int) {
        val index = cartItems.indexOfFirst { it.uniqueKey == uniqueKey }

        if (index != -1) {
            val cartItem = cartItems[index]
            val validQuantity = quantity.coerceAtLeast(0)

            if (validQuantity > 0) {
                cartItems[index] = CartItem(cartItem.item, validQuantity, cartItem.selectedSize, cartItem.note)
                Timber.d("CART UPDATED: Changed quantity to $validQuantity for ${cartItem.item.name} (size: ${cartItem.selectedSize?.name ?: "default"})")
            } else {
                cartItems.removeAt(index)
                Timber.d("CART UPDATED: Removed item ${cartItem.item.name} (size: ${cartItem.selectedSize?.name ?: "default"}) from cart")
            }

            _selectedItems.postValue(ArrayList(cartItems))
            updateTotalPrice()
        }
    }

    fun updateItemDetails(itemId: String, selectedSize: Size?, note: String?, oldUniqueKey: String) {
        val index = cartItems.indexOfFirst { it.uniqueKey == oldUniqueKey }
        if (index != -1) {
            val cartItem = cartItems[index]
            val newUniqueKey = "${itemId}_${selectedSize?.id ?: "no_size"}"
            val conflictingIndex = cartItems.indexOfFirst { it.uniqueKey == newUniqueKey && it.uniqueKey != oldUniqueKey }

            if (conflictingIndex != -1) {
                val conflictingItem = cartItems[conflictingIndex]
                val newQuantity = conflictingItem.quantity + cartItem.quantity
                cartItems[conflictingIndex] = CartItem(conflictingItem.item, newQuantity, selectedSize, note ?: conflictingItem.note)
                cartItems.removeAt(index)
                Timber.d("Merged item ${cartItem.item.name} (size: ${selectedSize?.name}) into existing item with quantity $newQuantity")
            } else {
                cartItems[index] = CartItem(cartItem.item, cartItem.quantity, selectedSize, note)
                Timber.d("Updated item ${cartItem.item.name}: size=${selectedSize?.name}, note=$note")
            }

            _selectedItems.postValue(ArrayList(cartItems))
            updateTotalPrice()
        }
    }

    fun updateTotalPrice() {
        Timber.d("Calculating total price from ${cartItems.size} items")
        cartItems.forEach { cartItem ->
            Timber.d("Item: ${cartItem.item.name}, Quantity: ${cartItem.quantity}, Size: ${cartItem.selectedSize?.name}, Price: ${cartItem.selectedSize?.price ?: cartItem.item.price}")
        }
        val total = if (cartItems.isEmpty()) {
            0L
        } else {
            cartItems.sumOf { cartItem ->
                val price = cartItem.selectedSize?.price ?: cartItem.item.price
                val itemTotal = price * cartItem.quantity
                Timber.d("Item subtotal: ${cartItem.item.name} - $itemTotal")
                itemTotal
            }
        }
        Timber.d("Final total price calculation: $total")
        _totalPrice.postValue(total)
    }

    fun createOrder(tableId: String?) {
        if (cartItems.isEmpty()) {
            _orderResult.postValue(OrderResultState.ResultState(Result.Error(Exception("Cart is empty"), "Gio hang trong")))
            return
        }

        val now = Instant.now()
        val startTime = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Ho_Chi_Minh"))
            .format(DateTimeFormatter.ISO_INSTANT)
        val endTime = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Ho_Chi_Minh"))
            .withHour(23)
            .withMinute(59)
            .withSecond(0)
            .withNano(0)
            .format(DateTimeFormatter.ISO_INSTANT)

        val orderItems = cartItems.map { cartItem ->
            OrderItemRequest(
                id = cartItem.item.id,
                quantity = cartItem.quantity,
                size = cartItem.selectedSize?.name,
                note = cartItem.note ?: ""
            )
        }

        val request = CreateOrderRequest(
            startTime = startTime,
            endTime = endTime,
            type = if (tableId.isNullOrBlank()) "takeaway" else "reservation",
            status = "pending",
            tables = if (tableId.isNullOrBlank()) emptyList() else listOf(tableId),
            items = orderItems
        )

        viewModelScope.launch {
            orderRepository.createOrder(request).collect { result ->
                _orderResult.postValue(OrderResultState.ResultState(result))
                if (result is Result.Success) {
                    cartItems.clear()
                    _selectedItems.postValue(emptyList())
                    updateTotalPrice()
                    // Reset orderResult to prevent re-triggering
                    _orderResult.postValue(OrderResultState.Reset)
                }
            }
        }
    }
}