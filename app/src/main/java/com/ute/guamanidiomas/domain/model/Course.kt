package com.ute.guamanidiomas.domain.model

import com.google.gson.annotations.SerializedName

data class Course(
    val id: Int = 0,
    @SerializedName("language") val languageId: Int? = null,
    @SerializedName("language_name") val languageName: String? = null,
    val title: String? = null,
    val description: String? = null,
    // El backend puede enviar "level" o "difficulty_level"
    val level: String? = null,
    @SerializedName("difficulty_level") val difficultyLevel: String? = null,
    val price: Double = 0.0,
    val stock: Int = 1,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null
) {
    /** Nivel real — usa difficulty_level del backend o level como fallback */
    val displayLevel: String get() = difficultyLevel ?: level ?: ""
}

data class CoursePayload(
    val languageId: Int,
    val title: String,
    val description: String,
    val level: String,
    val price: Double,
    val isActive: Boolean
)
