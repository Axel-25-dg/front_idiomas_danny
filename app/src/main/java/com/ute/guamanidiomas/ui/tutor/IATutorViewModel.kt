package com.ute.guamanidiomas.ui.tutor

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.remote.api.TutorApi
import com.ute.guamanidiomas.data.remote.api.TutorRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IATutorViewModel @Inject constructor(
    private val tutorApi: TutorApi
) : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        _messages.add(ChatMessage("¡Hola! Soy tu tutor IA de JumpUp UTE. ¿En qué puedo ayudarte hoy con tu inglés?", false))
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _messages.add(ChatMessage(text, true))
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = tutorApi.askTutor(TutorRequest(text))
                if (response.isSuccessful) {
                    _messages.add(ChatMessage(response.body()?.response ?: "No recibí una respuesta clara.", false))
                } else {
                    _messages.add(ChatMessage("Lo siento, hubo un error al conectar con mi cerebro artificial.", false))
                }
            } catch (e: Exception) {
                _messages.add(ChatMessage("Error de conexión: ${e.message}", false))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
