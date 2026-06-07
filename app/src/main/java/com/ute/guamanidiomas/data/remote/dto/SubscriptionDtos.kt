package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Payment
import com.ute.guamanidiomas.domain.model.SubscriptionPlan
import com.ute.guamanidiomas.domain.model.UserSubscription

data class SubscriptionPlanDto(
    val id: Int,
    val name: String,
    val description: String = "",
    val price: Double,
    @SerializedName("duration_days") val durationDays: Int,
    @SerializedName("is_active") val isActive: Boolean = true,
    val features: List<String> = emptyList()
) {
    fun toDomain() = SubscriptionPlan(
        id           = id,
        name         = name,
        description  = description,
        price        = price,
        durationDays = durationDays,
        isActive     = isActive,
        features     = features
    )
}

data class UserSubscriptionDto(
    val id: Int,
    @SerializedName("user") val userId: Int,
    @SerializedName("user_email") val userEmail: String = "",
    val plan: SubscriptionPlanDto?,
    @SerializedName("plan_name") val planName: String? = null,
    val status: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("is_premium") val isPremium: Boolean = false
) {
    fun toDomain() = UserSubscription(
        id        = id,
        userId    = userId,
        userEmail = userEmail,
        planId    = plan?.id ?: 0,
        planName  = plan?.name ?: planName.orEmpty(),
        price     = plan?.price ?: 0.0,
        status    = status,
        startDate = startDate,
        endDate   = endDate,
        isPremium = isPremium || status == "active"
    )
}

data class PaymentDto(
    val id: Int,
    @SerializedName("user") val userId: Int,
    val subscription: Int? = null,
    val amount: Double,
    val method: String = "",
    val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("reference") val reference: String? = null
) {
    fun toDomain() = Payment(
        id             = id,
        userId         = userId,
        subscriptionId = subscription,
        amount         = amount,
        method         = method,
        status         = status,
        createdAt      = createdAt,
        reference      = reference.orEmpty()
    )
}

data class SubscribeRequestDto(
    @SerializedName("subscription") val planId: Int
)
