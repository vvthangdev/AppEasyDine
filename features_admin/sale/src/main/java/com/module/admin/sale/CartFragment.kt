package com.module.admin.sale

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.admin.sale.databinding.FragmentCartBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import com.module.domain.api.model.CartItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding, CartViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_cart

    private val mViewModel: CartViewModel by activityViewModels()
    @Inject
    lateinit var mCoreNavigation: CoreNavigation

    private lateinit var cartAdapter: CartAdapter

    override fun getVM(): CartViewModel = mViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup recycler view
        cartAdapter = CartAdapter(mViewModel)
        binding.cartItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        // Setup back button
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Setup clear cart button
        binding.clearCartButton.setOnClickListener {
            mViewModel.clearCart()
        }
    }

    override fun observeViewModel() {
        mViewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CartState.CartUpdated -> {
                    cartAdapter.submitList(state.cartItems)
                    binding.cartItemsRecyclerView.visibility = if (state.cartItems.isEmpty()) View.GONE else View.VISIBLE
                    binding.emptyCartMessage.visibility = if (state.cartItems.isEmpty()) View.VISIBLE else View.GONE
                    binding.totalPriceTextView.text = "Tổng tiền: ${state.totalPrice} VNĐ"
                }
                is CartState.Error -> {
                    // Handle error if needed
                }
            }
        }
    }

    override fun onDestroyView() {
        // mCoreNavigation.unbind()
        super.onDestroyView()
    }
}