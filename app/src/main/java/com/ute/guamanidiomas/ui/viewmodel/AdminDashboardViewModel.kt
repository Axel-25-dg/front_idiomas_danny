package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val ordersByStatus: Map<String, Int> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            orderRepository.getStats()
                .onSuccess { stats ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            totalOrders = (stats["total_orders"] as? Number)?.toInt() ?: 0,
                            totalRevenue = (stats["total_revenue"] as? Number)?.toDouble() ?: 0.0,
                            ordersByStatus = (stats["by_status"] as? Map<*, *>)?.mapNotNull { (k, v) ->
                                if (k is String && v is Number) k to v.toInt() else null
                            }?.toMap() ?: emptyMap()
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
