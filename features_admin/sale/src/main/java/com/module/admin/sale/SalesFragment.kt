package com.module.admin.sale

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import com.module.domain.api.model.Category
import com.module.admin.sale.databinding.FragmentSalesBinding
import com.module.features.utils.AreaSaleViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SalesFragment : BaseFragment<FragmentSalesBinding, SalesViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_sales

    private val mViewModel: SalesViewModel by activityViewModels()
    private val sharedViewModel: AreaSaleViewModel by activityViewModels()
    override fun getVM(): SalesViewModel = mViewModel

    @Inject
    lateinit var mCoreNavigation: SaleNavigation

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var categoryAdapter: ArrayAdapter<Category>
    private var tableId: String? = null
    private var tableNumber: Int? = null
    private var hasOrder: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        tableId = arguments?.getString("tableId")
//        tableNumber = arguments?.getInt("tableNumber")
//        hasOrder = arguments?.getBoolean("hasOrder", false) ?: false
//        Timber.d("SalesFragment created with tableId: $tableId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing SalesFragment view")
        setupViews()
        observeViewModel()

        if (hasOrder) {
            tableId?.let { mViewModel.loadOrderInfo(tableId = it) }
        }
    }

    private fun setupViews() {
        binding.textSelectedTable?.text = tableId?.let {
            "Bàn $tableId va $tableNumber)"
        } ?: "Chưa chọn bàn"
        Timber.d("Set textSelectedTable to: ${binding.textSelectedTable?.text}")

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

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                mViewModel.searchItems(s.toString())
            }
        })

        itemsAdapter = ItemsAdapter(this, mViewModel)
        binding.recyclerViewItems.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = itemsAdapter
        }

        binding.buttonCart.setOnClickListener {
            val bundle = Bundle().apply {
                tableId?.let { putString("tableId", it) }
                tableNumber?.let { putInt("tableNumber", it) }
                putBoolean("hasOrder", hasOrder)
            }
            Timber.d("Navigating to CartFragment with tableId: $tableId, hasOrder: $hasOrder")
            mCoreNavigation.openSaleToCart(bundle)
        }

        sharedViewModel.selectedTableId.observe(viewLifecycleOwner) { selectedTableId ->
            tableId = selectedTableId
            binding.textSelectedTable?.text = tableId?.let {
                "Bàn: $it"
            } ?: "Chưa chọn bàn"
            Timber.d("Selected tableId in SalesFragment: $tableId")
            tableId?.let {
                if (hasOrder) mViewModel.loadOrderInfo(tableId = it)
            }
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
            val totalQuantity = selectedItems.sumOf { it.quantity }
            binding.buttonCart.text = "Giỏ hàng ($totalQuantity)"
        }
    }
}