package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.AchievementDto
import com.ute.guamanidiomas.data.remote.dto.HomeStatsDto
import com.ute.guamanidiomas.data.remote.dto.ProgressResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {

    @GET("stats/")
    suspend fun getStats(): Response<List<HomeStatsDto>>

    @GET("my-achievements/")
    suspend fun getUserAchievements(): Response<List<AchievementDto>>

    @GET("progress/")
    suspend fun getProgress(
        @Query("page_size") pageSize: Int = 50
    ): Response<ProgressResponseDto>
}
