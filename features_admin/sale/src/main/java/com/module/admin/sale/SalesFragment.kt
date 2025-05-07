package com.module.admin.sale

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.core.ui.base.BaseFragment
import com.module.domain.api.model.Category
import com.module.admin.sale.databinding.FragmentSalesBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SalesFragment : BaseFragment<FragmentSalesBinding, SalesViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_sales

    private val mViewModel: SalesViewModel by viewModels()
    override fun getVM(): SalesViewModel = mViewModel

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var selectedItemsAdapter: SelectedItemsAdapter
    private lateinit var categoryAdapter: ArrayAdapter<Category>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing SalesFragment view")
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup Category Spinner
        categoryAdapter = object : ArrayAdapter<Category>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = getItem(position)?.name
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = getItem(position)?.name
                return view
            }
        }
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val category = categoryAdapter.getItem(position)
                category?.id?.let { mViewModel.loadItemsForCategory(it) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Setup Search Bar
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                mViewModel.searchItems(s.toString())
            }
        })

        // Setup Items RecyclerView
        itemsAdapter = ItemsAdapter { item ->
            mViewModel.addItemToCart(item)
        }
        binding.recyclerViewItems.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = itemsAdapter
        }

        // Setup Selected Items RecyclerView
        selectedItemsAdapter = SelectedItemsAdapter { itemId, quantity ->
            mViewModel.updateItemQuantity(itemId, quantity)
        }
        binding.recyclerViewSelectedItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectedItemsAdapter
        }
    }

    override fun observeViewModel() {
        mViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.clear()
            categoryAdapter.addAll(categories)
            categoryAdapter.notifyDataSetChanged()
        }

        mViewModel.items.observe(viewLifecycleOwner) { items ->
            itemsAdapter.submitList(items)
        }

        mViewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
            selectedItemsAdapter.submitList(selectedItems)
        }

        mViewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            Timber.d("Updating total price in UI: $total")
            binding.textTotalPrice.text = "$total đ"
        }
    }
}