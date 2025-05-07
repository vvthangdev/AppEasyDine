package com.module.admin.area

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.admin.area.databinding.ItemTableBinding
import com.module.domain.api.model.TableStatus

class TableAdapter : ListAdapter<TableStatus, TableAdapter.TableViewHolder>(TableDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val binding = ItemTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TableViewHolder(private val binding: ItemTableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tableStatus: TableStatus) {
            binding.textTableNumber.text = "Bàn ${tableStatus.tableNumber}"
            binding.textCapacity.text = "Sức chứa: ${tableStatus.capacity}"

            // Set status icon based on table status
            val statusIcon = when (tableStatus.status) {
                "Available" -> R.drawable.table_available
                "Reserved" -> R.drawable.table_reserved
                "Occupied" -> R.drawable.table_occupied
                else -> R.drawable.table_available // Fallback
            }
            binding.imageStatus.setImageResource(statusIcon)
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<TableStatus>() {
        override fun areItemsTheSame(oldItem: TableStatus, newItem: TableStatus): Boolean {
            return oldItem.tableNumber == newItem.tableNumber
        }

        override fun areContentsTheSame(oldItem: TableStatus, newItem: TableStatus): Boolean {
            return oldItem == newItem
        }
    }
}