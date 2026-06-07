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
    val id: Int = 0,
    val username: String? = null,
    val email: String? = null,
    val role: RoleResponseDto? = null,
    val profile: ProfileDto? = null,
    @SerializedName("is_staff") val isStaff: Boolean = false,
    @SerializedName("is_superuser") val isSuperuser: Boolean = false,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null
)

/** El backend devuelve role como objeto { id, name } */
data class RoleResponseDto(
    val id: Int = 0,
    val name: String? = null
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
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("role_id") val roleId: Int,
    val password: String
)

data class UserUpdateRequest(
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("role_id") val roleId: Int? = null
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
