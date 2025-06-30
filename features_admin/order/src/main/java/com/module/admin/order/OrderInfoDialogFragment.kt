package com.module.admin.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.module.admin.order.databinding.DialogOrderInfoBinding
import com.module.domain.api.model.OrderInfoResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderInfoDialogFragment : DialogFragment() {

    private var _binding: DialogOrderInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogOrderInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy OrderInfoResponse từ arguments
        val orderInfoJson = arguments?.getString(KEY_ORDER_INFO)
        val orderInfo = orderInfoJson?.let { Gson().fromJson(it, OrderInfoResponse::class.java) }

        if (orderInfo != null) {
            bindOrderInfo(orderInfo)
        } else {
            dismiss() // Đóng dialog nếu không có dữ liệu
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun bindOrderInfo(orderInfo: OrderInfoResponse) {
        // Format time
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        val formattedTime = try {
            val date = inputFormat.parse(orderInfo.order.time)
            outputFormat.format(date)
        } catch (e: Exception) {
            orderInfo.order.time
        }

        // Bind order details
        binding.orderTimeTextView.text = "Thời gian: $formattedTime"
        binding.orderStatusTextView.text = "Trạng thái: ${orderInfo.order.status}"
        binding.orderAmountTextView.text = "Tổng tiền: ${orderInfo.order.finalAmount} VNĐ"

        // Bind tables
        val tablesText = orderInfo.reservedTables.joinToString("\n") { table ->
            "Bàn số ${table.tableNumber} (Khu vực: ${table.area}, Sức chứa: ${table.capacity})"
        }
        binding.tablesTextView.text = if (tablesText.isNotEmpty()) tablesText else "Không có bàn"

        // Bind items
        val itemsText = orderInfo.itemOrders.joinToString("\n") { item ->
            "${item.itemName} x${item.quantity} (${item.size ?: "Mặc định"}) - ${item.itemPrice} VNĐ"
        }
        binding.itemsTextView.text = if (itemsText.isNotEmpty()) itemsText else "Không có món"
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_ORDER_INFO = "key_order_info"

        fun newInstance(orderInfo: OrderInfoResponse): OrderInfoDialogFragment {
            return OrderInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ORDER_INFO, Gson().toJson(orderInfo))
                }
            }
        }
    }
}