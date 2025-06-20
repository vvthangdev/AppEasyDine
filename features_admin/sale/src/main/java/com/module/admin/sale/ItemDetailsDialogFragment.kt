package com.module.admin.sale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.module.admin.sale.databinding.DialogItemDetailsBinding
import com.module.domain.api.model.Item
import timber.log.Timber

class ItemDetailsDialogFragment : DialogFragment() {

    private var _binding: DialogItemDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var item: Item

    companion object {
        private const val ARG_ITEM = "item"

        fun newInstance(item: Item): ItemDetailsDialogFragment {
            val fragment = ItemDetailsDialogFragment()
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
        _binding = DialogItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Showing details for item: ${item.name}")
        setupViews()
    }

    private fun setupViews() {
        // Set item details
        binding.itemName.text = item.name
        binding.itemDescription.text = item.description ?: "Không có mô tả"

        // Load image using Glide
        Glide.with(binding.itemImage.context)
            .load(item.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(binding.itemImage)

        // Setup sizes spinner (display only, non-interactive)
        if (!item.sizes.isNullOrEmpty()) {
            val sizeAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                item.sizes!!.map { it.name }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.sizeSpinner.adapter = sizeAdapter
//            binding.sizeSpinner.isEnabled = false // Non-interactive for details
            binding.sizeSpinner.visibility = View.VISIBLE
        } else {
            binding.sizeSpinner.visibility = View.GONE
        }

        // Setup close button
        binding.closeButton.setOnClickListener {
            Timber.d("Closing ItemDetailsDialogFragment")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.d("Destroying ItemDetailsDialogFragment")
    }
}