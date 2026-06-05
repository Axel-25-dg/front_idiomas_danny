package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.domain.model.Payment
import com.ute.guamanidiomas.domain.model.SubscriptionPlan
import com.ute.guamanidiomas.domain.model.UserSubscription
import com.ute.guamanidiomas.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionUiState(
    val isLoading: Boolean = false,
    val plans: List<SubscriptionPlan> = emptyList(),
    val activeSubscription: UserSubscription? = null,
    val paymentHistory: List<Payment> = emptyList(),
    val isPremium: Boolean = false,
    val error: String? = null,
    val subscribeResult: SubscribeResult? = null
)

sealed interface SubscribeResult {
    data class Success(val planName: String) : SubscribeResult
    data class Error(val message: String) : SubscribeResult
}

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionUiState())
    val state: StateFlow<SubscriptionUiState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val plansDeferred    = async { subscriptionRepository.getPlans() }
            val mySubsDeferred   = async { subscriptionRepository.getMySubscriptions() }
            val paymentsDeferred = async { subscriptionRepository.getPaymentHistory() }

            val plansResult    = plansDeferred.await()
            val mySubsResult   = mySubsDeferred.await()
            val paymentsResult = paymentsDeferred.await()

            val activeSub = mySubsResult.getOrNull()
                ?.firstOrNull { it.status == "active" }

            _state.update { current ->
                current.copy(
                    isLoading          = false,
                    plans              = plansResult.getOrNull() ?: emptyList(),
                    activeSubscription = activeSub,
                    isPremium          = activeSub?.isPremium == true,
                    paymentHistory     = paymentsResult.getOrNull() ?: emptyList(),
                    error              = plansResult.exceptionOrNull()?.message
                        ?: mySubsResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun subscribe(planId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            subscriptionRepository.subscribe(planId)
                .onSuccess { newSub ->
                    loadDashboard()
                    _state.update {
                        it.copy(subscribeResult = SubscribeResult.Success(newSub.planName))
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading       = false,
                            subscribeResult = SubscribeResult.Error(
                                e.message ?: "No se pudo procesar la suscripción"
                            )
                        )
                    }
                }
        }
    }

    fun clearSubscribeResult() {
        _state.update { it.copy(subscribeResult = null) }
    }
}
