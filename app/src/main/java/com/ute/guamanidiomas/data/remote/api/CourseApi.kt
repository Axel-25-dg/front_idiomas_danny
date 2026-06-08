package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.CourseResponse
import com.ute.guamanidiomas.data.remote.dto.CourseRequest
import com.ute.guamanidiomas.domain.model.Course
import retrofit2.Response
import retrofit2.http.*

interface CourseApi {
    @GET("courses/")
    suspend fun getCourses(
        @QueryMap params: Map<String, String>
    ): Response<CourseResponse>

    @GET("courses/{id}/")
    suspend fun getCourseById(@Path("id") id: Int): Response<Course>

    @POST("courses/")
    suspend fun createCourse(@Body payload: CourseRequest): Response<Course>

    @PUT("courses/{id}/")
    suspend fun updateCourse(@Path("id") id: Int, @Body payload: CourseRequest): Response<Course>

    @DELETE("courses/{id}/")
    suspend fun deleteCourse(@Path("id") id: Int): Response<Unit>

    @POST("classrooms/join/")
    suspend fun joinClass(@Body payload: Map<String, String>): Response<Map<String, @JvmSuppressWildcards Any>>
}