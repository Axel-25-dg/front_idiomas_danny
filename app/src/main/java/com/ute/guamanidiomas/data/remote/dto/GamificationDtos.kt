package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Achievement
import com.ute.guamanidiomas.domain.model.LessonProgress
import com.ute.guamanidiomas.domain.model.StudentStats
import com.ute.guamanidiomas.domain.model.UserAchievement

data class DjangoPage<T>(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T> = emptyList()
)

data class StudentStatsDto(
    val id: Int = 0,
    @SerializedName("user") val userId: Int = 0,
    @SerializedName("user_email") val userEmail: String? = null,
    @SerializedName("total_xp") val totalXp: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("longest_streak") val longestStreak: Int = 0,
    @SerializedName("last_active_date") val lastActiveDate: String? = null,
    @SerializedName("modules_completed") val modulesCompleted: Int = 0,
    val level: Int? = null,
    @SerializedName("xp_for_next_level") val xpForNextLevel: Int? = null,
    @SerializedName("xp_progress") val xpProgress: Int? = null
) {
    fun toDomain() = StudentStats(
        id               = id,
        userId           = userId,
        userEmail        = userEmail.orEmpty(),
        totalXp          = totalXp,
        currentStreak    = currentStreak,
        longestStreak    = longestStreak,
        lastActiveDate   = lastActiveDate.orEmpty(),
        modulesCompleted = modulesCompleted
    )
}

data class LessonProgressDto(
    val id: Int = 0,
    @SerializedName("user") val userId: Int = 0,
    @SerializedName("user_email") val userEmail: String? = null,
    @SerializedName("lesson") val lessonId: Int = 0,
    @SerializedName("lesson_title") val lessonTitle: String? = null,
    val status: String? = null,
    val score: Int = 0,
    @SerializedName("completed_at") val completedAt: String? = null
) {
    fun toDomain() = LessonProgress(
        id          = id,
        userId      = userId,
        userEmail   = userEmail.orEmpty(),
        lessonId    = lessonId,
        lessonTitle = lessonTitle.orEmpty(),
        status      = status.orEmpty(),
        score       = score,
        completedAt = completedAt
    )
}

data class PostProgressRequestDto(
    @SerializedName("lesson") val lessonId: Int,
    val status: String = "completed",
    val score: Int = 0
)

data class AchievementDetailDto(
    val id: Int = 0,
    val name: String? = null,
    val title: String? = null,
    val description: String? = null,
    val icon: String? = null,
    @SerializedName("icon_url") val iconUrl: String? = null,
    @SerializedName("xp_required") val xpRequired: Int = 0,
    @SerializedName("required_xp") val requiredXp: Int = 0,
    @SerializedName("streak_required") val streakRequired: Int = 0
) {
    fun toDomain() = Achievement(
        id             = id,
        title          = name ?: title.orEmpty(),
        description    = description.orEmpty(),
        icon           = iconUrl ?: icon.orEmpty(),
        xpRequired     = if (requiredXp > 0) requiredXp else xpRequired,
        streakRequired = streakRequired
    )
}

data class UserAchievementDto(
    val id: Int = 0,
    @SerializedName("user") val userId: Int = 0,
    val achievement: AchievementDetailDto? = null,
    @SerializedName("achievement_name") val achievementName: String? = null,
    val title: String? = null,
    val name: String? = null,
    val description: String? = null,
    @SerializedName("icon_url") val iconUrl: String? = null,
    val unlocked: Boolean? = null,
    @SerializedName("unlocked_at") val unlockedAt: String? = null
) {
    fun toDomain() = UserAchievement(
        id          = id,
        userId      = userId,
        title       = achievement?.toDomain()?.title
            ?: achievementName
            ?: name
            ?: title
            ?: "Logro #$id",
        description = achievement?.toDomain()?.description
            ?: description
            ?: "",
        icon        = (achievement?.toDomain()?.icon
            ?: iconUrl
            ?: "").replace(" ", "").trim(),
        unlockedAt  = unlockedAt.orEmpty()
    )
}