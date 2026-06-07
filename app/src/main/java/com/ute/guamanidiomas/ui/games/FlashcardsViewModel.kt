package com.ute.guamanidiomas.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.GameProgressManager
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Flashcard(val english: String, val spanish: String)

data class FlashcardsUiState(
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val isLoading: Boolean = false,
    val level: Int = 1
)

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val progressManager: GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardsUiState())
    val uiState = _uiState.asStateFlow()

    private val levelCards = mapOf(
        1 to listOf(Flashcard("Hello", "Hola"), Flashcard("Goodbye", "Adios"), Flashcard("Please", "Por favor"), Flashcard("Thank you", "Gracias"), Flashcard("Yes", "Si"), Flashcard("No", "No"), Flashcard("Good", "Bueno"), Flashcard("Bad", "Malo")),
        2 to listOf(Flashcard("Knowledge", "Conocimiento"), Flashcard("Success", "Exito"), Flashcard("Experience", "Experiencia"), Flashcard("Opportunity", "Oportunidad"), Flashcard("Challenge", "Desafio"), Flashcard("Development", "Desarrollo"), Flashcard("Improvement", "Mejora"), Flashcard("Goal", "Meta")),
        3 to listOf(Flashcard("Accomplish", "Lograr"), Flashcard("Determine", "Determinar"), Flashcard("Establish", "Establecer"), Flashcard("Investigate", "Investigar"), Flashcard("Collaborate", "Colaborar"), Flashcard("Demonstrate", "Demostrar"), Flashcard("Negotiate", "Negociar"), Flashcard("Participate", "Participar")),
        4 to listOf(Flashcard("Procrastinate", "Procrastinar"), Flashcard("Sophisticated", "Sofisticado"), Flashcard("Controversial", "Controversial"), Flashcard("Comprehensive", "Comprensivo"), Flashcard("Circumstance", "Circunstancia"), Flashcard("Approximately", "Aproximadamente"), Flashcard("Nevertheless", "Sin embargo"), Flashcard("Simultaneously", "Simultaneamente")),
        5 to listOf(Flashcard("Quintessential", "Quintaesencia"), Flashcard("Serendipitous", "Fortuito"), Flashcard("Counterintuitive", "Contraintuitivo"), Flashcard("Unprecedented", "Sin precedentes"), Flashcard("Conscientious", "Concienzudo"), Flashcard("Idiosyncratic", "Idiosincratico"), Flashcard("Metamorphosis", "Metamorfosis"), Flashcard("Onomatopoeia", "Onomatopeya"))
    )

    init { loadLevel() }

    private fun loadLevel() {
        viewModelScope.launch {
            val level = progressManager.getLevel("flashcards").coerceIn(1, 5)
            startSession(level)
        }
    }

    fun startSession(level: Int = _uiState.value.level) {
        val cards = levelCards[level.coerceIn(1, 5)] ?: levelCards[1]!!
        _uiState.update { FlashcardsUiState(cards = cards.shuffled(), level = level) }
    }

    fun flipCard() { _uiState.update { it.copy(isFlipped = !it.isFlipped) } }

    fun nextCard(known: Boolean) {
        val xpPerCard = 15 + (_uiState.value.level * 5)
        val currentScore = if (known) _uiState.value.score + xpPerCard else _uiState.value.score
        if (_uiState.value.currentIndex < _uiState.value.cards.size - 1) {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1, isFlipped = false, score = currentScore) }
        } else {
            completeSession(currentScore)
        }
    }

    private fun completeSession(finalScore: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, score = finalScore) }
            progressManager.recordGameCompleted("flashcards", finalScore)
            gamificationRepository.postProgress(lessonId = 2, score = finalScore)
            _uiState.update { it.copy(isLoading = false, isCompleted = true) }
        }
    }
}
