package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartItem(
    val course:   Course,
    val quantity: Int,
)

sealed interface CheckoutState {
    data object Idle                          : CheckoutState
    data object Loading                       : CheckoutState
    data class  Success(val orderId: Int)     : CheckoutState
    data class  Error(val message: String)    : CheckoutState
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val totalItems: StateFlow<Int> = _items
        .map { it.sumOf { i -> i.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val subtotal: StateFlow<Double> = _items
        .map { it.sumOf { i -> i.course.price * i.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val tax: StateFlow<Double> = subtotal
        .map { it * 0.15 } 
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalWithTax: StateFlow<Double> = combine(subtotal, tax) { s, t -> s + t }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    fun addItem(course: Course, quantity: Int = 1) {
        _items.update { list ->
            val existing = list.find { it.course.id == course.id }
            if (existing != null) {
                list.map {
                    if (it.course.id == course.id)
                        it.copy(quantity = minOf(it.quantity + quantity, course.stock))
                    else it
                }
            } else {
                list + CartItem(course, minOf(quantity, course.stock))
            }
        }
    }

    fun updateQuantity(courseId: Int, quantity: Int) {
        if (quantity <= 0) removeItem(courseId)
        else _items.update { list ->
            list.map { if (it.course.id == courseId) it.copy(quantity = quantity) else it }
        }
    }

    fun removeItem(courseId: Int) {
        _items.update { it.filter { i -> i.course.id != courseId } }
    }

    fun clearCart() { _items.value = emptyList() }

    fun resetCheckout() { _checkoutState.value = CheckoutState.Idle }

    fun checkout() {
        val currentItems = _items.value
        if (currentItems.isEmpty()) {
            _checkoutState.value = CheckoutState.Error("El carrito está vacío")
            return
        }
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading

            val orderResult = orderRepository.createOrder()
            val order = orderResult.getOrElse {
                _checkoutState.value = CheckoutState.Error(it.message ?: "Error al crear pedido")
                return@launch
            }

            for (item in currentItems) {
                val addResult = orderRepository.addItem(order.id, item.course.id, item.quantity)
                if (addResult.isFailure) {
                    _checkoutState.value = CheckoutState.Error("Error al añadir ${item.course.title}")
                    return@launch
                }
            }

            val confirmResult = orderRepository.confirmOrder(order.id)
            val confirmed = confirmResult.getOrElse {
                _checkoutState.value = CheckoutState.Error(it.message ?: "Error al confirmar")
                return@launch
            }

            clearCart()
            _checkoutState.value = CheckoutState.Success(confirmed.id)
        }
    }
}
