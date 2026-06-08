package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.ExerciseDto
import com.ute.guamanidiomas.data.remote.dto.ExerciseRequestDto
import com.ute.guamanidiomas.data.remote.dto.ExercisePageDto
import retrofit2.Response
import retrofit2.http.*

interface ExerciseApi {

    @GET("exercises/")
    suspend fun getExercises(
        @Query("lesson") lessonId: Int? = null
    ): Response<ExercisePageDto>

    @GET("exercises/{id}/")
    suspend fun getExerciseById(@Path("id") id: Int): Response<ExerciseDto>

    @POST("exercises/")
    suspend fun createExercise(@Body payload: ExerciseRequestDto): Response<ExerciseDto>

    @PUT("exercises/{id}/")
    suspend fun updateExercise(@Path("id") id: Int, @Body payload: ExerciseRequestDto): Response<ExerciseDto>

    @DELETE("exercises/{id}/")
    suspend fun deleteExercise(@Path("id") id: Int): Response<Unit>

    @GET("modules/{moduleId}/exercises/")
    suspend fun getExercisesByModule(@Path("moduleId") moduleId: Int): Response<List<ExerciseDto>>
}