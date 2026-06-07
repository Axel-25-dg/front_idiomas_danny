package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.TeacherResourceDto
import com.ute.guamanidiomas.data.remote.dto.TeacherResourceRequest
import retrofit2.Response
import retrofit2.http.*

interface TeacherResourceApi {

    @GET("resources/")
    suspend fun getResources(
        @Query("course") courseId: Int? = null
    ): Response<TeacherResourcePage>

    @GET("resources/{id}/")
    suspend fun getResourceById(@Path("id") id: Int): Response<TeacherResourceDto>

    @POST("resources/")
    suspend fun createResource(@Body request: TeacherResourceRequest): Response<TeacherResourceDto>

    @PUT("resources/{id}/")
    suspend fun updateResource(
        @Path("id") id: Int,
        @Body request: TeacherResourceRequest
    ): Response<TeacherResourceDto>

    @DELETE("resources/{id}/")
    suspend fun deleteResource(@Path("id") id: Int): Response<Unit>
}

/** Wrapper paginado para resources */
data class TeacherResourcePage(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<TeacherResourceDto>? = null
)
