package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Achievement
import com.ute.guamanidiomas.domain.model.LessonProgress
import com.ute.guamanidiomas.domain.model.StudentStats
import com.ute.guamanidiomas.domain.model.UserAchievement

data class DjangoPage<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

data class StudentStatsDto(
    val id: Int,
    @SerializedName("user") val userId: Int,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("last_active_date") val lastActiveDate: String? = null,
    @SerializedName("modules_completed") val modulesCompleted: Int = 0
) {
    fun toDomain() = StudentStats(
        id               = id,
        userId           = userId,
        userEmail        = userEmail,
        totalXp          = totalXp,
        currentStreak    = currentStreak,
        longestStreak    = longestStreak,
        lastActiveDate   = lastActiveDate.orEmpty(),
        modulesCompleted = modulesCompleted
    )
}

data class LessonProgressDto(
    val id: Int,
    @SerializedName("user") val userId: Int,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("lesson") val lessonId: Int,
    @SerializedName("lesson_title") val lessonTitle: String,
    val status: String,
    val score: Int = 0,
    @SerializedName("completed_at") val completedAt: String? = null
) {
    fun toDomain() = LessonProgress(
        id          = id,
        userId      = userId,
        userEmail   = userEmail,
        lessonId    = lessonId,
        lessonTitle = lessonTitle,
        status      = status,
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
    val id: Int,
    val title: String,
    val description: String,
    val icon: String? = null,
    @SerializedName("xp_required") val xpRequired: Int = 0,
    @SerializedName("streak_required") val streakRequired: Int = 0
) {
    fun toDomain() = Achievement(
        id             = id,
        title          = title,
        description    = description,
        icon           = icon.orEmpty(),
        xpRequired     = xpRequired,
        streakRequired = streakRequired
    )
}

data class UserAchievementDto(
    val id: Int,
    @SerializedName("user") val userId: Int,
    val achievement: AchievementDetailDto?,
    val title: String? = null,
    val description: String? = null,
    val unlocked: Boolean? = null,
    @SerializedName("unlocked_at") val unlockedAt: String? = null
) {
    fun toDomain() = UserAchievement(
        id          = id,
        userId      = userId,
        title       = achievement?.title ?: title.orEmpty(),
        description = achievement?.description ?: description.orEmpty(),
        icon        = achievement?.icon.orEmpty(),
        unlockedAt  = unlockedAt.orEmpty()
    )
}
