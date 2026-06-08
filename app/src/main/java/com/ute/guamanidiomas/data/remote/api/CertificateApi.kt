package com.ute.guamanidiomas.data.remote.api

import com.ute.guamanidiomas.data.remote.dto.CertificateDto
import com.ute.guamanidiomas.data.remote.dto.CertificateRequest
import retrofit2.Response
import retrofit2.http.*

data class CertificatePage(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<CertificateDto> = emptyList()
)

interface CertificateApi {

    @POST("certificates/")
    suspend fun createCertificate(@Body request: CertificateRequest): Response<CertificateDto>

    @GET("certificates/")
    suspend fun getMyCertificates(): Response<CertificatePage>

    @PATCH("certificates/{id}/issue/")
    suspend fun issueCertificate(@Path("id") id: Int): Response<CertificateDto>

    @PATCH("certificates/{id}/revoke/")
    suspend fun revokeCertificate(@Path("id") id: Int): Response<CertificateDto>

    @GET("certificates/verify/{code}/")
    suspend fun verifyCertificate(@Path("code") code: String): Response<CertificateDto>
}