package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.AchievementDetailDto
import com.ute.guamanidiomas.data.remote.dto.DjangoPage
import com.ute.guamanidiomas.data.remote.dto.LessonProgressDto
import com.ute.guamanidiomas.data.remote.dto.PostProgressRequestDto
import com.ute.guamanidiomas.data.remote.dto.StudentStatsDto
import com.ute.guamanidiomas.data.remote.dto.UserAchievementDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GamificationApi {

    // Stats puede devolver paginado { count, results } o lista directa
    @GET("stats/")
    suspend fun getMyStats(): Response<DjangoPage<StudentStatsDto>>

    @POST("progress/")
    suspend fun postProgress(
        @Body request: PostProgressRequestDto
    ): Response<LessonProgressDto>

    @GET("progress/")
    suspend fun getProgressHistory(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<DjangoPage<LessonProgressDto>>

    // Achievements puede devolver paginado { count, results }
    @GET("achievements/")
    suspend fun getAllAchievements(): Response<DjangoPage<AchievementDetailDto>>

    // My achievements puede devolver lista directa o paginado
    @GET("my-achievements/")
    suspend fun getMyAchievements(): Response<DjangoPage<UserAchievementDto>>
}
