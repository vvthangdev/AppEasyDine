package com.module.admin.sale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.module.admin.sale.databinding.DialogItemSelectionBinding
import com.module.domain.api.model.CartItem
import timber.log.Timber

class ItemSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogItemSelectionBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var cartItem: CartItem

    companion object {
        private const val ARG_CART_ITEM = "cart_item"

        fun newInstance(cartItem: CartItem): ItemSelectionDialogFragment {
            val fragment = ItemSelectionDialogFragment()
            val args = Bundle().apply {
                putString(ARG_CART_ITEM, Gson().toJson(cartItem))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val dialogWidth = (displayMetrics.widthPixels * 0.8).toInt()
            window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_CART_ITEM)?.let {
            try {
                cartItem = Gson().fromJson(it, CartItem::class.java)
                Timber.d("Parsed CartItem: ${cartItem.item.name}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse CartItem")
                dismiss()
            }
        } ?: run {
            Timber.e("CartItem not found in arguments")
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogItemSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Showing selection dialog for item: ${cartItem.item.name}")
        setupViews()
    }

    private fun setupViews() {
        binding.itemName.text = cartItem.item.name

        // Load image using Glide
        Glide.with(binding.itemImage.context)
            .load(cartItem.item.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(binding.itemImage)

        // Setup sizes spinner
        if (!cartItem.item.sizes.isNullOrEmpty()) {
            val sizeAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cartItem.item.sizes!!.map { "${it.name} (${it.price} VNĐ)" }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.sizeSpinner.adapter = sizeAdapter
            binding.sizeSpinner.visibility = View.VISIBLE
            // Select current size if available
            cartItem.selectedSize?.let { size ->
                val index = cartItem.item.sizes!!.indexOfFirst { it.name == size.name }
                if (index >= 0) binding.sizeSpinner.setSelection(index)
            }
        } else {
            binding.sizeSpinner.visibility = View.GONE
        }

        // Setup quantity
        binding.quantityEditText.setText(cartItem.quantity.toString())

        // Setup note
        binding.noteEditText.setText(cartItem.note ?: "")

        // Setup buttons
        binding.addToCartButton.setOnClickListener {
            val quantity = binding.quantityEditText.text.toString().toIntOrNull() ?: 1
            if (quantity <= 0) {
                Toast.makeText(requireContext(), "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedSize = if (!cartItem.item.sizes.isNullOrEmpty()) {
                cartItem.item.sizes!![binding.sizeSpinner.selectedItemPosition]
            } else {
                null
            }
            val note = binding.noteEditText.text.toString().takeIf { it.isNotBlank() }

            // Generate new unique key
            val newUniqueKey = "${cartItem.item.id}_${selectedSize?.name ?: "no_size"}"

            // Check if item exists in cart
            val existingItem = cartViewModel.cartState.value?.let { state ->
                if (state is CartState.CartUpdated) {
                    state.cartItems.find { it.uniqueKey == newUniqueKey }
                } else null
            }

            if (existingItem != null) {
                // Update existing item
                cartViewModel.updateItemQuantity(newUniqueKey, quantity)
                cartViewModel.updateItemNote(newUniqueKey, note)
                Timber.d("Updated item in cart: ${cartItem.item.name}, quantity: ${quantity}, note: $note")
            } else {
                // Add new item
                cartViewModel.addItemToCart(cartItem.item, quantity, selectedSize, note)
                Timber.d("Added new item to cart: ${cartItem.item.name}, quantity: $quantity, size: ${selectedSize?.name}")
            }

            Toast.makeText(requireContext(), "Đã cập nhật ${cartItem.item.name} trong giỏ hàng", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            Timber.d("Canceling ItemSelectionDialogFragment")
            dismiss()
        }

        binding.decreaseQuantityButton.setOnClickListener {
            val currentQuantity = binding.quantityEditText.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                binding.quantityEditText.setText((currentQuantity - 1).toString())
            }
        }

        binding.increaseQuantityButton.setOnClickListener {
            val currentQuantity = binding.quantityEditText.text.toString().toIntOrNull() ?: 1
            binding.quantityEditText.setText((currentQuantity + 1).toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.d("Destroying ItemSelectionDialogFragment")
    }
}