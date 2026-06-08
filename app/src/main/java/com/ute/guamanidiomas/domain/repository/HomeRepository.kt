package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.data.remote.dto.AchievementDto
import com.ute.guamanidiomas.data.remote.dto.HomeStatsDto
import com.ute.guamanidiomas.data.remote.dto.ProgressResponseDto
import com.ute.guamanidiomas.data.remote.dto.RankingEntryDto
import com.ute.guamanidiomas.data.remote.dto.StudentDashboardDto

interface HomeRepository {
    suspend fun getStudentDashboard(): Result<StudentDashboardDto>
    suspend fun getRanking(): Result<List<RankingEntryDto>>
    suspend fun getStats(): Result<List<HomeStatsDto>>
    suspend fun getAchievements(): Result<List<AchievementDto>>
    suspend fun getProgress(): Result<ProgressResponseDto>
}