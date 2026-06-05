package com.ute.guamanidiomas.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String? = null,
    val isActive: Boolean,
    val isStaff: Boolean
)
