package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.StudentStats
import com.ute.guamanidiomas.domain.model.UserAchievement
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamificationUiState(
    val isLoading: Boolean = false,
    val stats: StudentStats? = null,
    val achievements: List<UserAchievement> = emptyList(),
    val error: String? = null,
    val lastProgressResult: ProgressResult? = null
)

sealed interface ProgressResult {
    data class Success(val totalXp: Int, val currentStreak: Int) : ProgressResult
    data class Error(val message: String) : ProgressResult
}

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GamificationUiState())
    val state: StateFlow<GamificationUiState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val statsDeferred        = async { gamificationRepository.getMyStats() }
            val achievementsDeferred = async { gamificationRepository.getMyAchievements() }

            val statsResult        = statsDeferred.await()
            val achievementsResult = achievementsDeferred.await()

            _state.update { current ->
                current.copy(
                    isLoading    = false,
                    stats        = statsResult.getOrNull()?.firstOrNull(),
                    achievements = achievementsResult.getOrNull() ?: emptyList(),
                    error        = statsResult.exceptionOrNull()?.message
                        ?: achievementsResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun completeLesson(lessonId: Int, score: Int = 100) {
        viewModelScope.launch {
            gamificationRepository.postProgress(lessonId = lessonId, score = score)
                .onSuccess {
                    val freshStats = gamificationRepository.getMyStats()
                        .getOrNull()?.firstOrNull()

                    _state.update { current ->
                        current.copy(
                            stats = freshStats ?: current.stats,
                            lastProgressResult = ProgressResult.Success(
                                totalXp       = freshStats?.totalXp ?: current.stats?.totalXp ?: 0,
                                currentStreak = freshStats?.currentStreak ?: current.stats?.currentStreak ?: 0
                            )
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            lastProgressResult = ProgressResult.Error(
                                e.message ?: "Error al registrar progreso"
                            )
                        )
                    }
                }
        }
    }

    fun clearProgressResult() {
        _state.update { it.copy(lastProgressResult = null) }
    }
}