package com.ute.guamanidiomas.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.GameProgressManager
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordPair(val english: String, val spanish: String)

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
    val message: String? = null,
    val level: Int = 1
)

@HiltViewModel
class WordMatchViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val progressManager: GameProgressManager
) : ViewModel() {

    private val levelWords = mapOf(
        1 to listOf(WordPair("Dog", "Perro"), WordPair("Cat", "Gato"), WordPair("House", "Casa"), WordPair("Book", "Libro"), WordPair("Water", "Agua"), WordPair("Sun", "Sol"), WordPair("Moon", "Luna"), WordPair("Tree", "Arbol")),
        2 to listOf(WordPair("Apple", "Manzana"), WordPair("School", "Escuela"), WordPair("Teacher", "Profesor"), WordPair("Student", "Estudiante"), WordPair("Computer", "Computadora"), WordPair("Phone", "Telefono"), WordPair("Friend", "Amigo"), WordPair("Family", "Familia")),
        3 to listOf(WordPair("Knowledge", "Conocimiento"), WordPair("Success", "Exito"), WordPair("Challenge", "Desafio"), WordPair("Development", "Desarrollo"), WordPair("Environment", "Medio ambiente"), WordPair("Relationship", "Relacion"), WordPair("Achievement", "Logro"), WordPair("Opportunity", "Oportunidad")),
        4 to listOf(WordPair("Ambitious", "Ambicioso"), WordPair("Perseverance", "Perseverancia"), WordPair("Overwhelmed", "Abrumado"), WordPair("Entrepreneur", "Emprendedor"), WordPair("Consciousness", "Conciencia"), WordPair("Nevertheless", "Sin embargo"), WordPair("Sophisticated", "Sofisticado"), WordPair("Accountability", "Responsabilidad")),
        5 to listOf(WordPair("Serendipity", "Serendipia"), WordPair("Ubiquitous", "Omnipresente"), WordPair("Resilience", "Resiliencia"), WordPair("Paradigm", "Paradigma"), WordPair("Ephemeral", "Efimero"), WordPair("Benevolent", "Benevolente"), WordPair("Quintessential", "Esencial"), WordPair("Unprecedented", "Sin precedentes"))
    )

    private val _uiState = MutableStateFlow(WordMatchUiState())
    val uiState = _uiState.asStateFlow()

    init { loadLevel() }

    private fun loadLevel() {
        viewModelScope.launch {
            val level = progressManager.getLevel("word_match").coerceIn(1, 5)
            startGame(level)
        }
    }

    fun startGame(level: Int = _uiState.value.level) {
        val words = levelWords[level.coerceIn(1, 5)] ?: levelWords[1]!!
        val pairsCount = (4 + level).coerceAtMost(8)
        val gamePairs = words.shuffled().take(pairsCount)
        _uiState.value = WordMatchUiState(
            wordsEnglish = gamePairs.map { it.english }.shuffled(),
            wordsSpanish = gamePairs.map { it.spanish }.shuffled(),
            level = level
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
        val eng = state.selectedEnglish ?: return
        val spa = state.selectedSpanish ?: return

        val level = state.level.coerceIn(1, 5)
        val allPairs = levelWords[level] ?: levelWords[1]!!
        val pair = allPairs.find { it.english == eng && it.spanish == spa }

        if (pair != null) {
            val newMatchedEng = state.matchedPairs + eng
            val newMatchedSpa = state.matchedSpanish + spa
            val xpPerMatch = 10 + (level * 3)
            val newScore = state.score + xpPerMatch
            _uiState.value = state.copy(
                matchedPairs = newMatchedEng,
                matchedSpanish = newMatchedSpa,
                selectedEnglish = null,
                selectedSpanish = null,
                score = newScore,
                message = "+$xpPerMatch XP"
            )
            if (newMatchedEng.size == state.wordsEnglish.size) {
                completeGame(newScore)
            }
        } else {
            viewModelScope.launch {
                _uiState.value = state.copy(message = "Incorrecto")
                delay(700)
                _uiState.value = _uiState.value.copy(selectedEnglish = null, selectedSpanish = null, message = null)
            }
        }
    }

    private fun completeGame(finalScore: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingProgress = true)
            progressManager.recordGameCompleted("word_match", finalScore)
            gamificationRepository.postProgress(lessonId = 1, score = finalScore)
            _uiState.value = _uiState.value.copy(isGameOver = true, isSavingProgress = false, message = "Nivel ${_uiState.value.level} completado")
        }
    }
}