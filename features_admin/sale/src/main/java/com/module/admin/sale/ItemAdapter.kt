package com.module.admin.sale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.admin.sale.databinding.ItemCardBinding
import com.module.domain.api.model.Item
import timber.log.Timber

class ItemsAdapter(
    private val fragment: SalesFragment,
    private val viewModel: SalesViewModel
) : ListAdapter<Item, ItemsAdapter.ItemViewHolder>(ItemDiffCallback()) {

    private val cartViewModel: CartViewModel by fragment.activityViewModels()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.menuItemName.text = item.name
            binding.menuItemPrice.text = "${item.price} VNĐ"

            // Load image using Glide
            Glide.with(binding.menuItemImage.context)
                .load(item.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.menuItemImage)

            // Handle add to cart button
            binding.addItemToCartButton.setOnClickListener {
//                if (item.sizes.isNullOrEmpty()) {
//                    // No sizes, add directly to cart
//                    Timber.d("Adding item ${item.name} to cart without size")
//                    cartViewModel.addItemToCart(item, quantity = 1)
//                } else {
                    // Show dialog for size and note selection
                    Timber.d("Showing selection dialog for ${item.name}")
                    val dialog = ItemSelectionDialogFragment.newInstance(item)
                    dialog.show(fragment.childFragmentManager, "ItemSelectionDialog")
//                }
            }

            // Handle details button
            binding.itemDetailsButton.setOnClickListener {
                Timber.d("Showing details dialog for ${item.name}")
                val dialog = ItemDetailsDialogFragment.newInstance(item)
                dialog.show(fragment.childFragmentManager, "ItemDetailsDialog")
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