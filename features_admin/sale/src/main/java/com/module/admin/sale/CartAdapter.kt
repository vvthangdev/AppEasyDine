package com.module.admin.sale

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.admin.sale.databinding.ItemCartBinding
import com.module.domain.api.model.CartItem
import timber.log.Timber

class CartAdapter(
    private val fragment: CartFragment, // Thêm tham số fragment
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
            val price = cartItem.selectedSize?.price ?: cartItem.item.price
            binding.cartItemPrice.text = "${price} VNĐ"
            binding.cartItemQuantity.setText(cartItem.quantity.toString())
            binding.cartItemSize.text = cartItem.selectedSize?.name ?: "Default"
            binding.cartItemNote.text = cartItem.note ?: ""

            Timber.d("Cart item quantity: ${cartItem.quantity}")

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
            }

            binding.decreaseQuantityButton.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                if (newQuantity > 0) {
                    viewModel.updateItemQuantity(cartItem.uniqueKey, newQuantity)
                }
            }

            // Handle manual quantity input when EditText loses focus or user presses Done
            binding.cartItemQuantity.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val input = binding.cartItemQuantity.text.toString()
                    val quantity = input.toIntOrNull()
                    if (quantity != null && quantity >= 0 && quantity != cartItem.quantity) {
                        Timber.d("Updating quantity for ${cartItem.item.name} to $quantity after losing focus")
                        viewModel.updateItemQuantity(cartItem.uniqueKey, quantity)
                    } else if (input.isBlank() || quantity == null) {
                        Timber.d("Invalid input for ${cartItem.item.name}, reverting to ${cartItem.quantity}")
                        binding.cartItemQuantity.setText(cartItem.quantity.toString())
                        binding.cartItemQuantity.setSelection(cartItem.quantity.toString().length)
                    }
                }
            }

            // Handle Done action on soft keyboard
            binding.cartItemQuantity.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
                ) {
                    val input = binding.cartItemQuantity.text.toString()
                    val quantity = input.toIntOrNull()
                    if (quantity != null && quantity >= 0 && quantity != cartItem.quantity) {
                        Timber.d("Updating quantity for ${cartItem.item.name} to $quantity after Done action")
                        viewModel.updateItemQuantity(cartItem.uniqueKey, quantity)
                    } else if (input.isBlank() || quantity == null) {
                        Timber.d("Invalid input for ${cartItem.item.name}, reverting to ${cartItem.quantity}")
                        binding.cartItemQuantity.setText(cartItem.quantity.toString())
                        binding.cartItemQuantity.setSelection(cartItem.quantity.toString().length)
                    }
                    // Hide soft keyboard
                    val imm = binding.cartItemQuantity.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.cartItemQuantity.windowToken, 0)
                    true
                } else {
                    false
                }
            }

            // Handle remove item
            binding.removeItemButton.setOnClickListener {
                viewModel.removeItemFromCart(cartItem.uniqueKey)
            }

            // Handle click on cart item image to open ItemSelectionDialogFragment
            binding.cartItemImage.setOnClickListener {
                Timber.d("Clicked on image for item: ${cartItem.item.name}")
                val dialog = ItemSelectionDialogFragment.newInstance(cartItem)
                dialog.show(fragment.childFragmentManager, "ItemSelectionDialogFragment")
            }
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.uniqueKey == newItem.uniqueKey
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            Timber.d("Comparing items: ${oldItem.quantity} and ${newItem.quantity}")
            return oldItem.quantity == newItem.quantity &&
                    oldItem.selectedSize == newItem.selectedSize &&
                    oldItem.note == newItem.note &&
                    oldItem.item == newItem.item
        }
    }
}