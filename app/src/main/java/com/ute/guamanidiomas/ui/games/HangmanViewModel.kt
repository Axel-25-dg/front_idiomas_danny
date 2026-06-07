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

data class HangmanUiState(
    val word: String = "",
    val hint: String = "",
    val guessedLetters: Set<Char> = emptySet(),
    val wrongGuesses: Int = 0,
    val maxWrong: Int = 6,
    val isWon: Boolean = false,
    val isLost: Boolean = false,
    val score: Int = 0,
    val round: Int = 1,
    val totalRounds: Int = 5,
    val isLoading: Boolean = false
) {
    val displayWord: String
        get() = word.map { c -> if (c == ' ' || guessedLetters.contains(c.uppercaseChar())) c else '_' }
            .joinToString(" ")
    val remainingLives: Int get() = maxWrong - wrongGuesses
    val isGameOver: Boolean get() = isWon || isLost
}

@HiltViewModel
class HangmanViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val progressManager: com.ute.guamanidiomas.data.local.GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HangmanUiState())
    val uiState = _uiState.asStateFlow()

    private val wordBank = listOf(
        "APPLE" to "Fruta roja o verde", "COMPUTER" to "Dispositivo electrónico",
        "SCHOOL" to "Lugar de educación", "TEACHER" to "Persona que enseña",
        "ELEPHANT" to "Animal grande con trompa", "MUSIC" to "Arte del sonido",
        "LIBRARY" to "Lugar con libros", "FOOTBALL" to "Deporte popular",
        "BUTTERFLY" to "Insecto de colores", "MOUNTAIN" to "Elevación natural",
        "KITCHEN" to "Habitación para cocinar", "RAINBOW" to "Fenómeno de colores",
        "PENCIL" to "Instrumento para escribir", "WINDOW" to "Abertura en la pared",
        "BIRTHDAY" to "Día de nacimiento", "LANGUAGE" to "Sistema de comunicación",
        "SCIENCE" to "Estudio de la naturaleza", "HOSPITAL" to "Centro médico",
        "CHAMPION" to "El mejor en algo", "INTERNET" to "Red global de computadoras"
    )

    private var roundIndex = 0
    private var shuffledWords = wordBank.shuffled()

    init { loadRound() }

    private fun loadRound() {
        val (word, hint) = shuffledWords[roundIndex % shuffledWords.size]
        _uiState.update { current ->
            current.copy(
                word          = word,
                hint          = hint,
                guessedLetters = emptySet(),
                wrongGuesses  = 0,
                isWon         = false,
                isLost        = false
            )
        }
    }

    fun guess(letter: Char) {
        val st = _uiState.value
        if (st.isGameOver || letter in st.guessedLetters) return
        val upper = letter.uppercaseChar()
        val newGuessed = st.guessedLetters + upper
        val isWrong = upper !in st.word
        val newWrong = if (isWrong) st.wrongGuesses + 1 else st.wrongGuesses
        val allRevealed = st.word.all { c -> c == ' ' || c in newGuessed }
        val isWon = allRevealed
        val isLost = newWrong >= st.maxWrong

        val newScore = when {
            isWon  -> st.score + 20 + (st.remainingLives * 5)
            isLost -> st.score
            !isWrong -> st.score + 5
            else   -> st.score
        }

        _uiState.update {
            it.copy(
                guessedLetters = newGuessed,
                wrongGuesses   = newWrong,
                isWon          = isWon,
                isLost         = isLost,
                score          = newScore
            )
        }

        if (isWon || isLost) {
            val isLastRound = st.round >= st.totalRounds
            if (isLastRound) {
                viewModelScope.launch {
                    progressManager.recordGameCompleted("hangman", newScore)
                    gamificationRepository.postProgress(lessonId = 5, score = newScore)
                }
            }
        }
    }

    fun nextRound() {
        val st = _uiState.value
        if (st.round >= st.totalRounds) return
        roundIndex++
        _uiState.update { it.copy(round = it.round + 1) }
        loadRound()
    }

    fun restart() {
        shuffledWords = wordBank.shuffled()
        roundIndex = 0
        _uiState.value = HangmanUiState()
        loadRound()
    }
}
