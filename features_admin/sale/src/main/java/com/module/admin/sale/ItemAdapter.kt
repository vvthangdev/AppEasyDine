package com.module.admin.sale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.admin.sale.databinding.ItemCardBinding
import com.module.domain.api.model.Item
import java.text.NumberFormat
import java.util.Locale

class ItemsAdapter(
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, ItemsAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate layout item_card.xml sử dụng Data Binding
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
            // Gán tên món ăn
            binding.itemName.text = item.name
            // Định dạng giá tiền theo tiền tệ Việt Nam
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.itemPrice.text = formatter.format(item.price)
            // Tải hình ảnh sử dụng Glide
            Glide.with(binding.itemImage.context)
                .load(item.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.itemImage)
            // Xử lý click để thêm món vào giỏ hàng (tăng số lượng)
            binding.root.setOnClickListener {
                onItemClick(item)
            }
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