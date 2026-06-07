package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.HomeRepository
import com.ute.guamanidiomas.domain.repository.LanguageRepository
import com.ute.guamanidiomas.domain.repository.AuthRepository
import com.ute.guamanidiomas.ui.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val languageRepository: LanguageRepository,
    private val homeRepository: HomeRepository,
    private val authRepository: AuthRepository,
    private val gameProgressManager: com.ute.guamanidiomas.data.local.GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val user = authRepository.getStoredUser()
            val userName = user?.username ?: "Estudiante"

            try {
                val languagesDeferred = async { languageRepository.getLanguages() }
                val coursesDeferred = async { courseRepository.getCourses() }
                val statsDeferred = async { homeRepository.getStats() }
                val achievementsDeferred = async { homeRepository.getAchievements() }
                val progressDeferred = async { homeRepository.getProgress() }

                val languagesResult = languagesDeferred.await()
                val coursesResult = coursesDeferred.await()
                val statsResult = statsDeferred.await()
                val achievementsResult = achievementsDeferred.await()
                val progressResult = progressDeferred.await()

                var loadedLanguages = emptyList<com.ute.guamanidiomas.domain.model.Language>()
                languagesResult.onSuccess { loadedLanguages = it }

                var loadedCourses = emptyList<com.ute.guamanidiomas.domain.model.Course>()
                var currentError: String? = null
                coursesResult
                    .onSuccess { (courses, _) -> loadedCourses = courses }
                    .onFailure { error -> currentError = error.message }

                var stats: com.ute.guamanidiomas.ui.home.UserStats? = null
                statsResult.onSuccess { list ->
                    list.firstOrNull()?.let { dto ->
                        stats = com.ute.guamanidiomas.ui.home.UserStats(
                            id = dto.id,
                            user = dto.user,
                            userEmail = dto.userEmail,
                            totalXp = dto.totalXp,
                            currentStreak = dto.currentStreak,
                            longestStreak = dto.longestStreak
                        )
                        // Sincronizar con datos locales de juegos
                        gameProgressManager.syncWithBackend(dto.totalXp, dto.currentStreak, dto.longestStreak)
                    }
                }

                // Si el backend no devolvio stats, usar datos locales de juegos
                if (stats == null) {
                    val local = gameProgressManager.getSnapshot()
                    if (local.totalXp > 0 || local.currentStreak > 0) {
                        stats = com.ute.guamanidiomas.ui.home.UserStats(
                            id = 0, user = 0, userEmail = "",
                            totalXp = local.totalXp,
                            currentStreak = local.currentStreak,
                            longestStreak = local.longestStreak
                        )
                    }
                }

                // Si no hay estadísticas del HomeRepository (que a veces devuelve una lista vacía), 
                // podríamos intentar obtenerlas del GamificationRepository si fuera necesario, 
                // pero por ahora confiaremos en los endpoints del home.

                var achievementsCount = 0
                achievementsResult.onSuccess { list -> achievementsCount = list.size }

                var lessonProgressList = emptyList<com.ute.guamanidiomas.ui.home.UserProgress>()
                progressResult.onSuccess { response ->
                    lessonProgressList = response.results.map { progressDto ->
                        com.ute.guamanidiomas.ui.home.UserProgress(
                            id = progressDto.id,
                            user = progressDto.user,
                            userEmail = progressDto.userEmail,
                            lesson = progressDto.lesson,
                            lessonTitle = progressDto.lessonTitle,
                            status = progressDto.status,
                            score = progressDto.score,
                            completedAt = progressDto.completedAt
                        )
                    }
                }

                // Simulación de Tareas del Profesor — removido: no hay endpoint disponible
                // La sección teacherTasks se dejó vacía para no mostrar datos ficticios

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        userName = userName,
                        languages = loadedLanguages,
                        courses = loadedCourses,
                        error = currentError,
                        stats = stats,
                        unlockedAchievementsCount = achievementsCount,
                        lessonProgressList = lessonProgressList,
                        teacherTasks = emptyList()   // sin datos quemados
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Error desconocido al cargar el Home"
                    )
                }
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            homeRepository.getStats().onSuccess { list ->
                list.firstOrNull()?.let { dto ->
                    val newStats = com.ute.guamanidiomas.ui.home.UserStats(
                        id = dto.id,
                        user = dto.user,
                        userEmail = dto.userEmail,
                        totalXp = dto.totalXp,
                        currentStreak = dto.currentStreak,
                        longestStreak = dto.longestStreak
                    )
                    _uiState.update { it.copy(stats = newStats) }
                }
            }
            homeRepository.getAchievements().onSuccess { list ->
                _uiState.update { it.copy(unlockedAchievementsCount = list.size) }
            }
        }
    }
}