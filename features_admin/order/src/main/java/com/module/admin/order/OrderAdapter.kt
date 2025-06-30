package com.module.admin.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.admin.order.databinding.ItemOrderBinding
import com.module.domain.api.model.UserOrderResponse
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<UserOrderResponse, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: UserOrderResponse) {
            // Format thời gian từ UTC sang giờ Việt Nam
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            }
            val formattedTime = try {
                val date = inputFormat.parse(order.time)
                outputFormat.format(date)
            } catch (e: Exception) {
                "Lỗi định dạng thời gian"
            }

            binding.orderTimeTextView.text = "Thời gian: $formattedTime"
            binding.orderStatusTextView.text = "Trạng thái: ${order.status}"
            binding.orderAmountTextView.text = "Tổng tiền: ${order.finalAmount} VNĐ"

            binding.root.setOnClickListener {
                onItemClick(order.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<UserOrderResponse>() {
        override fun areItemsTheSame(oldItem: UserOrderResponse, newItem: UserOrderResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserOrderResponse, newItem: UserOrderResponse): Boolean {
            return oldItem == newItem
        }
    }
}