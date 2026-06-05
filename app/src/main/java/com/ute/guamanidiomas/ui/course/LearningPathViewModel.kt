package com.ute.guamanidiomas.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.domain.repository.ExerciseRepository
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import com.ute.guamanidiomas.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningPathUiState(
    val modules: List<Module> = emptyList(),
    val exercisesByModule: Map<Int, List<Exercise>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val completedExerciseIds: Set<Int> = emptySet()
)

@HiltViewModel
class LearningPathViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val exerciseRepository: ExerciseRepository,
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LearningPathUiState())
    val state: StateFlow<LearningPathUiState> = _state.asStateFlow()

    fun loadPath(courseId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            moduleRepository.getModulesByCourse(courseId)
                .onSuccess { modules ->
                    val exerciseDeferreds = modules.map { module ->
                        async { module.id to exerciseRepository.getExercisesByModule(module.id) }
                    }

                    val exercisesMap = exerciseDeferreds.awaitAll()
                        .associate { (moduleId, result) ->
                            moduleId to (result.getOrNull() ?: emptyList())
                        }

                    val progressResult = gamificationRepository.getProgressHistory(page = 1)
                    val completedLessonIds = progressResult.getOrNull()
                        ?.results
                        ?.filter { it.status == "completed" }
                        ?.map { it.lessonId }
                        ?.toSet()
                        ?: emptySet()

                    _state.update {
                        it.copy(
                            modules              = modules,
                            exercisesByModule    = exercisesMap,
                            completedExerciseIds = completedLessonIds,
                            isLoading            = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun markExerciseCompleted(exerciseId: Int) {
        _state.update { current ->
            current.copy(
                completedExerciseIds = current.completedExerciseIds + exerciseId
            )
        }
    }
}
