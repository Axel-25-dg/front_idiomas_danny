package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── Dashboard Student ────────────────────────────────────────────────────────

data class StudentDashboardDto(
    @SerializedName("total_xp") val totalXp: Int = 0,
    val level: Int = 1,
    @SerializedName("xp_progress") val xpProgress: Int = 0,
    @SerializedName("xp_for_next_level") val xpForNextLevel: Int = 100,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("longest_streak") val longestStreak: Int = 0,
    @SerializedName("completed_lessons") val completedLessons: Int = 0,
    @SerializedName("total_lessons") val totalLessons: Int = 0,
    @SerializedName("progress_percentage") val progressPercentage: Int = 0,
    @SerializedName("certificates_count") val certificatesCount: Int = 0,
    @SerializedName("active_classrooms") val activeClassrooms: Int = 0,
    @SerializedName("achievements_count") val achievementsCount: Int = 0
)

// ─── Ranking ──────────────────────────────────────────────────────────────────

data class RankingEntryDto(
    val position: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    val username: String? = null,
    @SerializedName("total_xp") val totalXp: Int = 0,
    val level: Int = 1,
    @SerializedName("current_streak") val currentStreak: Int = 0
)

// ─── Legacy DTOs (kept for backward compatibility) ────────────────────────────

data class HomeStatsDto(
    val id: Int = 0,
    val user: Int = 0,
    @SerializedName("user_email") val userEmail: String? = null,
    @SerializedName("total_xp") val totalXp: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("longest_streak") val longestStreak: Int = 0,
    @SerializedName("last_active_date") val lastActiveDate: String? = null,
    @SerializedName("modules_completed") val modulesCompleted: Int = 0,
    val level: Int? = null
)

data class AchievementDto(
    val id: Int = 0,
    val name: String? = null,
    val title: String? = null,
    val description: String? = null,
    @SerializedName("icon_url") val iconUrl: String? = null,
    val unlocked: Boolean? = null,
    @SerializedName("required_xp") val requiredXp: Int = 0
)

data class ProgressResultDto(
    val id: Int = 0,
    val user: Int = 0,
    @SerializedName("user_email") val userEmail: String? = null,
    val lesson: Int = 0,
    @SerializedName("lesson_title") val lessonTitle: String? = null,
    val status: String? = null,
    val score: Int = 0,
    @SerializedName("completed_at") val completedAt: String? = null
)

data class ProgressResponseDto(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ProgressResultDto> = emptyList()
)
