package com.ute.guamanidiomas.domain.model

data class StudentStats(
    val id: Int,
    val userId: Int,
    val userEmail: String,
    val totalXp: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: String,
    val modulesCompleted: Int
)

data class LessonProgress(
    val id: Int,
    val userId: Int,
    val userEmail: String,
    val lessonId: Int,
    val lessonTitle: String,
    val status: String,
    val score: Int,
    val completedAt: String?
)

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val xpRequired: Int,
    val streakRequired: Int
)

data class UserAchievement(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedAt: String
)
