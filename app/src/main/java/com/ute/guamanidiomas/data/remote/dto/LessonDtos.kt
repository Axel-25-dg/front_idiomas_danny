package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Lesson

data class LessonDto(
    val id: Int = 0,
    @SerializedName("module") val moduleId: Int = 0,
    @SerializedName("module_title") val moduleTitle: String? = null,
    val title: String? = null,
    val content: String? = null,
    @SerializedName("content_type") val contentType: String? = null,
    val order: Int = 0,
    @SerializedName("xp_reward") val xpReward: Int = 10,
    @SerializedName("is_active") val isActive: Boolean = true
) {
    fun toDomain() = Lesson(
        id          = id,
        moduleId    = moduleId,
        moduleTitle = moduleTitle.orEmpty(),
        title       = title.orEmpty(),
        content     = content.orEmpty(),
        contentType = contentType.orEmpty(),
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
