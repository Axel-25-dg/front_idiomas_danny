package com.ute.guamanidiomas.ui.home

import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.Language

data class UserStats(
    val id: Int,
    val user: Int,
    val userEmail: String,
    val totalXp: Int,
    val currentStreak: Int,
    val longestStreak: Int
) {
    val level: Int get() = (totalXp / 100) + 1
    val xpForNextLevel: Int get() = level * 100
    val levelProgress: Float get() = (totalXp % 100) / 100f
}

data class UserProgress(
    val id: Int,
    val user: Int,
    val userEmail: String,
    val lesson: Int,
    val lessonTitle: String,
    val status: String,
    val score: Int,
    val completedAt: String?
)

data class TeacherTask(
    val id: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    val status: String,
    val priority: String
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val courses: List<Course> = emptyList(),
    val languages: List<Language> = emptyList(),
    val error: String? = null,
    val stats: UserStats? = null,
    val unlockedAchievementsCount: Int = 0,
    val lessonProgressList: List<UserProgress> = emptyList(),
    val teacherTasks: List<TeacherTask> = emptyList(),
    val completedLessonsCount: Int = 0,
    val totalLessonsCount: Int = 0,
    val progressPercentage: Int = 0,
    val certificatesCount: Int = 0,
    val activeClassrooms: Int = 0
) {
    val globalProgress: Float get() = progressPercentage / 100f
}