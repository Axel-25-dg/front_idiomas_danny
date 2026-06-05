package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.TeacherResourceDto
import com.ute.guamanidiomas.data.remote.dto.TeacherResourceRequest
import retrofit2.Response
import retrofit2.http.*

interface TeacherResourceApi {

    @GET("teacher-resources/")
    suspend fun getResources(
        @Query("classroom") classroomId: Int? = null
    ): Response<List<TeacherResourceDto>>

    @GET("teacher-resources/{id}/")
    suspend fun getResourceById(@Path("id") id: Int): Response<TeacherResourceDto>

    @POST("teacher-resources/")
    suspend fun createResource(@Body request: TeacherResourceRequest): Response<TeacherResourceDto>

    @PUT("teacher-resources/{id}/")
    suspend fun updateResource(
        @Path("id") id: Int,
        @Body request: TeacherResourceRequest
    ): Response<TeacherResourceDto>

    @DELETE("teacher-resources/{id}/")
    suspend fun deleteResource(@Path("id") id: Int): Response<Unit>
}
