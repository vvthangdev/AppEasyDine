package com.module.admin.sale

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.module.admin.sale.databinding.FragmentSalesBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import com.module.domain.api.model.Category
import com.module.domain.api.model.Item
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SalesFragment : BaseFragment<FragmentSalesBinding, SalesViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_sales

    private val mViewModel: SalesViewModel by viewModels()
    private val mCartViewModel: CartViewModel by activityViewModels()

    @Inject
    lateinit var mCoreNavigation: CoreNavigation

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var categoryAdapter: ArrayAdapter<Category>

    override fun getVM(): SalesViewModel = mViewModel

    override fun initView() {
        super.initView()
        Timber.d("Setting up views for SalesFragment")

        mViewModel.loadCategories()

        // Setup category spinner
        categoryAdapter = object : ArrayAdapter<Category>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf(Category(id = "", name = "Tất cả", description = null, image = null, createdAt = null, updatedAt = null, version = 0))
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
        binding.categorySpinner.adapter = categoryAdapter
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = categoryAdapter.getItem(position)
                Timber.d("Filtering by category: ${selectedCategory?.name}")
                mViewModel.filterItemsByCategory(selectedCategory?.id ?: "")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Timber.d("No category selected, loading all items")
                mViewModel.filterItemsByCategory("")
            }
        }

        // Setup recycler view
        itemsAdapter = ItemsAdapter(this, mViewModel)
        binding.menuItemsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = itemsAdapter
        }

        // Setup search input
        binding.menuSearchBar.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    Timber.d("Searching items with query: ${s.toString()}")
                    mViewModel.searchItems(s.toString())
                }
            }
        })

        // Setup back button
        binding.backButton.setOnClickListener {
            Timber.d("Navigating back")
            try {
                requireActivity().onBackPressed()
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Navigation error")
                Toast.makeText(requireContext(), "Không thể quay lại", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup cart button
        binding.cartButton.setOnClickListener {
            Timber.d("Navigating to CartFragment")
            try {
                mCoreNavigation.openSaleToCart()
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Navigation error")
                Toast.makeText(requireContext(), "Không thể điều hướng đến màn hình giỏ hàng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun observeViewModel() {
        mViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SalesState.ItemsLoaded -> {
                    Timber.d("Loaded ${state.items.size} items")
                    itemsAdapter.submitList(state.items)
                }
                is SalesState.CategoriesLoaded -> {
                    Timber.d("Loaded ${state.categories.size} categories")
                    // Tạo danh sách mới để tránh thay đổi danh sách gốc
                    val newCategories = mutableListOf<Category>().apply {
                        add(Category(id = "", name = "Tất cả", description = null, image = null, createdAt = null, updatedAt = null, version = 0))
                        addAll(state.categories)
                    }
                    categoryAdapter.clear()
                    categoryAdapter.addAll(newCategories)
                    categoryAdapter.notifyDataSetChanged()
                    // Đảm bảo Spinner chọn lại mục đầu tiên nếu cần
                    binding.categorySpinner.setSelection(0)
                }
                is SalesState.Error -> {
                    Timber.e(state.exception, "Error loading data")
                    state.exception?.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
                is SalesState.Loading -> {
                    Timber.d("Loading data")
                    // Handled by isLoading
                }
            }
        }

        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Timber.d("Loading state: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        mCartViewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CartState.CartUpdated -> {
                    val itemCount = state.cartItems.sumOf { it.quantity }
                    Timber.d("Cart updated with $itemCount items")
                    binding.cartBadge.text = itemCount.toString()
                    binding.cartBadge.visibility = if (itemCount > 0) View.VISIBLE else View.GONE
                }
                is CartState.Error -> {
                    Timber.e(state.exception, "Cart error")
                    state.exception?.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
                is CartState.ReservationSuccess -> {

                }
                is CartState.ReservationError -> {
                }
            }
        }
    }

    override fun onDestroyView() {
//        Timber.d("Unbinding CoreNavigation for SalesFragment")
//        mCoreNavigation.unbind()
        super.onDestroyView()
    }
}