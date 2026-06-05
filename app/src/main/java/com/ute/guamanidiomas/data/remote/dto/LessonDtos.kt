package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Lesson

data class LessonDto(
    val id: Int,
    @SerializedName("module") val moduleId: Int,
    @SerializedName("module_title") val moduleTitle: String = "",
    val title: String,
    val content: String = "",
    val order: Int = 0,
    @SerializedName("xp_reward") val xpReward: Int = 10,
    @SerializedName("is_active") val isActive: Boolean = true
) {
    fun toDomain() = Lesson(
        id          = id,
        moduleId    = moduleId,
        moduleTitle = moduleTitle,
        title       = title,
        content     = content,
        order       = order,
        xpReward    = xpReward,
        isActive    = isActive
    )
}

data class LessonResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<LessonDto> = emptyList()
)
