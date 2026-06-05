package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.SubscriptionApi
import com.ute.guamanidiomas.data.remote.dto.SubscribeRequestDto
import com.ute.guamanidiomas.domain.model.Payment
import com.ute.guamanidiomas.domain.model.SubscriptionPlan
import com.ute.guamanidiomas.domain.model.UserSubscription
import com.ute.guamanidiomas.domain.repository.SubscriptionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val api: SubscriptionApi
) : SubscriptionRepository {

    override suspend fun getPlans(): Result<List<SubscriptionPlan>> = runCatching {
        val response = api.getPlans()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception("Error ${response.code()}: ${response.message()}")
    }

    override suspend fun getMySubscriptions(): Result<List<UserSubscription>> = runCatching {
        val response = api.getMySubscriptions()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception("Error ${response.code()}: ${response.message()}")
    }

    override suspend fun subscribe(planId: Int): Result<UserSubscription> = runCatching {
        val response = api.subscribe(SubscribeRequestDto(planId))
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Respuesta vacía del servidor")
        } else {
            val errorBody = response.errorBody()?.string() ?: ""
            throw Exception("Error ${response.code()}: $errorBody")
        }
    }

    override suspend fun getPaymentHistory(): Result<List<Payment>> = runCatching {
        val response = api.getPaymentHistory()
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }
}
