package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.ClassroomDto
import com.ute.guamanidiomas.data.remote.dto.ClassroomPage
import com.ute.guamanidiomas.data.remote.dto.TeacherResourceDto
import retrofit2.Response
import retrofit2.http.*

/**
 * API para el rol student — consumir sus propias clases.
 * El backend Django REST devuelve objetos paginados { count, next, previous, results:[...] }
 * en los endpoints de lista. Usamos ClassroomPage para /mine/ y ResourcePage para resources.
 */
interface ClassroomApi {

    /** Clases en las que está inscrito el estudiante autenticado — responde con objeto paginado */
    @GET("classrooms/mine/")
    suspend fun getMyClassrooms(): Response<ClassroomPage>

    /** Detalle de una clase específica */
    @GET("classrooms/{id}/")
    suspend fun getClassroomById(@Path("id") id: Int): Response<ClassroomDto>

    /** Obtener recursos — filtrado por curso */
    @GET("resources/")
    suspend fun getClassroomResources(
        @Query("course") courseId: Int? = null
    ): Response<ResourcePage>

    /** Remover al estudiante autenticado de una clase */
    @POST("classrooms/{id}/remove-student/")
    suspend fun leaveClassroom(@Path("id") classroomId: Int): Response<Unit>
}

/** Wrapper paginado para recursos (equivalente a ClassroomPage pero para TeacherResourceDto) */
data class ResourcePage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<TeacherResourceDto>
)
