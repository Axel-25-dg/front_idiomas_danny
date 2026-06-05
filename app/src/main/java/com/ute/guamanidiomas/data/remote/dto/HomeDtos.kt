package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HomeStatsDto(
    val id: Int,
    val user: Int,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int
)

data class AchievementDto(
    val id: Int,
    val title: String? = null,
    val description: String? = null,
    val unlocked: Boolean? = null
)

data class ProgressResultDto(
    val id: Int,
    val user: Int,
    @SerializedName("user_email") val userEmail: String,
    val lesson: Int,
    @SerializedName("lesson_title") val lessonTitle: String,
    val status: String,
    val score: Int,
    @SerializedName("completed_at") val completedAt: String?
)

data class ProgressResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ProgressResultDto> = emptyList()
)
