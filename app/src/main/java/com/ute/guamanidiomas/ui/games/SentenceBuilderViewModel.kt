package com.ute.guamanidiomas.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SentenceGame(
    val fullSentence: String,
    val spanishTranslation: String,
    val scrambledWords: List<String>
)

data class SentenceBuilderUiState(
    val currentSentence: SentenceGame? = null,
    val selectedWords: List<String> = emptyList(),
    val availableWords: List<String> = emptyList(),
    val isCorrect: Boolean? = null,
    val score: Int = 0,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val currentLevel: Int = 0
)

@HiltViewModel
class SentenceBuilderViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val progressManager: com.ute.guamanidiomas.data.local.GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SentenceBuilderUiState())
    val uiState = _uiState.asStateFlow()

    private val levels = listOf(
        SentenceGame("I love learning English", "Me encanta aprender inglés", listOf("I", "love", "learning", "English")),
        SentenceGame("The cat is on the table", "El gato está sobre la mesa", listOf("The", "cat", "is", "on", "the", "table")),
        SentenceGame("We go to the park every day", "Vamos al parque todos los días", listOf("We", "go", "to", "the", "park", "every", "day")),
        SentenceGame("She speaks three different languages", "Ella habla tres idiomas diferentes", listOf("She", "speaks", "three", "different", "languages"))
    )

    init {
        loadLevel(0)
    }

    fun loadLevel(index: Int) {
        if (index < levels.size) {
            val level = levels[index]
            _uiState.update {
                it.copy(
                    currentSentence = level,
                    availableWords = level.scrambledWords.shuffled(),
                    selectedWords = emptyList(),
                    isCorrect = null,
                    currentLevel = index
                )
            }
        } else {
            completeGame()
        }
    }

    fun selectWord(word: String) {
        val currentSelected = _uiState.value.selectedWords.toMutableList()
        val currentAvailable = _uiState.value.availableWords.toMutableList()

        if (currentAvailable.contains(word)) {
            currentAvailable.remove(word)
            currentSelected.add(word)
            _uiState.update {
                it.copy(
                    selectedWords = currentSelected,
                    availableWords = currentAvailable
                )
            }
        }
    }

    fun removeWord(word: String) {
        val currentSelected = _uiState.value.selectedWords.toMutableList()
        val currentAvailable = _uiState.value.availableWords.toMutableList()

        if (currentSelected.contains(word)) {
            currentSelected.remove(word)
            currentAvailable.add(word)
            _uiState.update {
                it.copy(
                    selectedWords = currentSelected,
                    availableWords = currentAvailable
                )
            }
        }
    }

    fun checkSentence() {
        val state = _uiState.value
        val level = state.currentSentence ?: return
        val constructed = state.selectedWords.joinToString(" ")
        
        if (constructed == level.fullSentence) {
            _uiState.update { it.copy(isCorrect = true, score = it.score + 25) }
        } else {
            _uiState.update { it.copy(isCorrect = false) }
        }
    }

    private fun completeGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            progressManager.recordGameCompleted("sentence_builder", _uiState.value.score)
            gamificationRepository.postProgress(lessonId = 3, score = _uiState.value.score)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isCompleted = true) } }
                .onFailure { _uiState.update { it.copy(isLoading = false, isCompleted = true) } }
        }
    }
}
