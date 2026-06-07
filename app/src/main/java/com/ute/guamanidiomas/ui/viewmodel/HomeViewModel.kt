package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.GameProgressManager
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.domain.repository.HomeRepository
import com.ute.guamanidiomas.domain.repository.LanguageRepository
import com.ute.guamanidiomas.domain.repository.AuthRepository
import com.ute.guamanidiomas.ui.home.HomeUiState
import com.ute.guamanidiomas.ui.home.UserStats
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
    private val gameProgressManager: GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHomeData() }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val user = authRepository.getStoredUser()
            val userName = user?.username ?: "Estudiante"

            try {
                val languagesDeferred = async { languageRepository.getLanguages() }
                val coursesDeferred = async { courseRepository.getCourses() }
                val dashboardDeferred = async { homeRepository.getStudentDashboard() }
                val achievementsDeferred = async { homeRepository.getAchievements() }

                val languagesResult = languagesDeferred.await()
                val coursesResult = coursesDeferred.await()
                val dashboardResult = dashboardDeferred.await()
                val achievementsResult = achievementsDeferred.await()

                var loadedLanguages = emptyList<com.ute.guamanidiomas.domain.model.Language>()
                languagesResult.onSuccess { loadedLanguages = it }

                var loadedCourses = emptyList<com.ute.guamanidiomas.domain.model.Course>()
                var currentError: String? = null
                coursesResult
                    .onSuccess { (courses, _) -> loadedCourses = courses }
                    .onFailure { error -> currentError = error.message }

                var achievementsCount = 0
                achievementsResult.onSuccess { list -> achievementsCount = list.size }

                // Dashboard del estudiante — fuente principal de stats
                var stats: UserStats? = null
                var completedLessons = 0
                var totalLessons = 0
                var progressPct = 0
                var certsCount = 0
                var activeClassrooms = 0

                dashboardResult.onSuccess { dashboard ->
                    stats = UserStats(
                        id = 0, user = 0, userEmail = "",
                        totalXp = dashboard.totalXp,
                        currentStreak = dashboard.currentStreak,
                        longestStreak = dashboard.longestStreak
                    )
                    completedLessons = dashboard.completedLessons
                    totalLessons = dashboard.totalLessons
                    progressPct = dashboard.progressPercentage
                    certsCount = dashboard.certificatesCount
                    activeClassrooms = dashboard.activeClassrooms
                    if (dashboard.achievementsCount > 0) achievementsCount = dashboard.achievementsCount

                    // Sincronizar con progreso local de juegos
                    gameProgressManager.syncWithBackend(
                        dashboard.totalXp, dashboard.currentStreak, dashboard.longestStreak
                    )
                }.onFailure {
                    // Fallback: usar GET /api/stats/ si dashboard no existe
                    homeRepository.getStats().onSuccess { list ->
                        list.firstOrNull()?.let { dto ->
                            stats = UserStats(
                                id = dto.id, user = dto.user, userEmail = dto.userEmail.orEmpty(),
                                totalXp = dto.totalXp, currentStreak = dto.currentStreak, longestStreak = dto.longestStreak
                            )
                            gameProgressManager.syncWithBackend(dto.totalXp, dto.currentStreak, dto.longestStreak)
                        }
                    }
                    // Fallback local si todo falla
                    if (stats == null) {
                        val local = gameProgressManager.getSnapshot()
                        if (local.totalXp > 0) {
                            stats = UserStats(0, 0, "", local.totalXp, local.currentStreak, local.longestStreak)
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = userName,
                        languages = loadedLanguages,
                        courses = loadedCourses,
                        error = currentError,
                        stats = stats,
                        unlockedAchievementsCount = achievementsCount,
                        completedLessonsCount = completedLessons,
                        totalLessonsCount = totalLessons,
                        progressPercentage = progressPct,
                        certificatesCount = certsCount,
                        activeClassrooms = activeClassrooms,
                        teacherTasks = emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.localizedMessage ?: "Error al cargar el Home")
                }
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            homeRepository.getStudentDashboard().onSuccess { dashboard ->
                val newStats = UserStats(
                    0, 0, "",
                    dashboard.totalXp, dashboard.currentStreak, dashboard.longestStreak
                )
                _uiState.update {
                    it.copy(
                        stats = newStats,
                        completedLessonsCount = dashboard.completedLessons,
                        totalLessonsCount = dashboard.totalLessons,
                        progressPercentage = dashboard.progressPercentage,
                        unlockedAchievementsCount = dashboard.achievementsCount
                    )
                }
            }
        }
    }
}
