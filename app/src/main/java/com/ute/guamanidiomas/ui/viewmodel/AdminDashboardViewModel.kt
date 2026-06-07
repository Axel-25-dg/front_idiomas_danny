package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.repository.AdminConsoleRepository
import com.ute.guamanidiomas.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    // Métricas del dashboard principal
    val totalUsers: Int = 0,
    val totalTeachers: Int = 0,
    val totalStudents: Int = 0,
    val totalCourses: Int = 0,
    val totalClassrooms: Int = 0,
    val totalSubscriptions: Int = 0,
    val totalPayments: Int = 0,
    val totalCertificates: Int = 0,
    // Métricas de órdenes
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val ordersByStatus: Map<String, Int> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val adminConsoleRepository: AdminConsoleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val dashboardDeferred = async { adminConsoleRepository.getAdminDashboard() }
            val ordersDeferred = async { orderRepository.getStats() }

            val dashboardResult = dashboardDeferred.await()
            val ordersResult = ordersDeferred.await()

            dashboardResult.onSuccess { dashboard ->
                _uiState.update {
                    it.copy(
                        totalUsers = dashboard.users,
                        totalTeachers = dashboard.teachers,
                        totalStudents = dashboard.students,
                        totalCourses = dashboard.courses,
                        totalClassrooms = dashboard.classrooms,
                        totalSubscriptions = dashboard.subscriptions,
                        totalPayments = dashboard.payments,
                        totalCertificates = dashboard.certificates
                    )
                }
            }

            ordersResult.onSuccess { stats ->
                _uiState.update {
                    it.copy(
                        totalOrders = (stats["total_orders"] as? Number)?.toInt() ?: 0,
                        totalRevenue = (stats["total_revenue"] as? Number)?.toDouble() ?: 0.0,
                        ordersByStatus = (stats["by_status"] as? Map<*, *>)?.mapNotNull { (k, v) ->
                            if (k is String && v is Number) k to v.toInt() else null
                        }?.toMap() ?: emptyMap()
                    )
                }
            }

            val error = dashboardResult.exceptionOrNull()?.message
                ?: ordersResult.exceptionOrNull()?.message

            _uiState.update { it.copy(isLoading = false, error = error) }
        }
    }
}
