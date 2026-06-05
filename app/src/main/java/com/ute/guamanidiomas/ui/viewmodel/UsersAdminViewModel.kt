package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.User
import com.ute.guamanidiomas.domain.repository.AdminUsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)

@HiltViewModel
class UsersAdminViewModel @Inject constructor(
    private val repository: AdminUsersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        loadStaffUsers()
    }

    fun loadStaffUsers() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getUsers()
                .onSuccess { allUsers ->
                    // Filter to only display users who are staff
                    val staff = allUsers.filter { it.isStaff || it.role in setOf("teacher", "manager", "employee") }
                    _uiState.update { it.copy(users = staff, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Error al cargar personal") }
                }
        }
    }

    fun toggleUserActive(userId: Int, currentActive: Boolean) {
        viewModelScope.launch {
            repository.updateUser(userId, isActive = !currentActive)
                .onSuccess { updatedUser ->
                    _uiState.update { state ->
                        state.copy(
                            users = state.users.map { if (it.id == userId) updatedUser else it }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message ?: "Error al actualizar estado del usuario") }
                }
        }
    }

    fun createUser(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        role: String,
        passwordProvisional: String
    ) {
        _uiState.update { it.copy(isSubmitting = true, error = null, submitSuccess = false) }
        viewModelScope.launch {
            repository.createUser(
                username = username,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role,
                passwordProvisional = passwordProvisional
            ).onSuccess { newUser ->
                _uiState.update { state ->
                    state.copy(
                        users = state.users + newUser,
                        isSubmitting = false,
                        submitSuccess = true
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, error = error.message ?: "Error al crear usuario") }
            }
        }
    }

    fun resetSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = false, error = null) }
    }
}
