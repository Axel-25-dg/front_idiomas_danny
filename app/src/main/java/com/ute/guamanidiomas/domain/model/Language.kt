package com.ute.guamanidiomas.domain.model

data class Language(
    val id: Int,
    val name: String,
    val code: String, // ej: "EN", "FR"
    val description: String,
    val isActive: Boolean,
    val totalCourses: Int,
    val createdAt: String
)

data class LanguagePayload(
    val name: String,
    val code: String,
    val description: String,
    val isActive: Boolean
)
