package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.LoginRequest
import com.ute.guamanidiomas.data.remote.dto.LoginResponse
import com.ute.guamanidiomas.data.remote.dto.LogoutRequest
import com.ute.guamanidiomas.data.remote.dto.RegisterRequest
import com.ute.guamanidiomas.data.remote.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/logout/")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>

    @POST("auth/token/refresh/")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<LoginResponse>
}