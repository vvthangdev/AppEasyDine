package com.module.admin.sale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.admin.sale.databinding.ItemCardBinding
import com.module.domain.api.model.Item
import com.module.domain.api.model.Size
import java.text.NumberFormat
import java.util.Locale

class ItemsAdapter(
    private val fragment: Fragment,
    private val viewModel: SalesViewModel
) : ListAdapter<Item, ItemsAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.itemName.text = item.name
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.itemPrice.text = formatter.format(item.price)

            // Observe selectedItems to update quantity
            viewModel.selectedItems.observe(fragment.viewLifecycleOwner) { cartItems ->
                val quantity = cartItems
                    .filter { it.item.id == item.id }
                    .sumOf { it.quantity }
                binding.textQuantity.text = quantity.toString()
                binding.quantityLayout.visibility = View.VISIBLE // Always show buttons
            }

            // Handle increase button
            binding.buttonIncrease.setOnClickListener {
                if (item.sizes.isEmpty()) {
                    viewModel.addItemToCart(item, null)
                } else {
                    showSizeSelectionDialog(item, true)
                }
            }

            // Handle decrease button
            binding.buttonDecrease.setOnClickListener {
                if (item.sizes.isEmpty()) {
                    viewModel.decreaseItemQuantity(item, null)
                } else {
                    showSizeSelectionDialog(item, false)
                }
            }

            Glide.with(binding.itemImage.context)
                .load(item.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.itemImage)
        }

        private fun showSizeSelectionDialog(item: Item, isIncrease: Boolean) {
            val dialogView = LayoutInflater.from(binding.root.context)
                .inflate(R.layout.dialog_item_details, null)
            val dialog = AlertDialog.Builder(binding.root.context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.textViewTitle)?.text = "Chọn kích thước cho ${item.name}"

            val spinnerSize = dialogView.findViewById<Spinner>(R.id.spinnerSize)
            val sizes = item.sizes
            val sizeNames = sizes.map { "${it.name} - ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(it.price)}" }
            val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, sizeNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSize?.adapter = adapter
            spinnerSize?.visibility = View.VISIBLE

            dialogView.findViewById<EditText>(R.id.editNote)?.visibility = View.GONE

            dialogView.findViewById<Button>(R.id.buttonCancel)?.setOnClickListener {
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.buttonSave)?.setOnClickListener {
                val selectedSizeIndex = spinnerSize?.selectedItemPosition ?: -1
                val selectedSize = if (selectedSizeIndex >= 0 && selectedSizeIndex < sizes.size) {
                    sizes[selectedSizeIndex]
                } else {
                    null
                }
                if (isIncrease) {
                    viewModel.addItemToCart(item, selectedSize)
                } else {
                    viewModel.decreaseItemQuantity(item, selectedSize)
                }
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}