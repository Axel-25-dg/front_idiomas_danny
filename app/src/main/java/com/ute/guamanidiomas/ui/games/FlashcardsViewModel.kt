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

data class Flashcard(
    val english: String,
    val spanish: String,
    val pronunciation: String = ""
)

data class FlashcardsUiState(
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardsUiState())
    val uiState = _uiState.asStateFlow()

    private val allCards = listOf(
        Flashcard("Knowledge", "Conocimiento"),
        Flashcard("Success", "Éxito"),
        Flashcard("Experience", "Experiencia"),
        Flashcard("Opportunity", "Oportunidad"),
        Flashcard("Challenge", "Desafío"),
        Flashcard("Development", "Desarrollo"),
        Flashcard("Improvement", "Mejora"),
        Flashcard("Goal", "Meta")
    )

    init {
        startSession()
    }

    fun startSession() {
        _uiState.update { 
            FlashcardsUiState(cards = allCards.shuffled()) 
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun nextCard(known: Boolean) {
        val currentScore = if (known) _uiState.value.score + 20 else _uiState.value.score
        
        if (_uiState.value.currentIndex < _uiState.value.cards.size - 1) {
            _uiState.update { 
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    isFlipped = false,
                    score = currentScore
                ) 
            }
        } else {
            completeSession(currentScore)
        }
    }

    private fun completeSession(finalScore: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, score = finalScore) }
            // Usamos lessonId 2 para Flashcards (referencia)
            gamificationRepository.postProgress(lessonId = 2, score = finalScore)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isCompleted = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, isCompleted = true) }
                }
        }
    }
}
