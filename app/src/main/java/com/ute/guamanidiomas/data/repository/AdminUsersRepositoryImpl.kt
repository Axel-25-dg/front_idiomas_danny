package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.AdminUsersApi
import com.ute.guamanidiomas.data.remote.dto.UserCreateRequest
import com.ute.guamanidiomas.data.remote.dto.UserUpdateRequest
import com.ute.guamanidiomas.data.remote.dto.toDomain
import com.ute.guamanidiomas.domain.model.User
import com.ute.guamanidiomas.domain.repository.AdminUsersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminUsersRepositoryImpl @Inject constructor(
    private val api: AdminUsersApi
) : AdminUsersRepository {

    override suspend fun getUsers(): Result<List<User>> = runCatching {
        val response = api.getUsers()
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            error(adminUsersError(response.code(), response.message()))
        }
    }

    override suspend fun createUser(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        role: String,
        passwordProvisional: String
    ): Result<User> = runCatching {
        // Mapear nombre de rol a role_id
        val roleId = when (role.lowercase()) {
            "admin", "administrador" -> 1
            "teacher", "profesor"    -> 2
            "student", "estudiante"  -> 3
            else -> 3 // default student
        }
        val request = UserCreateRequest(
            username  = username,
            email     = email,
            firstName = firstName,
            lastName  = lastName,
            isActive  = true,
            roleId    = roleId,
            password  = passwordProvisional
        )
        val response = api.createUser(request)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Cuerpo de respuesta vacío")
        } else {
            error(adminUsersError(response.code(), response.errorBody()?.string() ?: response.message()))
        }
    }

    override suspend fun updateUser(
        id: Int,
        isActive: Boolean?,
        role: String?
    ): Result<User> = runCatching {
        // Mapear nombre de rol a role_id si se envía
        val roleId = when (role?.lowercase()) {
            "admin", "administrador" -> 1
            "teacher", "profesor"    -> 2
            "student", "estudiante"  -> 3
            null -> null
            else -> null
        }
        val request = UserUpdateRequest(
            isActive = isActive,
            roleId   = roleId
        )
        val response = api.updateUser(id, request)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Cuerpo de respuesta vacío")
        } else {
            error(adminUsersError(response.code(), response.errorBody()?.string() ?: response.message()))
        }
    }

    private fun adminUsersError(code: Int, detail: String): String {
        return if (code == 404) {
            "El backend no tiene registrado el endpoint /api/users/ para Gestion de Personal."
        } else {
            "Error $code: $detail"
        }
    }
}
