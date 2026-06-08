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

data class VerbQuestion(
    val verb: String,
    val tense: String,
    val subject: String,
    val correctAnswer: String,
    val options: List<String>
)

data class VerbUiState(
    val questions: List<VerbQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val correctCount: Int = 0,
    val isCompleted: Boolean = false
)

@HiltViewModel
class VerbConjugationViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val progressManager: com.ute.guamanidiomas.data.local.GameProgressManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerbUiState())
    val uiState = _uiState.asStateFlow()

    private val allQuestions = listOf(
        VerbQuestion("to be", "Present", "I", "am", listOf("am", "is", "are", "was")),
        VerbQuestion("to be", "Present", "She", "is", listOf("am", "is", "are", "be")),
        VerbQuestion("to have", "Present", "They", "have", listOf("has", "have", "had", "having")),
        VerbQuestion("to go", "Past", "He", "went", listOf("go", "goes", "went", "gone")),
        VerbQuestion("to eat", "Past", "We", "ate", listOf("eat", "eats", "ate", "eaten")),
        VerbQuestion("to run", "Present Continuous", "I", "am running", listOf("run", "am running", "ran", "running")),
        VerbQuestion("to write", "Past", "She", "wrote", listOf("write", "writes", "wrote", "written")),
        VerbQuestion("to speak", "Present", "He", "speaks", listOf("speak", "speaks", "spoke", "spoken")),
        VerbQuestion("to read", "Past", "They", "read", listOf("read", "reads", "readed", "reading")),
        VerbQuestion("to come", "Future", "We", "will come", listOf("came", "come", "will come", "coming")),
        VerbQuestion("to play", "Present", "She", "plays", listOf("play", "plays", "played", "playing")),
        VerbQuestion("to study", "Past", "I", "studied", listOf("study", "studies", "studied", "studying"))
    )

    init { startGame() }

    fun startGame() {
        val selected = allQuestions.shuffled().take(8)
        _uiState.value = VerbUiState(questions = selected)
    }

    fun answer(option: String) {
        val st = _uiState.value
        if (st.isAnswered) return
        val current = st.questions[st.currentIndex]
        val isCorrect = option == current.correctAnswer
        _uiState.update {
            it.copy(
                selectedOption = option,
                isAnswered     = true,
                score          = if (isCorrect) it.score + 18 else it.score,
                correctCount   = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
        viewModelScope.launch {
            delay(1000)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val st = _uiState.value
        if (st.currentIndex + 1 >= st.questions.size) {
            viewModelScope.launch {
                progressManager.recordGameCompleted("verb_conjugation", st.score)
                gamificationRepository.postProgress(lessonId = 8, score = st.score)
            }
            _uiState.update { it.copy(isCompleted = true) }
        } else {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1, selectedOption = null, isAnswered = false) }
        }
    }
}