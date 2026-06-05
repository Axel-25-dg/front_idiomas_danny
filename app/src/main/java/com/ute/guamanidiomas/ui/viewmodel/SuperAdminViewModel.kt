package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.data.remote.dto.RoleDto
import com.ute.guamanidiomas.domain.repository.AdminConsoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuperAdminUiState(
    val roles: List<RoleDto> = emptyList(),
    val orders: List<OrderDto> = emptyList(),
    val auditLogs: List<AuditLogDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SuperAdminViewModel @Inject constructor(
    private val repository: AdminConsoleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SuperAdminUiState())
    val state: StateFlow<SuperAdminUiState> = _state.asStateFlow()

    fun loadRoles() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getRoles()
                .onSuccess { roles -> _state.update { it.copy(roles = roles, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun createRole(name: String, permissions: List<String>) {
        _state.update { it.copy(isSaving = true, error = null, successMessage = null) }
        viewModelScope.launch {
            repository.createRole(name, permissions)
                .onSuccess { role ->
                    _state.update {
                        it.copy(
                            roles = it.roles + role,
                            isSaving = false,
                            successMessage = "Rol creado"
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
        }
    }

    fun assignRoleToUser(userId: Int, role: String) {
        _state.update { it.copy(isSaving = true, error = null, successMessage = null) }
        viewModelScope.launch {
            repository.assignRoleToUser(userId, role)
                .onSuccess {
                    _state.update { it.copy(isSaving = false, successMessage = "Rol asignado") }
                }
                .onFailure { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
        }
    }

    fun loadOrders() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getOrders()
                .onSuccess { orders -> _state.update { it.copy(orders = orders, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun approveOrder(orderId: Int) {
        _state.update { it.copy(isSaving = true, error = null, successMessage = null) }
        viewModelScope.launch {
            repository.approveOrder(orderId)
                .onSuccess { updated ->
                    _state.update { state ->
                        state.copy(
                            orders = state.orders.map { if (it.id == orderId) updated else it },
                            isSaving = false,
                            successMessage = "Inscripcion aprobada"
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
        }
    }

    fun loadAuditLogs() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getAuditLogs()
                .onSuccess { logs -> _state.update { it.copy(auditLogs = logs, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}

