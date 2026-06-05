package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.UserCreateRequest
import com.ute.guamanidiomas.data.remote.dto.UserDto
import com.ute.guamanidiomas.data.remote.dto.StaffPaginationResponse
import com.ute.guamanidiomas.data.remote.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface AdminUsersApi {
    @GET("users/")
    suspend fun getUsers(): Response<StaffPaginationResponse<UserDto>>

    @POST("users/")
    suspend fun createUser(
        @Body request: UserCreateRequest
    ): Response<UserDto>

    @PATCH("users/{id}/")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UserUpdateRequest
    ): Response<UserDto>
}
