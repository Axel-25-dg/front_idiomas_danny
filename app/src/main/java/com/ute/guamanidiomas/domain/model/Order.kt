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
    val id: Int = 0,
    val userId: Int = 0,
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val tax: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: String? = null
)

data class OrderItem(
    val id: Int = 0,
    val courseId: Int = 0,
    val courseTitle: String? = null,
    val price: Double = 0.0,
    val quantity: Int = 0
)
