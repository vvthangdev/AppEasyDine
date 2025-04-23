package com.module.admin.sale

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.admin.sale.databinding.FragmentSalesBinding
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SalesFragment : BaseFragment<FragmentSalesBinding, SalesViewModel>() {
    val TAG: String
        get() = "SalesFragment"
    override val layoutId: Int
        get() = R.layout.fragment_sales

    private val mViewModel: SalesViewModel by viewModels()
    override fun getVM(): SalesViewModel {
        return mViewModel
    }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var selectedItemsAdapter: SelectedItemsAdapter

    override fun initView() {
        super.initView()
        Timber.d("initView123")
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        categoryAdapter = CategoryAdapter { category ->
            mViewModel.loadItemsForCategory(category._id)
        }
        binding.recyclerViewCategory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }

        itemsAdapter = ItemsAdapter { item ->
            mViewModel.addItemToCart(item)
        }
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }

        selectedItemsAdapter = SelectedItemsAdapter()
        binding.recyclerViewSelectedItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectedItemsAdapter
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        mViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        mViewModel.items.observe(viewLifecycleOwner) { items ->
            itemsAdapter.submitList(items)
        }

        mViewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
            selectedItemsAdapter.submitList(selectedItems)
        }

        mViewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            binding.textTotalPrice.text = "$total đ"
        }
    }
}