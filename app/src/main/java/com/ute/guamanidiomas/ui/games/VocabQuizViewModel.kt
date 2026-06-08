package com.ute.guamanidiomas.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestion(
    val word: String,
    val correctAnswer: String,
    val options: List<String>
)

data class VocabQuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val correctCount: Int = 0,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val timeLeft: Int = 10,
    val isTimerRunning: Boolean = false
)

@HiltViewModel
class VocabQuizViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val progressManager: com.ute.guamanidiomas.data.local.GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabQuizUiState())
    val uiState = _uiState.asStateFlow()

    private val allWords = listOf(
        "Apple" to "Manzana", "House" to "Casa", "Book" to "Libro",
        "Water" to "Agua", "School" to "Escuela", "Teacher" to "Profesor",
        "Friend" to "Amigo", "Family" to "Familia", "Computer" to "Computadora",
        "Phone" to "Teléfono", "Music" to "Música", "Movie" to "Película",
        "Happy" to "Feliz", "Beautiful" to "Hermoso", "Run" to "Correr",
        "Sleep" to "Dormir", "Eat" to "Comer", "Drink" to "Beber",
        "Work" to "Trabajar", "Study" to "Estudiar", "Love" to "Amor",
        "City" to "Ciudad", "Night" to "Noche", "Day" to "Día",
        "Morning" to "Mañana", "Sun" to "Sol", "Moon" to "Luna",
        "Dog" to "Perro", "Cat" to "Gato", "Bird" to "Pájaro"
    )

    init { buildQuiz() }

    fun buildQuiz() {
        val shuffled = allWords.shuffled().take(10)
        val questions = shuffled.map { (word, correct) ->
            val wrongs = allWords
                .filter { it.second != correct }
                .shuffled()
                .take(3)
                .map { it.second }
            QuizQuestion(
                word          = word,
                correctAnswer = correct,
                options       = (wrongs + correct).shuffled()
            )
        }
        _uiState.value = VocabQuizUiState(questions = questions, isTimerRunning = true)
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val st = _uiState.value
                if (st.isCompleted || !st.isTimerRunning) break
                if (st.isAnswered) continue
                val newTime = st.timeLeft - 1
                if (newTime <= 0) {
                    _uiState.update { it.copy(timeLeft = 10, isAnswered = true) }
                    delay(800)
                    nextQuestion()
                } else {
                    _uiState.update { it.copy(timeLeft = newTime) }
                }
            }
        }
    }

    fun answer(option: String) {
        val st = _uiState.value
        if (st.isAnswered || st.isCompleted) return
        val current = st.questions.getOrNull(st.currentIndex) ?: return
        val isCorrect = option == current.correctAnswer
        val newScore  = if (isCorrect) st.score + 10 + (st.timeLeft * 2) else st.score
        _uiState.update {
            it.copy(
                selectedOption = option,
                isAnswered     = true,
                score          = newScore,
                correctCount   = if (isCorrect) it.correctCount + 1 else it.correctCount,
                isTimerRunning = false
            )
        }
        viewModelScope.launch {
            delay(1000)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val st = _uiState.value
        val nextIdx = st.currentIndex + 1
        if (nextIdx >= st.questions.size) {
            finishQuiz(st.score)
        } else {
            _uiState.update {
                it.copy(
                    currentIndex   = nextIdx,
                    selectedOption = null,
                    isAnswered     = false,
                    timeLeft       = 10,
                    isTimerRunning = true
                )
            }
            startTimer()
        }
    }

    private fun finishQuiz(finalScore: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCompleted = true, isLoading = true, isTimerRunning = false) }
            progressManager.recordGameCompleted("vocab_quiz", finalScore)
            gamificationRepository.postProgress(lessonId = 4, score = finalScore)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun restart() = buildQuiz()
}