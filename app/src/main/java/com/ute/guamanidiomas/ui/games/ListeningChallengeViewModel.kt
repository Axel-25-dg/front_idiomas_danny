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

data class ListeningQuestion(
    val phrase: String,
    val correctAnswer: String,
    val options: List<String>
)

data class ListeningUiState(
    val questions: List<ListeningQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val correctCount: Int = 0,
    val isCompleted: Boolean = false
)

@HiltViewModel
class ListeningChallengeViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListeningUiState())
    val uiState = _uiState.asStateFlow()

    private val allQuestions = listOf(
        ListeningQuestion("Good morning, how are you?", "Buenos dias, como estas?", listOf("Buenos dias, como estas?", "Buenas noches", "Hasta luego", "Gracias")),
        ListeningQuestion("I would like a glass of water", "Me gustaria un vaso de agua", listOf("Quiero comida", "Me gustaria un vaso de agua", "Necesito dormir", "Voy al parque")),
        ListeningQuestion("Where is the nearest hospital?", "Donde esta el hospital mas cercano?", listOf("Donde esta la tienda?", "Como te llamas?", "Donde esta el hospital mas cercano?", "Que hora es?")),
        ListeningQuestion("My name is John and I am a teacher", "Mi nombre es John y soy profesor", listOf("Soy estudiante", "Mi nombre es John y soy profesor", "El es doctor", "Ella trabaja aqui")),
        ListeningQuestion("Can you help me, please?", "Puedes ayudarme, por favor?", listOf("Donde vives?", "Cuanto cuesta?", "Puedes ayudarme, por favor?", "Que dia es hoy?")),
        ListeningQuestion("I need to go to the airport", "Necesito ir al aeropuerto", listOf("Voy a la escuela", "Necesito ir al aeropuerto", "Quiero ir al cine", "Estoy en casa")),
        ListeningQuestion("The weather is very cold today", "El clima esta muy frio hoy", listOf("Hace calor", "Esta lloviendo", "El clima esta muy frio hoy", "Es de noche")),
        ListeningQuestion("She speaks three languages fluently", "Ella habla tres idiomas con fluidez", listOf("El estudia mucho", "Ella habla tres idiomas con fluidez", "Nosotros cantamos", "Ellos viajan"))
    )

    init { startGame() }

    fun startGame() {
        val selected = allQuestions.shuffled().take(6)
        _uiState.value = ListeningUiState(questions = selected)
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
                score          = if (isCorrect) it.score + 20 else it.score,
                correctCount   = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
        viewModelScope.launch {
            delay(1200)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val st = _uiState.value
        if (st.currentIndex + 1 >= st.questions.size) {
            viewModelScope.launch {
                gamificationRepository.postProgress(lessonId = 7, score = st.score)
            }
            _uiState.update { it.copy(isCompleted = true) }
        } else {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1, selectedOption = null, isAnswered = false) }
        }
    }
}
