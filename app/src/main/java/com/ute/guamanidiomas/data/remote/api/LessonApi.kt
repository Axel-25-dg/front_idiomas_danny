package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.LessonDto
import com.ute.guamanidiomas.data.remote.dto.LessonResponseDto
import retrofit2.Response
import retrofit2.http.*

interface LessonApi {

    @GET("lessons/")
    suspend fun getLessons(
        @Query("module") moduleId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): Response<LessonResponseDto>

    @GET("lessons/{id}/")
    suspend fun getLessonById(@Path("id") id: Int): Response<LessonDto>

    @POST("lessons/")
    suspend fun createLesson(@Body payload: Map<String, @JvmSuppressWildcards Any>): Response<LessonDto>

    @PUT("lessons/{id}/")
    suspend fun updateLesson(@Path("id") id: Int, @Body payload: Map<String, @JvmSuppressWildcards Any>): Response<LessonDto>

    @DELETE("lessons/{id}/")
    suspend fun deleteLesson(@Path("id") id: Int): Response<Unit>
}