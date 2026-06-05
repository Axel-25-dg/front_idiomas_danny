package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.data.remote.dto.AchievementDto
import com.ute.guamanidiomas.data.remote.dto.HomeStatsDto
import com.ute.guamanidiomas.data.remote.dto.ProgressResponseDto

interface HomeRepository {
    suspend fun getStats(): Result<List<HomeStatsDto>>
    suspend fun getAchievements(): Result<List<AchievementDto>>
    suspend fun getProgress(): Result<ProgressResponseDto>
}
