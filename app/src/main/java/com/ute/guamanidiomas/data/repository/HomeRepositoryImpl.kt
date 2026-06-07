package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.HomeApi
import com.ute.guamanidiomas.data.remote.dto.ProgressResponseDto
import com.ute.guamanidiomas.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getStats(): Result<List<com.ute.guamanidiomas.data.remote.dto.HomeStatsDto>> = runCatching {
        val response = api.getStats()
        if (response.isSuccessful) {
            // Backend devuelve { count, results: [...] }
            response.body()?.results ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getAchievements(): Result<List<com.ute.guamanidiomas.data.remote.dto.AchievementDto>> = runCatching {
        val response = api.getUserAchievements()
        if (response.isSuccessful) {
            // Backend devuelve { count, results: [...] }
            response.body()?.results ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getProgress(): Result<ProgressResponseDto> = runCatching {
        val response = api.getProgress()
        if (response.isSuccessful) response.body() ?: ProgressResponseDto(0, null, null, emptyList())
        else throw Exception("Error ${response.code()}")
    }
}
