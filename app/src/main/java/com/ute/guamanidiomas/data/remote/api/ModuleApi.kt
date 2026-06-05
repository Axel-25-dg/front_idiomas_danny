package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.domain.model.ModulePayload
import retrofit2.Response
import retrofit2.http.*

interface ModuleApi {
    @GET("modules/")
    suspend fun getModules(): Response<List<Module>>

    @GET("courses/{courseId}/modules/")
    suspend fun getModulesByCourse(@Path("courseId") courseId: Int): Response<List<Module>>

    @GET("modules/{id}/")
    suspend fun getModuleById(@Path("id") id: Int): Response<Module>

    @POST("modules/")
    suspend fun createModule(@Body payload: ModulePayload): Response<Module>

    @PUT("modules/{id}/")
    suspend fun updateModule(@Path("id") id: Int, @Body payload: ModulePayload): Response<Module>

    @DELETE("modules/{id}/")
    suspend fun deleteModule(@Path("id") id: Int): Response<Unit>
}
