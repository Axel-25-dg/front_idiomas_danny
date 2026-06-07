package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.HomeApi
import com.ute.guamanidiomas.data.remote.dto.*
import com.ute.guamanidiomas.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getStudentDashboard(): Result<StudentDashboardDto> = runCatching {
        val response = api.getStudentDashboard()
        if (response.isSuccessful) {
            response.body() ?: StudentDashboardDto()
        } else throw Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun getRanking(): Result<List<RankingEntryDto>> = runCatching {
        val response = api.getRanking()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            // Si devuelve 404 el endpoint no existe — devolver vacio
            emptyList()
        }
    }

    override suspend fun getStats(): Result<List<HomeStatsDto>> = runCatching {
        val response = api.getStats()
        if (response.isSuccessful) {
            response.body()?.results ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getAchievements(): Result<List<AchievementDto>> = runCatching {
        val response = api.getUserAchievements()
        if (response.isSuccessful) {
            response.body()?.results ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getProgress(): Result<ProgressResponseDto> = runCatching {
        val response = api.getProgress()
        if (response.isSuccessful) response.body() ?: ProgressResponseDto()
        else throw Exception("Error ${response.code()}")
    }
}
