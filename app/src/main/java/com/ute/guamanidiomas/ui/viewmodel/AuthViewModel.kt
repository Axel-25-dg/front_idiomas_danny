package com.ute.guamanidiomas.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.TokenDataStore
import com.ute.guamanidiomas.domain.model.LoggedUser
import com.ute.guamanidiomas.domain.repository.AuthRepository
import com.ute.guamanidiomas.ui.auth.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Conjunto de roles que van al panel de administrador
private val ADMIN_ROLES    = setOf("admin", "superuser", "superusuario", "administrador", "staff")
// Conjunto de roles que van al panel de profesor
private val TEACHER_ROLES  = setOf("teacher", "profesor", "docente", "instructor")

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<LoggedUser?>(null)
    val currentUser: StateFlow<LoggedUser?> = _currentUser.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = _currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ── Derivados basados EXCLUSIVAMENTE en role ──────────────────
    val userRole: StateFlow<String> = _currentUser
        .map { it?.role?.lowercase()?.trim() ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val isAdminRole: StateFlow<Boolean> = userRole
        .map { it in ADMIN_ROLES }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isTeacherRole: StateFlow<Boolean> = userRole
        .map { it in TEACHER_ROLES }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // isStaff se mantiene por compatibilidad de UI que lo muestra,
    // pero NUNCA se debe usar para decidir navegación
    val isStaff: StateFlow<Boolean> = _currentUser
        .map { it?.isStaff == true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            try {
                val snapshot = authRepository.getStoredUser()
                if (snapshot != null && authRepository.isLoggedIn()) {
                    val user = LoggedUser(
                        id       = snapshot.id,
                        username = snapshot.username,
                        email    = snapshot.email,
                        isStaff  = snapshot.isStaff,
                        role     = snapshot.role.lowercase().trim(),
                    )
                    _currentUser.value = user
                    Log.d("ROLE_DEBUG", "restoreSession → role=${user.role}")
                }
            } finally {
                _isCheckingSession.value = false
            }
        }
    }

    fun login(username: String, password: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(username.trim(), password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value     = AuthUiState.Success(user)
                    Log.d("ROLE_DEBUG", "login success → role=${user.role}")
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Error al iniciar sesión")
                }
        }
    }

    fun register(username: String, email: String, password: String, password2: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(username.trim(), email.trim(), password, password2)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value     = AuthUiState.Success(user)
                    Log.d("ROLE_DEBUG", "register success → role=${user.role}")
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Error al registrarse")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _uiState.value     = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
