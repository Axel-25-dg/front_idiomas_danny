package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.DjangoPage
import com.ute.guamanidiomas.data.remote.dto.PaymentDto
import com.ute.guamanidiomas.data.remote.dto.SubscribeRequestDto
import com.ute.guamanidiomas.data.remote.dto.SubscriptionPlanDto
import com.ute.guamanidiomas.data.remote.dto.UserSubscriptionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SubscriptionApi {

    @GET("subscriptions/")
    suspend fun getPlans(): Response<List<SubscriptionPlanDto>>

    @GET("my-subscriptions/")
    suspend fun getMySubscriptions(): Response<List<UserSubscriptionDto>>

    @POST("subscriptions/")
    suspend fun subscribe(
        @Body request: SubscribeRequestDto
    ): Response<UserSubscriptionDto>

    @GET("payments/")
    suspend fun getPaymentHistory(
        @Query("page") page: Int = 1
    ): Response<DjangoPage<PaymentDto>>
}
