package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.Language
import com.ute.guamanidiomas.domain.repository.CourseFilters
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CatalogUiState(
    val courses: List<Course> = emptyList(),
    val languages: List<Language> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val total: Int = 0,
    val hasMore: Boolean = false,
    val search: String = "",
    val selectedLanguage: Int? = null,
    val page: Int = 1,
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadLanguages()
        load()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            languageRepository.getLanguages().onSuccess { langs ->
                _state.update { it.copy(languages = langs.filter { l -> l.isActive }) }
            }
        }
    }

    fun load(reset: Boolean = true) {
        val current = _state.value
        val page = if (reset) 1 else current.page

        if (reset) {
            _state.update { it.copy(isLoading = true, error = null, page = 1) }
        } else {
            if (current.isLoadingMore || !current.hasMore) return
            _state.update { it.copy(isLoadingMore = true) }
        }

        viewModelScope.launch {
            val filters = CourseFilters(
                search = current.search.ifBlank { null },
                languageId = current.selectedLanguage,
                isActive = true,
                page = page,
                pageSize = 12,
            )
            courseRepository.getCourses(filters)
                .onSuccess { (courses, total) ->
                    _state.update { s ->
                        s.copy(
                            courses = if (reset) courses else s.courses + courses,
                            total = total,
                            hasMore = (if (reset) courses else s.courses + courses).size < total,
                            isLoading = false,
                            isLoadingMore = false,
                            page = page + 1,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, isLoadingMore = false, error = e.message) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            load(reset = true)
        }
    }

    fun setLanguage(id: Int?) {
        _state.update { it.copy(selectedLanguage = id) }
        load(reset = true)
    }

    fun loadMore() = load(reset = false)
    fun refresh()  = load(reset = true)
}