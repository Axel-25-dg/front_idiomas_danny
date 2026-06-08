package com.ute.guamanidiomas.domain.model

data class User(
    val id: Int = 0,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val isActive: Boolean = true,
    val isStaff: Boolean = false
)