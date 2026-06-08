package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.User

interface AdminUsersRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun getAdminStudents(): Result<List<User>>
    suspend fun createUser(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        role: String,
        passwordProvisional: String
    ): Result<User>
    suspend fun updateUser(
        id: Int,
        isActive: Boolean? = null,
        role: String? = null
    ): Result<User>
}