package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Certificate
import com.ute.guamanidiomas.domain.model.CertificateStatus

data class CertificateDto(
    val id: Int,
    @SerializedName("student") val studentId: Int,
    @SerializedName("student_email") val studentEmail: String = "",
    @SerializedName("student_name") val studentName: String = "",
    @SerializedName("course") val courseId: Int? = null,
    @SerializedName("course_title") val courseTitle: String = "",
    @SerializedName("classroom") val classroomId: Int? = null,
    @SerializedName("classroom_name") val classroomName: String = "",
    val level: String = "",
    val status: String = "pending",
    @SerializedName("verification_code") val verificationCode: String = "",
    @SerializedName("issued_at") val issuedAt: String? = null,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("teacher_name") val teacherName: String = ""
) {
    fun toDomain() = Certificate(
        id               = id,
        studentId        = studentId,
        studentEmail     = studentEmail,
        studentName      = studentName.ifBlank { studentEmail },
        courseId         = courseId,
        courseTitle      = courseTitle,
        classroomId      = classroomId,
        classroomName    = classroomName,
        level            = level,
        status           = CertificateStatus.fromString(status),
        verificationCode = verificationCode,
        issuedAt         = issuedAt,
        createdAt        = createdAt,
        teacherName      = teacherName
    )
}

data class CertificateRequest(
    @SerializedName("student") val studentId: Int,
    @SerializedName("course") val courseId: Int? = null,
    @SerializedName("classroom") val classroomId: Int? = null,
    val level: String
)