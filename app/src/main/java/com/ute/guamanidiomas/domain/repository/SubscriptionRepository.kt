package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.Payment
import com.ute.guamanidiomas.domain.model.SubscriptionPlan
import com.ute.guamanidiomas.domain.model.UserSubscription

interface SubscriptionRepository {
    suspend fun getPlans(): Result<List<SubscriptionPlan>>
    suspend fun getMySubscriptions(): Result<List<UserSubscription>>
    suspend fun subscribe(planId: Int): Result<UserSubscription>
    suspend fun getPaymentHistory(): Result<List<Payment>>
}