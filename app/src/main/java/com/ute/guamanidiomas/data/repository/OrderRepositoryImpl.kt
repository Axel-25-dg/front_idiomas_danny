package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.OrderApi
import com.ute.guamanidiomas.data.remote.dto.*
import com.ute.guamanidiomas.domain.model.Order
import com.ute.guamanidiomas.domain.model.OrderStatus
import com.ute.guamanidiomas.domain.repository.OrderRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val api: OrderApi,
) : OrderRepository {

    override suspend fun getOrders(page: Int?, status: String?): Result<Pair<List<Order>, Int>> = 
        runCatching {
            try {
                val response = api.getOrders(page = page, status = status)
                if (response.isSuccessful) {
                    val body = response.body()!!
                    Pair(body.results.map { it.toDomain() }, body.count)
                } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            } catch (e: Exception) {
                throw Exception(ErrorUtils.parseError(e))
            }
        }

    override suspend fun getOrder(id: Int): Result<Order> = runCatching {
        try {
            val response = api.getOrder(id)
            if (response.isSuccessful) response.body()!!.toDomain()
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun createOrder(): Result<Order> = runCatching {
        try {
            val response = api.createOrder()
            if (response.isSuccessful) response.body()!!.toDomain()
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun addItem(orderId: Int, courseId: Int, quantity: Int): Result<Order> = 
        runCatching {
            try {
                val response = api.addItem(orderId, AddItemRequestDto(courseId, quantity))
                if (response.isSuccessful) response.body()!!.toDomain()
                else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            } catch (e: Exception) {
                throw Exception(ErrorUtils.parseError(e))
            }
        }

    override suspend fun confirmOrder(orderId: Int): Result<Order> = runCatching {
        try {
            val response = api.confirmOrder(orderId)
            if (response.isSuccessful) response.body()!!.toDomain()
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun updateStatus(orderId: Int, status: OrderStatus): Result<Order> = 
        runCatching {
            try {
                val response = api.updateStatus(orderId, UpdateStatusRequestDto(status.value))
                if (response.isSuccessful) response.body()!!.toDomain()
                else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            } catch (e: Exception) {
                throw Exception(ErrorUtils.parseError(e))
            }
        }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        try {
            val response = api.getStats()
            if (response.isSuccessful) {
                val s = response.body()!!
                mapOf(
                    "total_orders"  to s.totalOrders,
                    "total_revenue" to s.totalRevenue,
                    "by_status"     to s.byStatus,
                )
            } else if (response.code() == 404) {
                val ordersResponse = api.getOrders(page = 1, pageSize = 1000)
                if (ordersResponse.isSuccessful) {
                    val body = ordersResponse.body()!!
                    buildStatsFromOrders(body)
                } else {
                    throw Exception(ErrorUtils.parseErrorMessage(ordersResponse.errorBody()?.string(), ordersResponse.code()))
                }
            } else {
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    private fun buildStatsFromOrders(response: OrderResponseDto): Map<String, Any> {
        val orders = response.results
        val byStatus = orders
            .groupingBy { it.status }
            .eachCount()

        return mapOf(
            "total_orders" to response.count,
            "total_revenue" to orders.sumOf { it.total ?: 0.0 },
            "by_status" to byStatus,
        )
    }
}
