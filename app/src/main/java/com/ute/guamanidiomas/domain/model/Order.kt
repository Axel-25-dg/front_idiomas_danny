package com.ute.guamanidiomas.domain.model

enum class OrderStatus(val value: String) {
    PENDING("pending"),
    PAID("paid"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    companion object {
        fun fromValue(value: String): OrderStatus = 
            entries.find { it.value == value } ?: PENDING
    }
}

data class Order(
    val id: Int,
    val userId: Int,
    val items: List<OrderItem>,
    val total: Double,
    val tax: Double,
    val status: OrderStatus,
    val createdAt: String? = null
)

data class OrderItem(
    val id: Int,
    val courseId: Int,
    val courseTitle: String,
    val price: Double,
    val quantity: Int
)
