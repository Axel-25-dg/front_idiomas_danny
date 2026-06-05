package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password2") val password2: String
)

data class LogoutRequest(
    val refresh: String
)

data class LoginResponse(
    val access: String,
    val refresh: String
)

data class RegisterResponse(
    val message: String? = null,
    val user: RegisteredUserDto? = null
)

data class RegisteredUserDto(
    val id: Int,
    val username: String,
    val email: String,
    val role: String? = null,
    val profile: ProfileDto? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ProfileDto(
    val id: Int? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("native_language") val nativeLanguage: String? = null,
    val timezone: String? = null
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("is_staff") val isStaff: Boolean = false,
    val role: RoleDto? = null
)

data class StaffPaginationResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

data class RoleDto(
    val id: Int,
    val name: String,
    val permissions: List<String> = emptyList()
)

data class UserCreateRequest(
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_staff") val isStaff: Boolean = true,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("role_id") val roleId: Int? = null,
    val role: String? = null,
    val password: String
)

data class UserUpdateRequest(
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("role_id") val roleId: Int? = null,
    val role: String? = null
)

fun UserDto.toDomain() = com.ute.guamanidiomas.domain.model.User(
    id = id,
    username = username,
    email = email,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    role = role?.name?.lowercase() ?: "user",
    isActive = isActive,
    isStaff = isStaff
)
