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
        orderAdapter = OrderAdapter()
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
    }

    fun setNavController(navController: NavController) {
        mCoreNavigation.bind(navController)
        Timber.d("OrderFragment: NavController bound")
    }
}