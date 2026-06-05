package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.ClassroomDto
import com.ute.guamanidiomas.data.remote.dto.ClassroomPage
import retrofit2.Response
import retrofit2.http.*

/**
 * API para el rol student — consumir sus propias clases.
 * Usa los mismos endpoints del backend /api/classrooms/
 */
interface ClassroomApi {

    /** Clases en las que está inscrito el estudiante autenticado */
    @GET("classrooms/mine/")
    suspend fun getMyClassrooms(): Response<List<ClassroomDto>>

    /** Detalle de una clase específica */
    @GET("classrooms/{id}/")
    suspend fun getClassroomById(@Path("id") id: Int): Response<ClassroomDto>

    /** Obtener recursos de una clase */
    @GET("resources/")
    suspend fun getClassroomResources(
        @Query("classroom") classroomId: Int? = null
    ): Response<List<com.ute.guamanidiomas.data.remote.dto.TeacherResourceDto>>

    /** Remover al estudiante autenticado de una clase */
    @POST("classrooms/{id}/remove-student/")
    suspend fun leaveClassroom(@Path("id") classroomId: Int): Response<Unit>
}
