package com.ute.guamanidiomas.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseUiState(
    val exercise: Exercise? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseUiState())
    val state: StateFlow<ExerciseUiState> = _state.asStateFlow()

    fun loadExercise(exerciseId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            exerciseRepository.getExerciseById(exerciseId)
                .onSuccess { exercise ->
                    _state.update { it.copy(exercise = exercise, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
