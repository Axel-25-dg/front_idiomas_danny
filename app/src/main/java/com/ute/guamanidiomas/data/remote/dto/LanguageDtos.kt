package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Language

data class LanguageDto(
    val id: Int,
    val name: String,
    val code: String,
    val description: String = "",
    @SerializedName("flag_url") val flagUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("total_courses") val totalCourses: Int = 0,
    @SerializedName("created_at") val createdAt: String = ""
) {
    fun toDomain() = Language(
        id           = id,
        name         = name,
        code         = code,
        description  = description,
        isActive     = isActive,
        totalCourses = totalCourses,
        createdAt    = createdAt
    )
}
