package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Order
import com.ute.guamanidiomas.domain.model.OrderItem
import com.ute.guamanidiomas.domain.model.OrderStatus

data class OrderResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<OrderDto>
)

data class OrderDto(
    val id: Int,
    @SerializedName("user") val userId: Int? = null,
    @SerializedName("user_email") val userEmail: String? = null,
    val items: List<OrderItemDto> = emptyList(),
    val total: Double? = 0.0,
    @SerializedName("total_price") val totalPrice: Double? = null,
    val tax: Double? = 0.0,
    val status: String? = null,
    @SerializedName("course_title") val courseTitle: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class OrderItemDto(
    val id: Int,
    @SerializedName("course") val courseId: Int,
    @SerializedName("course_title") val courseTitle: String,
    val price: Double,
    val quantity: Int
)

data class AddItemRequestDto(
    @SerializedName("course_id") val courseId: Int,
    val quantity: Int
)

data class UpdateStatusRequestDto(
    val status: String
)

data class OrderStatsDto(
    @SerializedName("total_orders") val totalOrders: Int,
    @SerializedName("total_revenue") val totalRevenue: Double,
    @SerializedName("by_status") val byStatus: Map<String, Int>
)

fun OrderDto.toDomain() = Order(
    id = id,
    userId = userId ?: 0,
    items = items.map { it.toDomain() },
    total = totalPrice ?: total ?: 0.0,
    tax = tax ?: 0.0,
    status = OrderStatus.fromValue(status ?: ""),
    createdAt = createdAt
)

fun OrderItemDto.toDomain() = OrderItem(
    id = id,
    courseId = courseId,
    courseTitle = courseTitle,
    price = price,
    quantity = quantity
)
