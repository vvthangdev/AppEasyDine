package com.module.admin.sale

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.admin.cart.databinding.FragmentCartBinding
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding, SalesViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_cart

    private val mViewModel: SalesViewModel by viewModels()
    override fun getVM(): SalesViewModel = mViewModel

    private lateinit var selectedItemsAdapter: SelectedItemsAdapter
    private var tableId: String? = null
    private var tableNumber: Int? = null
    private var hasOrder: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableId = arguments?.getString("tableId")
        tableNumber = arguments?.getInt("tableNumber")
        hasOrder = arguments?.getBoolean("hasOrder", false) ?: false
        Timber.d("CartFragment created with tableId: $tableId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing CartFragment view")
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
                    Toast.makeText(context, "Tạo đơn hàng thành công!", Toast.LENGTH_LONG).show()
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