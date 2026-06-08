package com.ute.guamanidiomas.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherUiState(
    val isLoading: Boolean = false,
    val classes: List<Course> = emptyList(),
    val totalStudents: Int = 0,
    val error: String? = null
)

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTeacherData()
    }

    fun loadTeacherData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            courseRepository.getCourses()
                .onSuccess { (courses, _) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        classes = courses,
                        totalStudents = courses.size * 15
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar datos del profesor"
                    )
                }
        }
    }
}