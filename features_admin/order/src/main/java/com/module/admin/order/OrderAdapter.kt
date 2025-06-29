package com.module.admin.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.admin.order.databinding.ItemOrderBinding
import com.module.domain.api.model.UserOrderResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderAdapter : ListAdapter<UserOrderResponse, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: UserOrderResponse) {
            // Format time từ UTC sang định dạng hiển thị
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }
            val formattedTime = try {
                val date = inputFormat.parse(order.time)
                outputFormat.format(date)
            } catch (e: Exception) {
                order.time
            }

            binding.orderIdTextView.text = "Mã đơn: ${order.id}"
            binding.orderTimeTextView.text = "Thời gian: $formattedTime"
            binding.orderStatusTextView.text = "Trạng thái: ${order.status}"
            binding.orderAmountTextView.text = "Tổng tiền: ${order.finalAmount} VNĐ"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
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