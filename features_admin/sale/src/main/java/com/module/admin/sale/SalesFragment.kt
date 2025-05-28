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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseFragment
import com.module.domain.api.model.Category
import com.module.admin.sale.databinding.FragmentSalesBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class SalesFragment : BaseFragment<FragmentSalesBinding, SalesViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_sales

    private val mViewModel: SalesViewModel by viewModels()
    override fun getVM(): SalesViewModel = mViewModel

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var selectedItemsAdapter: SelectedItemsAdapter
    private lateinit var categoryAdapter: ArrayAdapter<Category>
    private var tableId: String? = null
    private var tableNumber: Int? = null
    private var hasOrder: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableId = arguments?.getString("tableId")
        tableNumber = arguments?.getInt("tableNumber")
        hasOrder = arguments?.getBoolean("hasOrder", false) ?: false
        Timber.d("SalesFragment created with tableId: $tableId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing SalesFragment view")
        setupViews()
        observeViewModel()

        if (hasOrder) {
            tableId?.let { mViewModel.loadOrderInfo(it) }
        }
    }

    private fun setupViews() {
        binding.textSelectedTable?.text = tableId?.let {
            "Bàn: $it (Số: $tableNumber)"
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

        itemsAdapter = ItemsAdapter { item, selectedSize ->
            mViewModel.addItemToCart(item, selectedSize)
        }
        binding.recyclerViewItems.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = itemsAdapter
        }

        selectedItemsAdapter = SelectedItemsAdapter(
            onQuantityChange = { uniqueKey, quantity ->
                mViewModel.updateItemQuantity(uniqueKey, quantity)
            },
            onItemDetailsChange = { itemId, selectedSize, note, oldUniqueKey ->
                mViewModel.updateItemDetails(itemId, selectedSize, note, oldUniqueKey)
            }
        )
        binding.recyclerViewSelectedItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectedItemsAdapter
        }

        binding.buttonCreateOrder?.setOnClickListener {
            tableId?.let { id ->
                mViewModel.createOrder(id)
            } ?: run {
                Toast.makeText(context, "Chưa chọn bàn", Toast.LENGTH_SHORT).show()
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
            selectedItemsAdapter.submitList(selectedItems)
        }

        mViewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            Timber.d("Updating total price in UI: $total")
            binding.textTotalPrice.text = "${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(total)}"
        }

        mViewModel.orderResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Timber.d("Creating order...")
                    Toast.makeText(context, "Đang tạo đơn hàng...", Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    Timber.d("Order created: ${result.data}")
                    Toast.makeText(context, "Tạo đơn hàng thành công! ID: ${result.data?.id}", Toast.LENGTH_LONG).show()
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Error creating order: ${result.message}")
                    Toast.makeText(context, "Lỗi: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        mViewModel.orderInfoResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Timber.d("Loading order info for tableId: $tableId")
                    Toast.makeText(context, "Đang tải thông tin đơn hàng...", Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    Timber.d("Order info loaded: ${result.data}")
                    Toast.makeText(context, "Tải thông tin đơn hàng thành công!", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Error loading order info: ${result.message}")
                    Toast.makeText(context, "Lỗi tải thông tin đơn hàng: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}