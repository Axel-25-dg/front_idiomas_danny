package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {

    // Dashboard consolidado del estudiante
    @GET("dashboard/student/")
    suspend fun getStudentDashboard(): Response<StudentDashboardDto>

    // Ranking global
    @GET("ranking/")
    suspend fun getRanking(): Response<List<RankingEntryDto>>

    // Legacy endpoints (fallback si dashboard no existe)
    @GET("stats/")
    suspend fun getStats(): Response<HomeStatsPage>

    @GET("my-achievements/")
    suspend fun getUserAchievements(): Response<HomeAchievementsPage>

    @GET("progress/")
    suspend fun getProgress(
        @Query("page_size") pageSize: Int = 50
    ): Response<ProgressResponseDto>
}

data class HomeStatsPage(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<HomeStatsDto>? = null
)

data class HomeAchievementsPage(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<AchievementDto>? = null
)
