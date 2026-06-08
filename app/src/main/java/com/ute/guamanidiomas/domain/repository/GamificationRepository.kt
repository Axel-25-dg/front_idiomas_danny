package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.data.remote.dto.DjangoPage
import com.ute.guamanidiomas.domain.model.Achievement
import com.ute.guamanidiomas.domain.model.LessonProgress
import com.ute.guamanidiomas.domain.model.StudentStats
import com.ute.guamanidiomas.domain.model.UserAchievement

interface GamificationRepository {
    suspend fun getMyStats(): Result<List<StudentStats>>
    suspend fun postProgress(lessonId: Int, score: Int): Result<LessonProgress>
    suspend fun getProgressHistory(page: Int = 1): Result<DjangoPage<LessonProgress>>
    suspend fun getAllAchievements(): Result<List<Achievement>>
    suspend fun getMyAchievements(): Result<List<UserAchievement>>
}