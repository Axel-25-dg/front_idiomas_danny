package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.ClassroomDto
import com.ute.guamanidiomas.data.remote.dto.ClassroomPage
import com.ute.guamanidiomas.data.remote.dto.TeacherResourceDto
import retrofit2.Response
import retrofit2.http.*

interface ClassroomApi {

    @GET("classrooms/mine/")
    suspend fun getMyClassrooms(): Response<ClassroomPage>

    @GET("classrooms/{id}/")
    suspend fun getClassroomById(@Path("id") id: Int): Response<ClassroomDto>

    @GET("resources/")
    suspend fun getClassroomResources(
        @Query("course") courseId: Int? = null
    ): Response<ResourcePage>

    @POST("classrooms/{id}/remove-student/")
    suspend fun leaveClassroom(@Path("id") classroomId: Int): Response<Unit>
}

data class ResourcePage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<TeacherResourceDto>
)