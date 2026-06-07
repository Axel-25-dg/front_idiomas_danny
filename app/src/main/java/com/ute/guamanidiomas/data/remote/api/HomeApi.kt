package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.AchievementDto
import com.ute.guamanidiomas.data.remote.dto.HomeStatsDto
import com.ute.guamanidiomas.data.remote.dto.ProgressResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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

interface HomeApi {

    @GET("stats/")
    suspend fun getStats(): Response<HomeStatsPage>

    @GET("my-achievements/")
    suspend fun getUserAchievements(): Response<HomeAchievementsPage>

    @GET("progress/")
    suspend fun getProgress(
        @Query("page_size") pageSize: Int = 50
    ): Response<ProgressResponseDto>
}
