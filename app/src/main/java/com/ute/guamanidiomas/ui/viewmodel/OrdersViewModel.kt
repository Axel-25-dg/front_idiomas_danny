package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Order
import com.ute.guamanidiomas.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val totalOrders: Int = 0,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUiState())
    val state: StateFlow<OrdersUiState> = _state.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            orderRepository.getOrders()
                .onSuccess { (orders, total) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            orders = orders,
                            totalOrders = total,
                            isRefreshing = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, error = e.message, isRefreshing = false)
                    }
                }
        }
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadOrders()
    }
}