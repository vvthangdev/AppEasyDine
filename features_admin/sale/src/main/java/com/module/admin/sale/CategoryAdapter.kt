package com.module.admin.sale

// CategoryAdapter.kt
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.domain.api.model.AdminHome
import com.module.domain.api.model.CartItem

class CategoryAdapter(
    private val onCategoryClick: (AdminHome.Category) -> Unit
) : ListAdapter<AdminHome.Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: AdminHome.Category) {
            itemView.findViewById<TextView>(android.R.id.text1).text = category.name
            itemView.setOnClickListener { onCategoryClick(category) }
        }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<AdminHome.Category>() {
    override fun areItemsTheSame(oldItem: AdminHome.Category, newItem: AdminHome.Category) = oldItem._id == newItem._id
    override fun areContentsTheSame(oldItem: AdminHome.Category, newItem: AdminHome.Category) = oldItem == newItem
}

// ItemsAdapter.kt

class ItemsAdapter(
    private val onItemClick: (AdminHome.Item) -> Unit
) : ListAdapter<AdminHome.Item, ItemsAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.item_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.item_price)
        private val imageView: ImageView = itemView.findViewById(R.id.item_image)

        fun bind(item: AdminHome.Item) {
            nameTextView.text = item.name
            priceTextView.text = "${item.price} đ"
            Glide.with(itemView.context)
                .load(item.image)
                .placeholder(R.drawable.placeholder_image) // Ảnh placeholder
                .error(R.drawable.placeholder_image) // Ảnh lỗi
                .into(imageView)
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<AdminHome.Item>() {
        override fun areItemsTheSame(oldItem: AdminHome.Item, newItem: AdminHome.Item) = oldItem._id == newItem._id
        override fun areContentsTheSame(oldItem: AdminHome.Item, newItem: AdminHome.Item) = oldItem == newItem
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<AdminHome.Item>() {
    override fun areItemsTheSame(oldItem: AdminHome.Item, newItem: AdminHome.Item) = oldItem._id == newItem._id
    override fun areContentsTheSame(oldItem: AdminHome.Item, newItem: AdminHome.Item) = oldItem == newItem
}

// SelectedItemsAdapter.kt (tương tự ItemsAdapter, có thể tùy chỉnh)
class SelectedItemsAdapter(
    private val onQuantityChanged: (String, Int) -> Unit
) : ListAdapter<CartItem, SelectedItemsAdapter.ViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.selected_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.selected_item_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.selected_item_price)
        private val quantityEditText: EditText = itemView.findViewById(R.id.selected_item_quantity)
        private val decreaseButton: Button = itemView.findViewById(R.id.decrease_quantity)
        private val increaseButton: Button = itemView.findViewById(R.id.increase_quantity)

        fun bind(cartItem: CartItem) {
            nameTextView.text = cartItem.item.name
            priceTextView.text = "${cartItem.item.price} đ"
            quantityEditText.setText(cartItem.quantity.toString())

            // Handle EditText input
            quantityEditText.addTextChangedListener(object : TextWatcher {
                private var isUserInput = true

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (isUserInput) {
                        val quantity = s.toString().toIntOrNull() ?: 0
                        onQuantityChanged(cartItem.item._id, quantity)
                    }
                }
            })

            // Handle buttons
            decreaseButton.setOnClickListener {
                val currentQuantity = quantityEditText.text.toString().toIntOrNull() ?: 1
                val newQuantity = maxOf(0, currentQuantity - 1)
                quantityEditText.setText(newQuantity.toString())
                onQuantityChanged(cartItem.item._id, newQuantity)
            }

            increaseButton.setOnClickListener {
                val currentQuantity = quantityEditText.text.toString().toIntOrNull() ?: 1
                val newQuantity = currentQuantity + 1
                quantityEditText.setText(newQuantity.toString())
                onQuantityChanged(cartItem.item._id, newQuantity)
            }
        }
    }
}

class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
        oldItem.item._id == newItem.item._id
    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
        oldItem == newItem
}