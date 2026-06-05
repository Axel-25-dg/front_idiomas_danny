package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.ExamDto
import com.ute.guamanidiomas.data.remote.dto.ExamPage
import com.ute.guamanidiomas.data.remote.dto.ExamRequest
import com.ute.guamanidiomas.data.remote.dto.ExamResultDto
import retrofit2.Response
import retrofit2.http.*

interface TeacherExamApi {

    @GET("exams/")
    suspend fun getExams(
        @Query("classroom") classroomId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<ExamPage>

    @GET("exams/{id}/")
    suspend fun getExamById(@Path("id") id: Int): Response<ExamDto>

    @POST("exams/")
    suspend fun createExam(@Body request: ExamRequest): Response<ExamDto>

    @PUT("exams/{id}/")
    suspend fun updateExam(
        @Path("id") id: Int,
        @Body request: ExamRequest
    ): Response<ExamDto>

    @DELETE("exams/{id}/")
    suspend fun deleteExam(@Path("id") id: Int): Response<Unit>

    @GET("exams/{id}/results/")
    suspend fun getExamResults(@Path("id") examId: Int): Response<List<ExamResultDto>>
}
