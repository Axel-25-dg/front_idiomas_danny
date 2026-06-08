package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.CoursePayload

data class CourseResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Course>
)

data class CourseRequest(
    @SerializedName("language") val languageId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("level") val level: String,
    @SerializedName("price") val price: Double,
    @SerializedName("is_active") val isActive: Boolean
)

fun CoursePayload.toRequest() = CourseRequest(
    languageId = languageId,
    title = title,
    description = description,
    level = level,
    price = price,
    isActive = isActive
)