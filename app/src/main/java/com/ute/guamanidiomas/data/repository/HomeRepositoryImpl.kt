package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.HomeApi
import com.ute.guamanidiomas.data.remote.dto.*
import com.ute.guamanidiomas.domain.repository.HomeRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getStudentDashboard(): Result<StudentDashboardDto> = runCatching {
        try {
            val response = api.getStudentDashboard()
            if (response.isSuccessful) {
                response.body() ?: StudentDashboardDto()
            } else {
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getRanking(): Result<List<RankingEntryDto>> = runCatching {
        try {
            val response = api.getRanking()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                if (response.code() == 404) emptyList()
                else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getStats(): Result<List<HomeStatsDto>> = runCatching {
        try {
            val response = api.getStats()
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getAchievements(): Result<List<AchievementDto>> = runCatching {
        try {
            val response = api.getUserAchievements()
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getProgress(): Result<ProgressResponseDto> = runCatching {
        try {
            val response = api.getProgress()
            if (response.isSuccessful) response.body() ?: ProgressResponseDto()
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }
}
