package com.module.admin.order

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.admin.order.databinding.FragmentOrderBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding, OrderViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_order

    private val mViewModel: OrderViewModel by viewModels()
    override fun getVM(): OrderViewModel = mViewModel

    @Inject
    lateinit var mCoreNavigation: CoreNavigation

    private lateinit var orderAdapter: OrderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { orderId ->
            mViewModel.fetchOrderInfo(orderId)
        }
        binding.orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
            setItemViewCacheSize(0)
        }
    }

    override fun observeViewModel() {
        mViewModel.orderState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is OrderState.Loading -> {
                    Timber.d("OrderFragment: Showing loading state")
                    binding.progressBar.visibility = View.VISIBLE
                    binding.orderRecyclerView.visibility = View.GONE
                    binding.errorTextView.visibility = View.GONE
                }
                is OrderState.Success -> {
                    Timber.d("OrderFragment: Showing ${state.orders.size} orders")
                    binding.progressBar.visibility = View.GONE
                    binding.orderRecyclerView.visibility = View.VISIBLE
                    binding.errorTextView.visibility = View.GONE
                    orderAdapter.submitList(state.orders)
                }
                is OrderState.Error -> {
                    Timber.e("OrderFragment: Error - ${state.message}")
                    binding.progressBar.visibility = View.GONE
                    binding.orderRecyclerView.visibility = View.GONE
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.errorTextView.text = state.message ?: "Đã xảy ra lỗi"
                    Toast.makeText(context, state.message ?: "Lỗi khi tải đơn hàng", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mViewModel.orderInfoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is OrderInfoState.Loading -> {
                    Timber.d("OrderFragment: Loading order info")
                    // Có thể thêm loading indicator nếu cần
                }
                is OrderInfoState.Success -> {
                    Timber.d("OrderFragment: Showing order info")
                    // Kiểm tra xem dialog đã tồn tại chưa
                    if (childFragmentManager.findFragmentByTag("OrderInfoDialog") == null) {
                        val dialog = OrderInfoDialogFragment.newInstance(state.orderInfo)
                        dialog.show(childFragmentManager, "OrderInfoDialog")
                    }
                }
                is OrderInfoState.Error -> {
                    Timber.e("OrderFragment: Error loading order info - ${state.message}")
                    Toast.makeText(context, state.message ?: "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}