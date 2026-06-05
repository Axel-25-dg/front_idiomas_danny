package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.domain.model.Language
import com.ute.guamanidiomas.domain.model.LanguagePayload
import retrofit2.Response
import retrofit2.http.*

interface LanguageApi {
    @GET("languages/")
    suspend fun getLanguages(): Response<List<Language>>

    @GET("languages/{id}/")
    suspend fun getLanguageById(@Path("id") id: Int): Response<Language>

    @POST("languages/")
    suspend fun createLanguage(@Body payload: LanguagePayload): Response<Language>

    @PUT("languages/{id}/")
    suspend fun updateLanguage(@Path("id") id: Int, @Body payload: LanguagePayload): Response<Language>

    @DELETE("languages/{id}/")
    suspend fun deleteLanguage(@Path("id") id: Int): Response<Unit>
}
