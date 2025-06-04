package com.module.admin.sale

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.core.navigation.CoreNavigation
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseFragment
import com.module.admin.sale.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding, SalesViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_cart

    private val mViewModel: SalesViewModel by activityViewModels()
    override fun getVM(): SalesViewModel = mViewModel

    @Inject
    lateinit var mCoreNavigation: CoreNavigation

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

        // Handle back press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.d("Back pressed in CartFragment")
                if (mCoreNavigation.back()) {
                    Timber.d("Navigated up successfully")
                } else {
                    Timber.w("Failed to navigate up")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
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
        } ?: "Mang về"
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
            mViewModel.createOrder(tableId)
            Timber.d("Creating order with tableId: $tableId")
        }
    }

    override fun observeViewModel() {
        mViewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
            selectedItemsAdapter.submitList(selectedItems)
            Timber.d("CartFragment: Updated selectedItems with ${selectedItems.size} items")
        }

        mViewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            Timber.d("Updating total price in UI: $total")
            binding.textTotalPrice.text = "${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(total)}"
        }

        mViewModel.orderResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is OrderResultState.ResultState -> {
                    when (state.result) {
                        is Result.Loading -> {
                            Timber.d("Creating order...")
                            Toast.makeText(context, "Đang tạo đơn hàng...", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            Timber.d("Order created successfully")
                            Toast.makeText(context, "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show()
                            mCoreNavigation.back()
                        }
                        is Result.Error -> {
                            Timber.e(state.result.message, "Error saving order: ${state.result.message}")
                            Toast.makeText(context, "Lỗi: ${state.result.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is OrderResultState.Reset -> {
                    Timber.d("Order result reset, no action taken")
                    // No action needed, allows viewing cart
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
                    Toast.makeText(context, "Lỗi tải đơn hàng: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}