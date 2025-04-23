package com.module.admin.sale

// CategoryAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.domain.api.model.AdminHome

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
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: AdminHome.Item) {
            itemView.findViewById<TextView>(android.R.id.text1).text = item.name
            itemView.findViewById<TextView>(android.R.id.text2).text = "${item.price} đ"
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<AdminHome.Item>() {
    override fun areItemsTheSame(oldItem: AdminHome.Item, newItem: AdminHome.Item) = oldItem._id == newItem._id
    override fun areContentsTheSame(oldItem: AdminHome.Item, newItem: AdminHome.Item) = oldItem == newItem
}

// SelectedItemsAdapter.kt (tương tự ItemsAdapter, có thể tùy chỉnh)
class SelectedItemsAdapter : ListAdapter<AdminHome.Item, SelectedItemsAdapter.ViewHolder>(ItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: AdminHome.Item) {
            itemView.findViewById<TextView>(android.R.id.text1).text = item.name
            itemView.findViewById<TextView>(android.R.id.text2).text = "${item.price} đ"
        }
    }
}