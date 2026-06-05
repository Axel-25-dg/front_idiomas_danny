package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.CertificateApi
import com.ute.guamanidiomas.data.remote.dto.CertificateRequest
import com.ute.guamanidiomas.domain.model.Certificate
import com.ute.guamanidiomas.domain.repository.CertificateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CertificateRepositoryImpl @Inject constructor(
    private val api: CertificateApi
) : CertificateRepository {

    override suspend fun getMyCertificates(): Result<List<Certificate>> = runCatching {
        val response = api.getMyCertificates()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun createCertificate(
        studentId: Int,
        courseId: Int?,
        classroomId: Int?,
        level: String
    ): Result<Certificate> = runCatching {
        val request = CertificateRequest(
            studentId   = studentId,
            courseId     = courseId,
            classroomId = classroomId,
            level       = level
        )
        val response = api.createCertificate(request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun issueCertificate(id: Int): Result<Certificate> = runCatching {
        val response = api.issueCertificate(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun revokeCertificate(id: Int): Result<Certificate> = runCatching {
        val response = api.revokeCertificate(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun verifyCertificate(code: String): Result<Certificate> = runCatching {
        val response = api.verifyCertificate(code)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    private fun apiError(code: Int, body: String?): String =
        "Error $code${body?.takeIf { it.isNotBlank() }?.let { ": $it" } ?: ""}"
}
