package com.ute.guamanidiomas.domain.model

data class StudentStats(
    val id: Int = 0,
    val userId: Int = 0,
    val userEmail: String? = null,
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String? = null,
    val modulesCompleted: Int = 0
)

data class LessonProgress(
    val id: Int = 0,
    val userId: Int = 0,
    val userEmail: String? = null,
    val lessonId: Int = 0,
    val lessonTitle: String? = null,
    val status: String? = null,
    val score: Int = 0,
    val completedAt: String? = null
)

data class Achievement(
    val id: Int = 0,
    val title: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val xpRequired: Int = 0,
    val streakRequired: Int = 0
)

data class UserAchievement(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val unlockedAt: String? = null
)
