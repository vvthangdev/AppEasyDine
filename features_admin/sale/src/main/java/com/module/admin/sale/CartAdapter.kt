package com.module.admin.sale

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.admin.sale.databinding.ItemCartBinding
import com.module.domain.api.model.CartItem
import timber.log.Timber

class CartAdapter(
    private val viewModel: CartViewModel
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        private var currentCartItem: CartItem? = null

        fun bind(cartItem: CartItem) {
            currentCartItem = cartItem
            binding.cartItemName.text = cartItem.item.name
            // Hiển thị giá theo size nếu có, nếu không thì dùng giá mặc định của item
            val price = cartItem.selectedSize?.price ?: cartItem.item.price
            binding.cartItemPrice.text = "${price} VNĐ"
            binding.cartItemQuantity.setText(cartItem.quantity.toString())
            binding.cartItemSize.text = cartItem.selectedSize?.name ?: "Default"
            binding.cartItemNote.text = cartItem.note ?: ""

            // Load image using Glide
            Glide.with(binding.cartItemImage.context)
                .load(cartItem.item.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.cartItemImage)

            // Handle quantity changes
            binding.increaseQuantityButton.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                viewModel.updateItemQuantity(cartItem.uniqueKey, newQuantity)
                binding.cartItemQuantity.setText(newQuantity.toString())
            }

            binding.decreaseQuantityButton.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                viewModel.updateItemQuantity(cartItem.uniqueKey, newQuantity)
                if (newQuantity > 0) {
                    binding.cartItemQuantity.setText(newQuantity.toString())
                }
            }

            // Handle manual quantity input
            binding.cartItemQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString()
                    val quantity = input.toIntOrNull()
                    if (quantity != null && quantity >= 0 && quantity != cartItem.quantity) {
                        Timber.d("Manually updating quantity for ${cartItem.item.name} to $quantity")
                        viewModel.updateItemQuantity(cartItem.uniqueKey, quantity)
                    } else if (input.isBlank() || quantity == null) {
                        // Reset to current quantity if input is invalid
                        binding.cartItemQuantity.setText(cartItem.quantity.toString())
                    }
                }
            })

            // Handle remove item
            binding.removeItemButton.setOnClickListener {
                viewModel.removeItemFromCart(cartItem.uniqueKey)
            }
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.uniqueKey == newItem.uniqueKey
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}