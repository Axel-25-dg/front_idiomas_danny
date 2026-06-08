package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.AdminConsoleApi
import com.ute.guamanidiomas.data.remote.api.AdminDashboardDto
import com.ute.guamanidiomas.data.remote.api.AdminUsersApi
import com.ute.guamanidiomas.data.remote.api.OrderApi
import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.data.remote.dto.RoleDto
import com.ute.guamanidiomas.data.remote.dto.RoleRequest
import com.ute.guamanidiomas.data.remote.dto.UpdateStatusRequestDto
import com.ute.guamanidiomas.data.remote.dto.UserUpdateRequest
import com.ute.guamanidiomas.domain.repository.AdminConsoleRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminConsoleRepositoryImpl @Inject constructor(
    private val adminConsoleApi: AdminConsoleApi,
    private val adminUsersApi: AdminUsersApi,
    private val orderApi: OrderApi,
) : AdminConsoleRepository {

    override suspend fun getAdminDashboard(): Result<AdminDashboardDto> = runCatching {
        val response = adminConsoleApi.getAdminDashboard()
        if (response.isSuccessful) {
            response.body() ?: AdminDashboardDto()
        } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    override suspend fun getRoles(): Result<List<RoleDto>> = runCatching {
        val response = adminConsoleApi.getRoles()
        if (response.isSuccessful) response.body()?.results.orEmpty()
        else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    override suspend fun createRole(name: String, permissions: List<String>): Result<RoleDto> = runCatching {
        val response = adminConsoleApi.createRole(RoleRequest(name, permissions))
        if (response.isSuccessful) response.body()!!
        else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    override suspend fun assignRoleToUser(userId: Int, role: String): Result<Unit> = runCatching {
        val roleId = when (role.lowercase()) {
            "admin", "administrador" -> 1
            "teacher", "profesor"    -> 2
            "student", "estudiante"  -> 3
            else -> 3
        }
        val response = adminUsersApi.updateUser(userId, UserUpdateRequest(roleId = roleId))
        if (!response.isSuccessful) {
            throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        }
    }

    override suspend fun getOrders(): Result<List<OrderDto>> = runCatching {
        val response = orderApi.getOrders(page = 1, pageSize = 100)
        if (response.isSuccessful) response.body()?.results.orEmpty()
        else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    override suspend fun approveOrder(orderId: Int): Result<OrderDto> = runCatching {
        val response = orderApi.patchOrderStatus(orderId, UpdateStatusRequestDto("COMPLETADO"))
        if (response.isSuccessful) response.body()!!
        else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    override suspend fun getAuditLogs(): Result<List<AuditLogDto>> = runCatching {
        val response = adminConsoleApi.getAuditLogs()
        if (response.isSuccessful) response.body()?.results.orEmpty()
        else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
    }
}
