package com.ute.guamanidiomas.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordPair(
    val english: String,
    val spanish: String,
    var isMatched: Boolean = false
)

data class WordMatchUiState(
    val wordsEnglish: List<String> = emptyList(),
    val wordsSpanish: List<String> = emptyList(),
    val selectedEnglish: String? = null,
    val selectedSpanish: String? = null,
    val matchedPairs: Set<String> = emptySet(),
    val matchedSpanish: Set<String> = emptySet(),
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val isSavingProgress: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class WordMatchViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val allPairs = listOf(
        WordPair("Apple", "Manzana"),
        WordPair("House", "Casa"),
        WordPair("Book", "Libro"),
        WordPair("Water", "Agua"),
        WordPair("School", "Escuela"),
        WordPair("Teacher", "Profesor"),
        WordPair("Student", "Estudiante"),
        WordPair("Table", "Mesa"),
        WordPair("Computer", "Computadora"),
        WordPair("Phone", "Teléfono"),
        WordPair("Friend", "Amigo"),
        WordPair("Family", "Familia"),
        WordPair("Bread", "Pan"),
        WordPair("Milk", "Leche"),
        WordPair("Coffee", "Café")
    )

    private val _uiState = MutableStateFlow(WordMatchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        startGame()
    }

    fun startGame() {
        val gamePairs = allPairs.shuffled().take(6)
        _uiState.value = WordMatchUiState(
            wordsEnglish = gamePairs.map { it.english }.shuffled(),
            wordsSpanish = gamePairs.map { it.spanish }.shuffled(),
            matchedPairs = emptySet(),
            matchedSpanish = emptySet(),
            score = 0,
            isGameOver = false
        )
    }

    fun selectEnglish(word: String) {
        if (_uiState.value.matchedPairs.contains(word)) return
        _uiState.value = _uiState.value.copy(selectedEnglish = word)
        checkMatch()
    }

    fun selectSpanish(word: String) {
        if (_uiState.value.matchedSpanish.contains(word)) return
        
        _uiState.value = _uiState.value.copy(selectedSpanish = word)
        checkMatch()
    }

    private fun checkMatch() {
        val state = _uiState.value
        val eng = state.selectedEnglish
        val spa = state.selectedSpanish

        if (eng != null && spa != null) {
            val pair = allPairs.find { it.english == eng && it.spanish == spa }
            if (pair != null) {
                // Correct match
                val newMatchedEng = state.matchedPairs + eng
                val newMatchedSpa = state.matchedSpanish + spa
                val newScore = state.score + 15
                _uiState.value = state.copy(
                    matchedPairs = newMatchedEng,
                    matchedSpanish = newMatchedSpa,
                    selectedEnglish = null,
                    selectedSpanish = null,
                    score = newScore,
                    message = "¡Correcto! +15 XP"
                )
                if (newMatchedEng.size == state.wordsEnglish.size) {
                    completeGame(newScore)
                }
            } else {
                // Wrong match
                viewModelScope.launch {
                    _uiState.value = state.copy(message = "Inténtalo de nuevo")
                    delay(800)
                    _uiState.value = _uiState.value.copy(
                        selectedEnglish = null,
                        selectedSpanish = null,
                        message = null
                    )
                }
            }
        }
    }

    private fun completeGame(finalScore: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingProgress = true)
            // Aquí simulamos el guardado en el backend usando el lessonId 1 (esto debería ser dinámico)
            // En un futuro el backend debería tener un endpoint específico para juegos o usar el de progreso.
            gamificationRepository.postProgress(lessonId = 1, score = finalScore)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isGameOver = true,
                        isSavingProgress = false,
                        message = "¡Felicidades! Progreso guardado."
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isGameOver = true,
                        isSavingProgress = false,
                        message = "Juego terminado (Error al sincronizar)"
                    )
                }
        }
    }
}

