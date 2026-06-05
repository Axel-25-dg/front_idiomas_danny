package com.ute.guamanidiomas.domain.model

enum class CertificateStatus(val value: String, val label: String) {
    PENDING("pending", "Pendiente"),
    ISSUED("issued", "Emitido"),
    REVOKED("revoked", "Revocado");

    companion object {
        fun fromString(value: String): CertificateStatus =
            entries.firstOrNull { it.value == value.lowercase() } ?: PENDING
    }
}

data class Certificate(
    val id: Int,
    val studentId: Int,
    val studentEmail: String,
    val studentName: String,
    val courseId: Int?,
    val courseTitle: String,
    val classroomId: Int?,
    val classroomName: String,
    val level: String,
    val status: CertificateStatus,
    val verificationCode: String,
    val issuedAt: String?,
    val createdAt: String,
    val teacherName: String
)
