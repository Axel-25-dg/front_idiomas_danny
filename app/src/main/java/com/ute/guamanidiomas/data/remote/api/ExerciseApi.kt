package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExercisePayload
import retrofit2.Response
import retrofit2.http.*

interface ExerciseApi {
    @GET("exercises/")
    suspend fun getExercises(): Response<List<Exercise>>

    @GET("modules/{moduleId}/exercises/")
    suspend fun getExercisesByModule(@Path("moduleId") moduleId: Int): Response<List<Exercise>>

    @GET("exercises/{id}/")
    suspend fun getExerciseById(@Path("id") id: Int): Response<Exercise>

    @POST("exercises/")
    suspend fun createExercise(@Body payload: ExercisePayload): Response<Exercise>

    @PUT("exercises/{id}/")
    suspend fun updateExercise(@Path("id") id: Int, @Body payload: ExercisePayload): Response<Exercise>

    @DELETE("exercises/{id}/")
    suspend fun deleteExercise(@Path("id") id: Int): Response<Unit>
}
