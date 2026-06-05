package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Order
import com.ute.guamanidiomas.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CheckoutUiState {
    data object Idle : CheckoutUiState
    data object Loading : CheckoutUiState
    data class Success(val order: Order) : CheckoutUiState
    data class Error(val message: String) : CheckoutUiState
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun processCheckout(cartItems: List<CartItem>, onClearCart: () -> Unit) {
        if (_uiState.value is CheckoutUiState.Loading) return
        
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading
            
            // 1. Crear el pedido
            orderRepository.createOrder()
                .onSuccess { order ->
                    // 2. Añadir items del carrito al pedido
                    var lastOrder = order
                    
                    try {
                        cartItems.forEach { item ->
                            val result = orderRepository.addItem(order.id, item.course.id, item.quantity)
                            if (result.isSuccess) {
                                lastOrder = result.getOrThrow()
                            } else {
                                throw Exception("Error al añadir item: ${item.course.title}")
                            }
                        }
                        
                        // 3. Confirmar el pedido
                        orderRepository.confirmOrder(lastOrder.id)
                            .onSuccess { confirmedOrder ->
                                onClearCart()
                                _uiState.value = CheckoutUiState.Success(confirmedOrder)
                            }
                            .onFailure { e ->
                                _uiState.value = CheckoutUiState.Error(e.message ?: "Error al confirmar pedido")
                            }
                            
                    } catch (e: Exception) {
                        _uiState.value = CheckoutUiState.Error(e.message ?: "Error procesando el carrito")
                    }
                }
                .onFailure { e ->
                    _uiState.value = CheckoutUiState.Error(e.message ?: "Error al crear el pedido")
                }
        }
    }

    fun resetState() {
        _uiState.value = CheckoutUiState.Idle
    }
}
