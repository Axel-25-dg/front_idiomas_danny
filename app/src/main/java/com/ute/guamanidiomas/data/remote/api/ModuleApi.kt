package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.domain.model.ModulePayload
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.*

interface ModuleApi {
    @GET("modules/")
    suspend fun getModules(): Response<List<Module>>

    @GET("modules/")
    suspend fun getModulesByCourse(@Query("course") courseId: Int): Response<JsonElement>

    @GET("modules/{id}/")
    suspend fun getModuleById(@Path("id") id: Int): Response<Module>

    @POST("modules/")
    suspend fun createModule(@Body payload: ModulePayload): Response<Module>

    @PUT("modules/{id}/")
    suspend fun updateModule(@Path("id") id: Int, @Body payload: ModulePayload): Response<Module>

    @DELETE("modules/{id}/")
    suspend fun deleteModule(@Path("id") id: Int): Response<Unit>
}