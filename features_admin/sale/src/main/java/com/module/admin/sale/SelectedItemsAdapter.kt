package com.module.admin.sale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.admin.sale.databinding.ItemSelectedCardBinding
import com.module.domain.api.model.CartItem
import com.module.domain.api.model.Size
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

class SelectedItemsAdapter(
    private val onQuantityChange: (String, Int) -> Unit, // uniqueKey, quantity
    private val onItemDetailsChange: (String, Size?, String?, String) -> Unit
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
        private var isUpdating = false

        fun bind(cartItem: CartItem) {
            binding.itemName.text = "${cartItem.item.name}${cartItem.selectedSize?.let { " (${it.name})" } ?: ""}"
            updateItemPrice(cartItem)

            isUpdating = true
            binding.quantityText.setText(cartItem.quantity.toString())
            isUpdating = false

            binding.root.setOnClickListener {
                showItemDetailsDialog(cartItem)
            }

            binding.quantityText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isUpdating) {
                    val input = binding.quantityText.text.toString()
                    val newQuantity = input.toIntOrNull() ?: cartItem.quantity
                    if (newQuantity != cartItem.quantity) {
                        updateItemPrice(CartItem(cartItem.item, newQuantity, cartItem.selectedSize, cartItem.note))
                        onQuantityChange(cartItem.uniqueKey, newQuantity)
                    }
                    if (input.isBlank()) {
                        isUpdating = true
                        binding.quantityText.setText(cartItem.quantity.toString())
                        isUpdating = false
                    }
                }
            }

            binding.quantityText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val input = binding.quantityText.text.toString()
                    val newQuantity = input.toIntOrNull() ?: cartItem.quantity
                    if (newQuantity != cartItem.quantity) {
                        updateItemPrice(CartItem(cartItem.item, newQuantity, cartItem.selectedSize, cartItem.note))
                        onQuantityChange(cartItem.uniqueKey, newQuantity)
                    }
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

            binding.buttonIncrease.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                updateItemPrice(CartItem(cartItem.item, newQuantity, cartItem.selectedSize, cartItem.note))
                isUpdating = true
                binding.quantityText.setText(newQuantity.toString())
                isUpdating = false
                onQuantityChange(cartItem.uniqueKey, newQuantity)
            }

            binding.buttonDecrease.setOnClickListener {
                val newQuantity = (cartItem.quantity - 1).coerceAtLeast(0)
                updateItemPrice(CartItem(cartItem.item, newQuantity, cartItem.selectedSize, cartItem.note))
                isUpdating = true
                binding.quantityText.setText(newQuantity.toString())
                isUpdating = false
                onQuantityChange(cartItem.uniqueKey, newQuantity)
            }
        }

        private fun updateItemPrice(cartItem: CartItem) {
            val price = cartItem.selectedSize?.price ?: cartItem.item.price
            binding.itemPrice.text = "${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(price)}"
        }

        private fun showItemDetailsDialog(cartItem: CartItem) {
            val dialogView = LayoutInflater.from(binding.root.context)
                .inflate(R.layout.dialog_item_details, null)
            val dialog = AlertDialog.Builder(binding.root.context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.textViewTitle).text = "Cập nhật ${cartItem.item.name}"

            val spinnerSize = dialogView.findViewById<Spinner>(R.id.spinnerSize)
            val sizes = cartItem.item.sizes
            if (sizes.isNotEmpty()) {
                val sizeNames = sizes.map { "${it.name} - ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(it.price)}" }
                val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, sizeNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSize.adapter = adapter
                spinnerSize.visibility = View.VISIBLE

                cartItem.selectedSize?.let { selectedSize ->
                    val index = sizes.indexOfFirst { it.id == selectedSize.id }
                    if (index != -1) spinnerSize.setSelection(index)
                }
            } else {
                spinnerSize.visibility = View.GONE
            }

            val editNote = dialogView.findViewById<EditText>(R.id.editNote)
            editNote.setText(cartItem.note)

            dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.buttonSave).setOnClickListener {
                val selectedSizeIndex = spinnerSize.selectedItemPosition
                val selectedSize = if (sizes.isNotEmpty() && selectedSizeIndex >= 0 && selectedSizeIndex < sizes.size) {
                    sizes[selectedSizeIndex]
                } else {
                    null
                }
                val note = editNote.text.toString().takeIf { it.isNotBlank() }
                onItemDetailsChange(cartItem.item.id, selectedSize, note, cartItem.uniqueKey)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.uniqueKey == newItem.uniqueKey
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.uniqueKey == newItem.uniqueKey &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.note == newItem.note
        }
    }
}