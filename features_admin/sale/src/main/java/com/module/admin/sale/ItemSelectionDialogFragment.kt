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
import com.module.domain.api.model.Item
import com.module.domain.api.model.Size
import timber.log.Timber

class ItemSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogItemSelectionBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var item: Item

    companion object {
        private const val ARG_ITEM = "item"

        fun newInstance(item: Item): ItemSelectionDialogFragment {
            val fragment = ItemSelectionDialogFragment()
            val args = Bundle().apply {
                putString(ARG_ITEM, Gson().toJson(item))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val dialogWidth = (displayMetrics.widthPixels * 0.8).toInt() // 80% chiều ngang màn hình
            window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent) // Nền trong suốt
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_ITEM)?.let {
            item = Gson().fromJson(it, Item::class.java)
        } ?: run {
            Timber.e("Item not found in arguments")
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
        Timber.d("Showing selection dialog for item: ${item.name}")
        setupViews()
    }

    private fun setupViews() {
        binding.itemName.text = item.name

        // Load image using Glide
        Glide.with(binding.itemImage.context)
            .load(item.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(binding.itemImage)

        // Setup sizes spinner
        if (!item.sizes.isNullOrEmpty()) {
            val sizeAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                item.sizes!!.map { "${it.name} (${it.price} VNĐ)" }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.sizeSpinner.adapter = sizeAdapter
            binding.sizeSpinner.visibility = View.VISIBLE
        } else {
            binding.sizeSpinner.visibility = View.GONE
        }

        // Setup buttons
        binding.addToCartButton.setOnClickListener {
            val selectedSize = if (!item.sizes.isNullOrEmpty()) {
                item.sizes!![binding.sizeSpinner.selectedItemPosition]
            } else {
                null
            }
            val note = binding.noteEditText.text.toString().takeIf { it.isNotBlank() }
            Timber.d("Adding item ${item.name} to cart with size: ${selectedSize?.name}, note: $note")
            cartViewModel.addItemToCart(item, quantity = 1, selectedSize = selectedSize, note = note)
            Toast.makeText(requireContext(), "Đã thêm ${item.name} vào giỏ hàng", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            Timber.d("Canceling ItemSelectionDialogFragment")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.d("Destroying ItemSelectionDialogFragment")
    }
}