package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.AdminUsersApi
import com.ute.guamanidiomas.data.remote.dto.UserCreateRequest
import com.ute.guamanidiomas.data.remote.dto.UserUpdateRequest
import com.ute.guamanidiomas.data.remote.dto.toDomain
import com.ute.guamanidiomas.domain.model.User
import com.ute.guamanidiomas.domain.repository.AdminUsersRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminUsersRepositoryImpl @Inject constructor(
    private val api: AdminUsersApi
) : AdminUsersRepository {

    override suspend fun getUsers(): Result<List<User>> = runCatching {
        try {
            val response = api.getUsers(pageSize = 500)
            if (response.isSuccessful) {
                response.body()?.results?.map { it.toDomain() } ?: emptyList()
            } else {
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getAdminStudents(): Result<List<User>> = runCatching {
        try {
            val response = api.getAdminStudents(pageSize = 500)
            if (response.isSuccessful) {
                response.body()?.results?.map { it.toDomain() } ?: emptyList()
            } else {
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
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
        try {
            val roleId = when (role.lowercase()) {
                "admin", "administrador" -> 1
                "teacher", "profesor"    -> 2
                "student", "estudiante"  -> 3
                else -> 3
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
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun updateUser(
        id: Int,
        isActive: Boolean?,
        role: String?
    ): Result<User> = runCatching {
        try {
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
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }
}