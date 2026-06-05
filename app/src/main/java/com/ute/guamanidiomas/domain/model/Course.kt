package com.ute.guamanidiomas.domain.model

import com.google.gson.annotations.SerializedName

data class Course(
    val id: Int,
    @SerializedName("language") val languageId: Int?,
    @SerializedName("language_name") val languageName: String? = null,
    val title: String? = null,
    val description: String? = null,
    val level: String? = null, // ej: "A1", "B2"
    val price: Double = 0.0,
    val stock: Int = 1,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null
)

data class CoursePayload(
    val languageId: Int,
    val title: String,
    val description: String,
    val level: String,
    val price: Double,
    val isActive: Boolean
)

