package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.CoursePayload
import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.domain.model.ModulePayload
import com.ute.guamanidiomas.domain.repository.CourseFilters
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val courses: List<Course> = emptyList(),
    val modules: List<Module> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val total: Int = 0,
    val isDeleting: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val moduleRepository: ModuleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            courseRepository.getCourses(CourseFilters(pageSize = 100))
                .onSuccess { (courses, total) ->
                    _state.update { it.copy(
                        courses = courses,
                        total = total,
                        isLoading = false
                    ) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun deleteCourse(id: Int) {
        _state.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            courseRepository.deleteCourse(id)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false, deleteSuccess = true) }
                    loadCourses()
                }
                .onFailure { e ->
                    _state.update { it.copy(isDeleting = false, error = e.message) }
                }
        }
    }

    fun saveCourse(
        id: Int? = null,
        title: String,
        description: String,
        price: Double,
        level: String,
        isActive: Boolean,
        languageId: Int = 1
    ) {
        _state.update { it.copy(isSaving = true, error = null, saveSuccess = false) }
        viewModelScope.launch {
            try {
                val payload = CoursePayload(
                    languageId = languageId,
                    title = title,
                    description = description,
                    level = level,
                    price = price,
                    isActive = isActive
                )

                val result = if (id == null) {
                    courseRepository.createCourse(payload)
                } else {
                    courseRepository.updateCourse(id, payload)
                }

                result.onSuccess {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                    loadCourses()
                }
                .onFailure { e ->
                    _state.update { it.copy(isSaving = false, error = e.message ?: "No se pudo guardar el curso") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Error inesperado al guardar el curso") }
            }
        }
    }

    fun resetStatus() {
        _state.update { it.copy(deleteSuccess = false, saveSuccess = false, error = null) }
    }

    fun loadModules(courseId: Int) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            moduleRepository.getModulesByCourse(courseId)
                .onSuccess { modules ->
                    _state.update { it.copy(modules = modules, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun saveModule(
        id: Int? = null,
        courseId: Int,
        title: String,
        description: String,
        order: Int,
        isActive: Boolean
    ) {
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val payload = ModulePayload(
                courseId = courseId,
                title = title,
                description = description,
                order = order,
                isActive = isActive
            )
            
            val result = if (id == null) {
                moduleRepository.createModule(payload)
            } else {
                moduleRepository.updateModule(id, payload)
            }

            result.onSuccess {
                _state.update { it.copy(isSaving = false, saveSuccess = true) }
                loadModules(courseId)
            }
            .onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun deleteModule(id: Int, courseId: Int) {
        _state.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            moduleRepository.deleteModule(id)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false, deleteSuccess = true) }
                    loadModules(courseId)
                }
                .onFailure { e ->
                    _state.update { it.copy(isDeleting = false, error = e.message) }
                }
        }
    }
}
