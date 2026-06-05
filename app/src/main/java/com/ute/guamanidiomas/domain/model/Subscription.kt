package com.ute.guamanidiomas.domain.model

data class SubscriptionPlan(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val durationDays: Int,
    val isActive: Boolean,
    val features: List<String>
)

data class UserSubscription(
    val id: Int,
    val userId: Int,
    val userEmail: String,
    val planId: Int,
    val planName: String,
    val price: Double,
    val status: String,
    val startDate: String,
    val endDate: String,
    val isPremium: Boolean
)

data class Payment(
    val id: Int,
    val userId: Int,
    val subscriptionId: Int?,
    val amount: Double,
    val method: String,
    val status: String,
    val createdAt: String,
    val reference: String
)

data class SubscriptionPayload(
    val planId: Int
)
