package com.module.admin.order

import androidx.lifecycle.MutableLiveData
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.OrderInfoResponse
import com.module.domain.api.model.UserOrderResponse
import com.module.domain.api.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

sealed class OrderState {
    data object Loading : OrderState()
    data class Success(val orders: List<UserOrderResponse>) : OrderState()
    data class Error(val message: String?) : OrderState()
}

sealed class OrderInfoState {
    data object Loading : OrderInfoState()
    data class Success(val orderInfo: OrderInfoResponse) : OrderInfoState()
    data class Error(val message: String?) : OrderInfoState()
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    val orderState = MutableLiveData<OrderState>()
    val orderInfoState = MutableLiveData<OrderInfoState>()

    init {
        fetchMyOrders()
    }

    private fun fetchMyOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            orderRepository.getMyOrders().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Fetching my orders: Loading")
                        orderState.postValue(OrderState.Loading)
                    }
                    is Result.Success -> {
                        // Sắp xếp danh sách đơn hàng theo thời gian giảm dần
                        val sortedOrders = result.data?.sortedByDescending { order ->
                            try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                                    timeZone = TimeZone.getTimeZone("UTC")
                                }
                                inputFormat.parse(order.time)?.time ?: 0
                            } catch (e: Exception) {
                                0
                            }
                        } ?: emptyList()
                        Timber.d("Fetched my orders: ${sortedOrders.size} orders")
                        orderState.postValue(OrderState.Success(sortedOrders))
                    }
                    is Result.Error -> {
                        Timber.e("Error fetching my orders: ${result.message}")
                        orderState.postValue(OrderState.Error(result.message))
                    }
                }
            }
        }
    }

    fun fetchOrderInfo(orderId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            orderInfoState.postValue(OrderInfoState.Loading)
            orderRepository.getOrderInfo(orderId, null).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Không post lại Loading vì đã post ở trên
                    }
                    is Result.Success -> {
                        Timber.d("Fetched order info: $result")
                        result.data?.let { orderInfoState.postValue(OrderInfoState.Success(it)) }
                            ?: orderInfoState.postValue(OrderInfoState.Error("Không tìm thấy thông tin đơn hàng"))
                    }
                    is Result.Error -> {
                        Timber.e("Error fetching order info: ${result.message}")
                        orderInfoState.postValue(OrderInfoState.Error(result.message))
                    }
                }
            }
        }
    }
}