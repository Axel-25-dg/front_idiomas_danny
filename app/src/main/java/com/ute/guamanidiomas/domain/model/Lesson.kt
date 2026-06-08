package com.ute.guamanidiomas.domain.model

data class Lesson(
    val id: Int = 0,
    val moduleId: Int = 0,
    val moduleTitle: String? = null,
    val title: String? = null,
    val content: String? = null,
    val contentType: String? = "",
    val order: Int = 0,
    val xpReward: Int = 0,
    val isActive: Boolean = true
) {
    val isExam: Boolean get() = contentType == "interactive"
}

data class LessonPayload(
    val moduleId: Int,
    val title: String,
    val content: String,
    val contentType: String = "text",
    val order: Int,
    val xpReward: Int,
    val isActive: Boolean
)