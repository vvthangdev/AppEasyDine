package com.module.admin.sale

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.core.navigation.CoreNavigation
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseFragment
import com.module.admin.sale.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing CartFragment view")
        setupViews()
        observeViewModel()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        if (hasOrder) {
            tableId?.let { mViewModel.loadOrderInfo(it) }
        }
    }

    private fun setupViews() {
        binding.textSelectedTable?.text = tableId?.let {
            "Bàn số: $tableNumber"
        } ?: "Chuc ban ngon mieng"
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
            showOrderTypeDialog()
        }
    }

    private fun showOrderTypeDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Chọn loại đơn hàng")
            .setMessage("Bạn muốn đặt hàng ngay hay đặt bàn trước?")
            .setPositiveButton("Đặt hàng ngay") { _, _ ->
                mViewModel.createOrder(tableId)
                Timber.d("Creating order with tableId: $tableId")
            }
            .setNegativeButton("Đặt bàn trước") { _, _ ->
                showReserveTableDialog()
            }
            .setCancelable(true)
            .show()
    }

    private fun showReserveTableDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reserve_table, null)
        val editTextPeople = dialogView.findViewById<EditText>(R.id.editTextPeople)
        val textViewTime = dialogView.findViewById<TextView>(R.id.textViewTime)

        var selectedTime: LocalDateTime? = null

        textViewTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            selectedTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                            textViewTime.text = selectedTime?.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                            )
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Đặt bàn trước")
            .setView(dialogView)
            .setPositiveButton("Xác nhận") { _, _ ->
                val peopleAssigned = editTextPeople.text.toString().toIntOrNull()
                if (peopleAssigned == null || peopleAssigned <= 0) {
                    Toast.makeText(context, "Vui lòng nhập số người hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (selectedTime == null) {
                    Toast.makeText(context, "Vui lòng chọn thời gian", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val startTime = selectedTime!!.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .format(DateTimeFormatter.ISO_INSTANT)
                mViewModel.reserveTable(startTime, peopleAssigned, tableId)
                Timber.d("Reserving table with startTime: $startTime, people: $peopleAssigned, tableId: $tableId")
            }
            .setNegativeButton("Hủy", null)
            .setCancelable(true)
            .show()
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

        mViewModel.reserveTableResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReserveTableResultState.ResultState -> {
                    when (state.result) {
                        is Result.Loading -> {
                            Timber.d("Reserving table...")
                            Toast.makeText(context, "Đang đặt bàn...", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            Timber.d("Table reserved successfully")
                            Toast.makeText(context, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show()
                            mCoreNavigation.back()
                        }
                        is Result.Error -> {
                            Timber.e(state.result.exception, "Error reserving table: ${state.result.message}")
                            Toast.makeText(context, "Lỗi đặt bàn: ${state.result.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is ReserveTableResultState.Reset -> {
                    Timber.d("Reserve table result reset, no action taken")
                    // Không hiển thị Toast khi trạng thái reset
                }
            }
        }
    }
}