package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.data.remote.api.AdminDashboardDto
import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.data.remote.dto.RoleDto

interface AdminConsoleRepository {
    suspend fun getAdminDashboard(): Result<AdminDashboardDto>
    suspend fun getRoles(): Result<List<RoleDto>>
    suspend fun createRole(name: String, permissions: List<String>): Result<RoleDto>
    suspend fun assignRoleToUser(userId: Int, role: String): Result<Unit>
    suspend fun getOrders(): Result<List<OrderDto>>
    suspend fun approveOrder(orderId: Int): Result<OrderDto>
    suspend fun getAuditLogs(): Result<List<AuditLogDto>>
}

