package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.teacher.*

// ─── Classroom ────────────────────────────────────────────────────────────────

data class ClassroomDto(
    val id: Int,
    @SerializedName("course") val courseId: Int,
    @SerializedName("course_title") val courseTitle: String = "",
    @SerializedName("course_level") val courseLevel: String = "",
    val name: String,
    val description: String = "",
    @SerializedName("access_code") val accessCode: String = "",
    @SerializedName("teacher") val teacherId: Int,
    @SerializedName("teacher_name") val teacherName: String = "",
    @SerializedName("student_count") val studentCount: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String = ""
) {
    fun toDomain() = Classroom(
        id           = id,
        courseId     = courseId,
        courseTitle  = courseTitle,
        courseLevel  = courseLevel,
        name         = name,
        description  = description,
        accessCode   = accessCode,
        teacherId    = teacherId,
        teacherName  = teacherName,
        studentCount = studentCount,
        isActive     = isActive,
        createdAt    = createdAt
    )
}

data class ClassroomRequest(
    @SerializedName("course") val courseId: Int,
    val name: String,
    val description: String = "",
    @SerializedName("is_active") val isActive: Boolean = true
)

data class ClassroomPage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ClassroomDto>
)

// ─── Enrollment ───────────────────────────────────────────────────────────────

data class EnrollmentDto(
    val id: Int,
    @SerializedName("classroom") val classroomId: Int,
    @SerializedName("classroom_name") val classroomName: String = "",
    @SerializedName("student") val studentId: Int,
    @SerializedName("student_email") val studentEmail: String = "",
    @SerializedName("student_name") val studentName: String = "",
    @SerializedName("total_xp") val totalXp: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("modules_completed") val modulesCompleted: Int = 0,
    @SerializedName("enrolled_at") val enrolledAt: String = ""
) {
    fun toDomain() = Enrollment(
        id               = id,
        classroomId      = classroomId,
        classroomName    = classroomName,
        studentId        = studentId,
        studentEmail     = studentEmail,
        studentName      = studentName.ifBlank { studentEmail },
        totalXp          = totalXp,
        currentStreak    = currentStreak,
        modulesCompleted = modulesCompleted,
        enrolledAt       = enrolledAt
    )
}

// ─── Exam ─────────────────────────────────────────────────────────────────────

data class ExamDto(
    val id: Int,
    @SerializedName("classroom") val classroomId: Int,
    @SerializedName("classroom_name") val classroomName: String = "",
    val title: String,
    val description: String = "",
    @SerializedName("time_limit_minutes") val timeLimitMinutes: Int = 60,
    @SerializedName("passing_score") val passingScore: Int = 70,
    @SerializedName("auto_grade") val autoGrade: Boolean = true,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("submission_count") val submissionCount: Int = 0
) {
    fun toDomain() = Exam(
        id               = id,
        classroomId      = classroomId,
        classroomName    = classroomName,
        title            = title,
        description      = description,
        timeLimitMinutes = timeLimitMinutes,
        passingScore     = passingScore,
        autoGrade        = autoGrade,
        startDate        = startDate,
        endDate          = endDate,
        isActive         = isActive,
        createdAt        = createdAt,
        submissionCount  = submissionCount
    )
}

data class ExamRequest(
    @SerializedName("classroom") val classroomId: Int,
    val title: String,
    val description: String = "",
    @SerializedName("time_limit_minutes") val timeLimitMinutes: Int = 60,
    @SerializedName("passing_score") val passingScore: Int = 70,
    @SerializedName("auto_grade") val autoGrade: Boolean = true,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class ExamPage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ExamDto>
)

// ─── ExamResult ───────────────────────────────────────────────────────────────

data class ExamResultDto(
    val id: Int,
    @SerializedName("exam") val examId: Int,
    @SerializedName("exam_title") val examTitle: String = "",
    @SerializedName("student") val studentId: Int,
    @SerializedName("student_email") val studentEmail: String = "",
    @SerializedName("student_name") val studentName: String = "",
    val score: Int = 0,
    val passed: Boolean = false,
    @SerializedName("submitted_at") val submittedAt: String = ""
) {
    fun toDomain() = ExamResult(
        id           = id,
        examId       = examId,
        examTitle    = examTitle,
        studentId    = studentId,
        studentEmail = studentEmail,
        studentName  = studentName.ifBlank { studentEmail },
        score        = score,
        passed       = passed,
        submittedAt  = submittedAt
    )
}

// ─── TeacherResource ──────────────────────────────────────────────────────────

data class TeacherResourceDto(
    val id: Int,
    @SerializedName("classroom") val classroomId: Int,
    @SerializedName("classroom_name") val classroomName: String = "",
    val title: String,
    val description: String = "",
    @SerializedName("resource_type") val resourceType: String = "link",
    val url: String = "",
    @SerializedName("file_url") val fileUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String = ""
) {
    fun toDomain() = TeacherResource(
        id            = id,
        classroomId   = classroomId,
        classroomName = classroomName,
        title         = title,
        description   = description,
        resourceType  = ResourceType.fromString(resourceType),
        url           = url,
        fileUrl       = fileUrl,
        isActive      = isActive,
        createdAt     = createdAt
    )
}

data class TeacherResourceRequest(
    @SerializedName("classroom") val classroomId: Int,
    val title: String,
    val description: String = "",
    @SerializedName("resource_type") val resourceType: String = "link",
    val url: String = "",
    @SerializedName("is_active") val isActive: Boolean = true
)

// ─── Teacher Stats (summary for dashboard home) ───────────────────────────────

data class TeacherStatsDto(
    @SerializedName("total_classrooms") val totalClassrooms: Int = 0,
    @SerializedName("total_students") val totalStudents: Int = 0,
    @SerializedName("active_exams") val activeExams: Int = 0,
    @SerializedName("total_resources") val totalResources: Int = 0,
    @SerializedName("average_score") val averageScore: Double = 0.0
) {
    fun toDomain() = TeacherStats(
        totalClassrooms = totalClassrooms,
        totalStudents   = totalStudents,
        activeExams     = activeExams,
        totalResources  = totalResources,
        averageScore    = averageScore
    )
}
