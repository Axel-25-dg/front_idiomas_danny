package com.ute.guamanidiomas.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinClassViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _joinSuccess = MutableStateFlow<Int?>(null)
    val joinSuccess = _joinSuccess.asStateFlow()

    fun joinClass(accessCode: String) {
        if (accessCode.isBlank()) {
            _error.value = "Ingresa un código válido"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.joinClass(accessCode)
                .onSuccess { course ->
                    _joinSuccess.value = course.id
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Error al unirse a la clase. Verifica el código."
                }
            
            _isLoading.value = false
        }
    }

    fun clearEvents() {
        _joinSuccess.value = null
        _error.value = null
    }
}
