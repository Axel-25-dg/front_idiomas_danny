package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.Certificate

interface CertificateRepository {
    suspend fun getMyCertificates(): Result<List<Certificate>>
    suspend fun createCertificate(studentId: Int, courseId: Int?, classroomId: Int?, level: String): Result<Certificate>
    suspend fun issueCertificate(id: Int): Result<Certificate>
    suspend fun revokeCertificate(id: Int): Result<Certificate>
    suspend fun verifyCertificate(code: String): Result<Certificate>
}