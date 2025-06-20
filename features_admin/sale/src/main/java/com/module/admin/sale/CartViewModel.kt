package com.module.admin.sale

import androidx.lifecycle.MutableLiveData
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.CartItem
import com.module.domain.api.model.Item
import com.module.domain.api.model.Size
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class CartState {
    data class CartUpdated(val cartItems: List<CartItem>, val totalPrice: Long) : CartState()
    data class Error(val exception: Throwable?) : CartState()
}

@HiltViewModel
class CartViewModel @Inject constructor() : BaseViewModel() {

    val cartState = MutableLiveData<CartState>()
    private val cartItems = mutableListOf<CartItem>()

    fun addItemToCart(item: Item, quantity: Int = 1, selectedSize: Size? = null, note: String? = null) {
        val existingItem = cartItems.find { it.uniqueKey == "${item.id}_${selectedSize?.name ?: "no_size"}" }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(item, quantity, selectedSize, note))
        }
        updateCartState()
    }

    fun removeItemFromCart(uniqueKey: String) {
        cartItems.removeAll { it.uniqueKey == uniqueKey }
        updateCartState()
    }

    fun updateItemQuantity(uniqueKey: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItemFromCart(uniqueKey)
        } else {
            cartItems.find { it.uniqueKey == uniqueKey }?.let { item ->
                item.quantity = newQuantity
                updateCartState()
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
        updateCartState()
    }

    private fun calculateTotalPrice(): Long {
        return cartItems.sumOf { cartItem ->
            val price = cartItem.selectedSize?.price ?: cartItem.item.price
            price * cartItem.quantity
        }
    }

    private fun updateCartState() {
        cartState.postValue(CartState.CartUpdated(cartItems.toList(), calculateTotalPrice()))
    }
}