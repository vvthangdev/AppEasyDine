package com.module.admin.sale

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.admin.sale.databinding.FragmentCartBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import com.module.core.utils.extensions.sharedviewmodel.ShareViewModel
import com.module.domain.api.model.CartItem
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding, CartViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_cart

    private val mViewModel: CartViewModel by activityViewModels()
    private val sharedViewModel: ShareViewModel by activityViewModels()

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
        cartAdapter = CartAdapter(this, mViewModel)
        binding.cartItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
            setItemViewCacheSize(0)
        }

        // Setup back button
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Setup clear cart button
        binding.clearCartButton.setOnClickListener {
            mViewModel.clearCart()
        }

        binding.placeOrderButton.setOnClickListener {
            val cartState = mViewModel.cartState.value
            if (cartState is CartState.CartUpdated && cartState.cartItems.isEmpty()) {
                Toast.makeText(context, "Giỏ hàng trống, vui lòng thêm món!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tableId = sharedViewModel.selectedTableId.value
            if (tableId != null) {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault())
                dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")

                val startTime = dateTimeFormat.format(calendar.time)
                calendar.add(Calendar.HOUR, 2)
                val endTime = dateTimeFormat.format(calendar.time)

                mViewModel.createOrder(tableId, startTime, endTime, sharedViewModel)
                Timber.d("Creating order with tableId: $tableId, startTime: $startTime, endTime: $endTime")
            } else {
                showReservationDialog()
            }
        }

    }

    private fun showReservationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reservation, null)
        val peopleSpinner = dialogView.findViewById<android.widget.Spinner>(R.id.peopleSpinner)
        val dateButton = dialogView.findViewById<android.widget.Button>(R.id.dateButton)
        val timeButton = dialogView.findViewById<android.widget.Button>(R.id.timeButton)

        // Setup spinner for number of people
        val peopleOptions = (1..20).map { it.toString() }
        peopleSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, peopleOptions)

        // Setup date and time
        val calendar = Calendar.getInstance()
        var selectedDateTime = calendar.timeInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        dateButton.text = dateFormat.format(calendar.time)
        timeButton.text = timeFormat.format(calendar.time)

        // Date picker
        dateButton.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDateTime = calendar.timeInMillis
                    dateButton.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Time picker
        timeButton.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    selectedDateTime = calendar.timeInMillis
                    timeButton.text = timeFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Show dialog
        AlertDialog.Builder(requireContext())
//            .setTitle("Đặt bàn")
            .setView(dialogView)
            .setPositiveButton("Xác nhận") { _, _ ->
                // Format startTime in UTC
                val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = selectedDateTime
                    add(Calendar.HOUR, -7) // Chuyển đổi múi giờ
                }
                val startTime = dateFormat.format(utcCalendar.time) + "T" + timeFormat.format(utcCalendar.time) + "Z"
                val peopleAssigned = peopleSpinner.selectedItem.toString().toInt()
                mViewModel.reserveTable(startTime, peopleAssigned, sharedViewModel)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun observeViewModel() {
        mViewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CartState.CartUpdated -> {
                    if (cartAdapter.currentList != state.cartItems) {
                        cartAdapter.submitList(state.cartItems)
                        Timber.d("Cart updated with ${state.cartItems.size} items: ${state.cartItems}")
                    }
                    binding.cartItemsRecyclerView.visibility = if (state.cartItems.isEmpty()) View.GONE else View.VISIBLE
                    binding.emptyCartMessage.visibility = if (state.cartItems.isEmpty()) View.VISIBLE else View.GONE
                    binding.totalPriceTextView.text = "Tổng tiền: ${state.totalPrice} VNĐ"
                }
                is CartState.Error -> {
                    Toast.makeText(context, "Lỗi: ${state.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                is CartState.ReservationSuccess -> {
                    Toast.makeText(context, "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show()
                    mViewModel.clearCart()
                }
                is CartState.ReservationError -> {
                    Toast.makeText(context, "Lỗi tạo đơn hàng: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}