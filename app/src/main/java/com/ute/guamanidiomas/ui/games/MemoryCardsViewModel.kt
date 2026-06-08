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

data class MemoryCard(
    val id: Int,
    val content: String,
    val pairId: Int,
    val isEnglish: Boolean,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

data class MemoryCardsUiState(
    val cards: List<MemoryCard> = emptyList(),
    val firstSelected: Int? = null,
    val secondSelected: Int? = null,
    val moves: Int = 0,
    val score: Int = 0,
    val isChecking: Boolean = false,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class MemoryCardsViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryCardsUiState())
    val uiState = _uiState.asStateFlow()

    private val pairs = listOf(
        "Dog" to "Perro", "Cat" to "Gato", "Sun" to "Sol",
        "Moon" to "Luna", "Star" to "Estrella", "Tree" to "Árbol",
        "Fire" to "Fuego", "Rain" to "Lluvia"
    )

    init { buildBoard() }

    fun buildBoard() {
        val gamePairs = pairs.shuffled().take(6)
        val cards = gamePairs.flatMapIndexed { index, (eng, spa) ->
            listOf(
                MemoryCard(id = index * 2,     content = eng, pairId = index, isEnglish = true),
                MemoryCard(id = index * 2 + 1, content = spa, pairId = index, isEnglish = false)
            )
        }.shuffled().mapIndexed { i, card -> card.copy(id = i) }

        _uiState.value = MemoryCardsUiState(cards = cards)
    }

    fun flipCard(cardId: Int) {
        val st = _uiState.value
        if (st.isChecking) return
        val card = st.cards.find { it.id == cardId } ?: return
        if (card.isFlipped || card.isMatched) return

        when {
            st.firstSelected == null -> {
                _uiState.update {
                    it.copy(
                        cards = it.cards.map { c -> if (c.id == cardId) c.copy(isFlipped = true) else c },
                        firstSelected = cardId
                    )
                }
            }
            st.secondSelected == null -> {
                val newCards = st.cards.map { c -> if (c.id == cardId) c.copy(isFlipped = true) else c }
                _uiState.update { it.copy(cards = newCards, secondSelected = cardId, isChecking = true, moves = it.moves + 1) }
                checkMatch(st.firstSelected, cardId, newCards)
            }
        }
    }

    private fun checkMatch(firstId: Int, secondId: Int, cards: List<MemoryCard>) {
        viewModelScope.launch {
            delay(800)
            val first  = cards.find { it.id == firstId }!!
            val second = cards.find { it.id == secondId }!!
            val isMatch = first.pairId == second.pairId

            val newScore = if (isMatch) _uiState.value.score + 20 else _uiState.value.score

            val updatedCards = cards.map { c ->
                when {
                    c.id == firstId || c.id == secondId -> {
                        if (isMatch) c.copy(isMatched = true, isFlipped = true)
                        else c.copy(isFlipped = false)
                    }
                    else -> c
                }
            }

            val allMatched = updatedCards.all { it.isMatched }

            _uiState.update {
                it.copy(
                    cards         = updatedCards,
                    firstSelected = null,
                    secondSelected = null,
                    isChecking    = false,
                    score         = newScore,
                    isCompleted   = allMatched
                )
            }

            if (allMatched) {
                gamificationRepository.postProgress(lessonId = 6, score = newScore)
            }
        }
    }

    fun restart() = buildBoard()
}