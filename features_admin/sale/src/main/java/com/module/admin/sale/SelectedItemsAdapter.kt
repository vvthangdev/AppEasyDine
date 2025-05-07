package com.module.admin.sale

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.admin.sale.databinding.ItemSelectedCardBinding
import com.module.domain.api.model.CartItem
import timber.log.Timber

class SelectedItemsAdapter(
    private val onQuantityChange: (String, Int) -> Unit
) : ListAdapter<CartItem, SelectedItemsAdapter.SelectedItemViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedItemViewHolder {
        val binding = ItemSelectedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        Timber.d("Binding item at position $position: ${item.item.name} with quantity ${item.quantity}")
    }

    inner class SelectedItemViewHolder(
        private val binding: ItemSelectedCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var isUpdating = false // Flag to prevent recursive updates

        fun bind(cartItem: CartItem) {
            binding.itemName.text = cartItem.item.name
            updateItemPrice(cartItem)

            // Set quantity to EditText
            isUpdating = true
            binding.quantityText.setText(cartItem.quantity.toString())
            isUpdating = false

            Timber.d("Quantity for ${cartItem.item.name}: ${cartItem.quantity}")

            // Handle focus change to update quantity
            binding.quantityText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isUpdating) {
                    val input = binding.quantityText.text.toString()
                    val newQuantity = input.toIntOrNull() ?: cartItem.quantity // Revert to previous quantity if invalid
                    if (newQuantity != cartItem.quantity) {
                        Timber.d("Focus lost for ${cartItem.item.name}. New quantity: $newQuantity")
                        // Cập nhật giá ngay tại chỗ trước
                        updateItemPrice(CartItem(cartItem.item, newQuantity))
                        // Sau đó thông báo cho ViewModel để cập nhật tổng tiền
                        onQuantityChange(cartItem.item.id, newQuantity)
                    }
                    // Ensure EditText always shows a valid number
                    if (input.isBlank()) {
                        isUpdating = true
                        binding.quantityText.setText(cartItem.quantity.toString())
                        isUpdating = false
                    }
                }
            }

            // Handle "Done" action on keyboard
            binding.quantityText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val input = binding.quantityText.text.toString()
                    val newQuantity = input.toIntOrNull() ?: cartItem.quantity // Revert to previous quantity if invalid
                    if (newQuantity != cartItem.quantity) {
                        Timber.d("Done pressed for ${cartItem.item.name}. New quantity: $newQuantity")
                        // Cập nhật giá ngay tại chỗ trước
                        updateItemPrice(CartItem(cartItem.item, newQuantity))
                        // Sau đó thông báo cho ViewModel để cập nhật tổng tiền
                        onQuantityChange(cartItem.item.id, newQuantity)
                    }
                    // Ensure EditText always shows a valid number
                    if (input.isBlank()) {
                        isUpdating = true
                        binding.quantityText.setText(cartItem.quantity.toString())
                        isUpdating = false
                    }
                    binding.quantityText.clearFocus()
                    true
                } else {
                    false
                }
            }

            // Increase button
            binding.buttonIncrease.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                Timber.d("Increase button clicked for ${cartItem.item.name}. New quantity: $newQuantity")

                // Cập nhật giá ngay lập tức khi bấm nút tăng
                updateItemPrice(CartItem(cartItem.item, newQuantity))

                // Update EditText
                isUpdating = true
                binding.quantityText.setText(newQuantity.toString())
                isUpdating = false

                // Thông báo cho ViewModel để cập nhật tổng tiền
                onQuantityChange(cartItem.item.id, newQuantity)
            }

            // Decrease button
            binding.buttonDecrease.setOnClickListener {
                val newQuantity = (cartItem.quantity - 1).coerceAtLeast(0)
                Timber.d("Decrease button clicked for ${cartItem.item.name}. New quantity: $newQuantity")

                // Cập nhật giá ngay lập tức khi bấm nút giảm
                updateItemPrice(CartItem(cartItem.item, newQuantity))

                // Update EditText
                isUpdating = true
                binding.quantityText.setText(newQuantity.toString())
                isUpdating = false

                // Thông báo cho ViewModel để cập nhật tổng tiền
                onQuantityChange(cartItem.item.id, newQuantity)
            }
        }

        // Phương thức hỗ trợ cập nhật giá món ăn
        private fun updateItemPrice(cartItem: CartItem) {
            binding.itemPrice.text = "${cartItem.item.price * cartItem.quantity} đ"
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.item.id == newItem.item.id &&
                    oldItem.quantity == newItem.quantity
        }
    }
}