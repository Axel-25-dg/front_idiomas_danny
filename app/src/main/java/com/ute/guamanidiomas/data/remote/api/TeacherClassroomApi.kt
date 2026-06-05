package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.ClassroomDto
import com.ute.guamanidiomas.data.remote.dto.ClassroomPage
import com.ute.guamanidiomas.data.remote.dto.ClassroomRequest
import com.ute.guamanidiomas.data.remote.dto.EnrollmentDto
import com.ute.guamanidiomas.data.remote.dto.TeacherStatsDto
import retrofit2.Response
import retrofit2.http.*

interface TeacherClassroomApi {

    // Stats del profesor autenticado
    @GET("teacher/stats/")
    suspend fun getTeacherStats(): Response<TeacherStatsDto>

    // Classrooms
    @GET("classrooms/")
    suspend fun getClassrooms(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<ClassroomPage>

    @GET("classrooms/{id}/")
    suspend fun getClassroomById(@Path("id") id: Int): Response<ClassroomDto>

    @POST("classrooms/")
    suspend fun createClassroom(@Body request: ClassroomRequest): Response<ClassroomDto>

    @PUT("classrooms/{id}/")
    suspend fun updateClassroom(
        @Path("id") id: Int,
        @Body request: ClassroomRequest
    ): Response<ClassroomDto>

    @DELETE("classrooms/{id}/")
    suspend fun deleteClassroom(@Path("id") id: Int): Response<Unit>

    // Enrollments / students of a classroom
    // Django REST puede devolver objeto paginado { count, next, previous, results:[...] }
    // o lista directa según la configuración del ViewSet.
    // Usamos EnrollmentPage para cubrir el caso paginado.
    @GET("classrooms/{id}/enrollments/")
    suspend fun getEnrollments(@Path("id") classroomId: Int): Response<EnrollmentPage>
}

/** Wrapper paginado para enrollments (por si el ViewSet aplica paginación) */
data class EnrollmentPage(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<EnrollmentDto> = emptyList()
)
