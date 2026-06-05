package com.ute.guamanidiomas.domain.model

data class Lesson(
    val id: Int,
    val moduleId: Int,
    val moduleTitle: String,
    val title: String,
    val content: String,
    val order: Int,
    val xpReward: Int,
    val isActive: Boolean
)

data class LessonPayload(
    val moduleId: Int,
    val title: String,
    val content: String,
    val order: Int,
    val xpReward: Int,
    val isActive: Boolean
)
