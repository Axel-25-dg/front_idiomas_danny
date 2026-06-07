package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CourseDetailUiState {
    data object Loading                                                 : CourseDetailUiState
    data class  Success(val course: Course, val modules: List<Module>) : CourseDetailUiState
    data class  Error(val message: String)                              : CourseDetailUiState
}

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val moduleRepository: ModuleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val state: StateFlow<CourseDetailUiState> = _state.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = CourseDetailUiState.Loading
            
            val courseResult = courseRepository.getCourseById(id)
            val modulesResult = moduleRepository.getModulesByCourse(id)
            
            val course = courseResult.getOrNull()
            if (courseResult.isSuccess && course != null) {
                _state.value = CourseDetailUiState.Success(
                    course = course,
                    modules = modulesResult.getOrDefault(emptyList())
                )
            } else {
                _state.value = CourseDetailUiState.Error(
                    courseResult.exceptionOrNull()?.message ?: "No se encontró el curso solicitado"
                )
            }
        }
    }
}
