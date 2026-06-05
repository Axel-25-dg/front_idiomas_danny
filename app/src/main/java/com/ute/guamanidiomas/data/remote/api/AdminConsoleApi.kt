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
    @GET("roles/")
    suspend fun getRoles(): Response<StaffPaginationResponse<RoleDto>>

    @POST("roles/")
    suspend fun createRole(@Body request: RoleRequest): Response<RoleDto>

    @GET("audit-logs/")
    suspend fun getAuditLogs(): Response<StaffPaginationResponse<AuditLogDto>>
}

