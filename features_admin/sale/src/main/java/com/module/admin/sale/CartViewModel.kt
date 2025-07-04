package com.module.admin.sale

import androidx.lifecycle.MutableLiveData
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.core.utils.extensions.sharedviewmodel.ShareViewModel
import com.module.domain.api.model.CartItem
import com.module.domain.api.model.CreateOrderRequest
import com.module.domain.api.model.Item
import com.module.domain.api.model.OrderItemRequest
import com.module.domain.api.model.ReserveTableRequest
import com.module.domain.api.model.Size
import com.module.domain.api.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class CartState {
    data class CartUpdated(val cartItems: List<CartItem>, val totalPrice: Long) : CartState()
    data class Error(val exception: Throwable?) : CartState()
    data object ReservationSuccess : CartState()
    data class ReservationError(val message: String?) : CartState()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val appPreferences: AppPreferences,
//    private val shareViewModel: ShareViewModel khong inject duoc viewmodel khac vao day
) : BaseViewModel() {

    val cartState = MutableLiveData<CartState>()
    val cartItems = mutableListOf<CartItem>()
    val role = appPreferences.get(PreferenceKey.USER_ROLE, "")

    init {
        Timber.d("CartViewModel initialized with role: $role")
    }

    fun addItemToCart(item: Item, quantity: Int = 1, selectedSize: Size? = null, note: String? = null) {
        val uniqueKey = "${item.id}_${selectedSize?.name ?: "no_size"}"

        val newCartItems = cartItems.map {
            if (it.uniqueKey == uniqueKey) {
                it.copy(quantity = it.quantity + quantity, note = note ?: it.note)
            } else {
                it
            }
        }.toMutableList()

        if (newCartItems.none { it.uniqueKey == uniqueKey }) {
            newCartItems.add(CartItem(item, quantity, selectedSize, note))
            Timber.d("Added new item: ${item.name}, quantity: $quantity, size: ${selectedSize?.name}")
        } else {
            Timber.d("Increased quantity for ${item.name}")
        }

        cartItems.clear()
        cartItems.addAll(newCartItems)

        updateCartState()
    }

    fun removeItemFromCart(uniqueKey: String) {
        cartItems.removeAll { it.uniqueKey == uniqueKey }
        Timber.d("Removed item with uniqueKey: $uniqueKey")
        updateCartState()
    }

    fun updateItemQuantity(uniqueKey: String, newQuantity: Int) {
        val newCartItems = cartItems.mapNotNull { item ->
            if (item.uniqueKey == uniqueKey) {
                if (newQuantity <= 0) {
                    Timber.d("Removed item with uniqueKey: $uniqueKey due to quantity <= 0")
                    null
                } else {
                    Timber.d("Updating quantity for ${item.item.name} from ${item.quantity} to $newQuantity")
                    item.copy(quantity = newQuantity)
                }
            } else {
                item
            }
        }

        if (newCartItems == cartItems) {
            Timber.d("No changes in cart items, skipping update")
            return
        }

        cartItems.clear()
        cartItems.addAll(newCartItems)

        updateCartState()
    }

    fun clearCart() {
        cartItems.clear()
        Timber.d("Cleared cart")
        updateCartState()
    }

    private fun calculateTotalPrice(): Long {
        Timber.d("Calculating total price for ${cartItems.size} items")
        return cartItems.sumOf { cartItem ->
            val price = cartItem.selectedSize?.price ?: cartItem.item.price
            price * cartItem.quantity
        }
    }

    private fun updateCartState() {
        Timber.d("Updating cart state with ${cartItems.size} items: $cartItems")
        cartState.postValue(CartState.CartUpdated(cartItems.toList(), calculateTotalPrice()))
    }

    fun updateItemNote(uniqueKey: String, note: String?) {
        val newCartItems = cartItems.map { item ->
            if (item.uniqueKey == uniqueKey) {
                Timber.d("Updated note for ${item.item.name}: $note")
                item.copy(note = note)
            } else {
                item
            }
        }

        cartItems.clear()
        cartItems.addAll(newCartItems)
        updateCartState()
    }

    fun reserveTable(startTime: String, peopleAssigned: Int, shareViewModel: ShareViewModel) {
        val items = cartItems.map { cartItem ->
            OrderItemRequest(
                id = cartItem.item.id,
                quantity = cartItem.quantity,
                size = cartItem.selectedSize?.name,
                note = cartItem.note
            )
        }
        val request = ReserveTableRequest(
            startTime = startTime,
            peopleAssigned = peopleAssigned,
            items = items,
            status = if (role == "CUSTOMER") "pending" else "confirmed",
        )

        CoroutineScope(Dispatchers.Main).launch {
            orderRepository.reserveTable(request).collect { result ->
                when (result) {
                    is Result.Success -> {
                        cartState.postValue(CartState.ReservationSuccess)
                        shareViewModel.setSelectedTableId(null) // Đặt tableId về null
//                        Timber.d("Cleared tableId in ShareViewModel after successful reservation")
                    }
                    is Result.Error -> {
                        cartState.postValue(CartState.ReservationError(result.message))
                    }
                    is Result.Loading -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    fun createOrder(tableId: String, startTime: String, endTime: String, shareViewModel: ShareViewModel) {
        val items = cartItems.map { cartItem ->
            OrderItemRequest(
                id = cartItem.item.id,
                quantity = cartItem.quantity,
                size = cartItem.selectedSize?.name,
                note = cartItem.note
            )
        }

        val request = CreateOrderRequest(
            startTime = startTime,
            endTime = endTime,
            type = "reservation",
            status = if (role == "CUSTOMER") "pending" else "confirmed",
            tables = listOf(tableId),
            items = items
        )

        CoroutineScope(Dispatchers.Main).launch {
            orderRepository.createOrder(request).collect { result ->
                when (result) {
                    is Result.Success -> {
                        cartState.postValue(CartState.ReservationSuccess)
                        shareViewModel.setSelectedTableId(null) // Đặt tableId về null
//                        Timber.d("Cleared tableId in ShareViewModel after successful reservation")
                    }

                    is Result.Error -> {
                        cartState.postValue(CartState.ReservationError(result.message))
                    }

                    is Result.Loading -> {

                    }
                }
            }
        }
    }
}