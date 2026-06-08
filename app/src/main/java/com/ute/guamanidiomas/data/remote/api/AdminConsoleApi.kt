package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.RoleDto
import com.ute.guamanidiomas.data.remote.dto.RoleRequest
import com.ute.guamanidiomas.data.remote.dto.StaffPaginationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminConsoleApi {
    @GET("dashboard/admin/")
    suspend fun getAdminDashboard(): Response<AdminDashboardDto>

    @GET("roles/")
    suspend fun getRoles(): Response<StaffPaginationResponse<RoleDto>>

    @POST("roles/")
    suspend fun createRole(@Body request: RoleRequest): Response<RoleDto>

    @GET("audit-logs/")
    suspend fun getAuditLogs(): Response<StaffPaginationResponse<AuditLogDto>>
}

data class AdminDashboardDto(
    val users: Int = 0,
    val teachers: Int = 0,
    val students: Int = 0,
    val courses: Int = 0,
    val classrooms: Int = 0,
    val subscriptions: Int = 0,
    val payments: Int = 0,
    val certificates: Int = 0
)
