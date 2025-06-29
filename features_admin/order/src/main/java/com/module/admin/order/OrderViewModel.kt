package com.module.admin.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.UserOrderResponse
import com.module.domain.api.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class OrderState {
    data object Loading : OrderState()
    data class Success(val orders: List<UserOrderResponse>) : OrderState()
    data class Error(val message: String?) : OrderState()
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    val orderState = MutableLiveData<OrderState>()

    init {
        fetchMyOrders()
    }

    fun fetchMyOrders() {
        viewModelScope.launch {
            orderRepository.getMyOrders().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        Timber.d("Fetching my orders: Loading")
                        orderState.postValue(OrderState.Loading)
                    }
                    is Result.Success -> {
                        Timber.d("Fetched my orders: ${result.data?.size ?: 0} orders")
                        orderState.postValue(OrderState.Success(result.data ?: emptyList()))
                    }
                    is Result.Error -> {
                        Timber.e("Error fetching my orders: ${result.message}")
                        orderState.postValue(OrderState.Error(result.message))
                    }
                }
            }
        }
    }
}