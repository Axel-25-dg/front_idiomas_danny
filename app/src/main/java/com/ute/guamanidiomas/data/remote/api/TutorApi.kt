package com.ute.guamanidiomas.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TutorApi {
    @POST("tutor/ask/")
    suspend fun askTutor(@Body request: TutorRequest): Response<TutorResponse>
}

data class TutorRequest(val prompt: String)
data class TutorResponse(val response: String)