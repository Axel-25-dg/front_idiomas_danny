package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.teacher.*

data class ClassroomDto(
    val id: Int = 0,
    @SerializedName("course") val courseId: Int? = null,
    @SerializedName("course_title") val courseTitle: String? = null,
    val name: String? = null,
    val description: String? = null,
    @SerializedName("access_code") val accessCode: String? = null,
    @SerializedName("teacher") val teacherId: Int? = null,
    @SerializedName("teacher_email") val teacherEmail: String? = null,
    @SerializedName("teacher_name") val teacherName: String? = null,
    @SerializedName("total_students") val totalStudents: Int? = null,
    @SerializedName("student_count") val studentCount: Int? = null,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    val enrollments: List<EnrollmentInlineDto>? = null
) {
    fun toDomain() = Classroom(
        id           = id,
        courseId      = courseId ?: 0,
        courseTitle   = courseTitle.orEmpty(),
        name         = name.orEmpty(),
        description  = description.orEmpty(),
        accessCode   = accessCode.orEmpty(),
        teacherId    = teacherId ?: 0,
        teacherName  = (teacherName ?: teacherEmail).orEmpty(),
        studentCount = (totalStudents ?: studentCount) ?: enrollments?.size ?: 0,
        isActive     = isActive ?: true,
        createdAt    = createdAt.orEmpty()
    )
}

data class EnrollmentInlineDto(
    val id: Int = 0,
    val student: Int? = null,
    @SerializedName("student_email") val studentEmail: String? = null,
    @SerializedName("enrolled_at") val enrolledAt: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null
)

data class ClassroomRequest(
    @SerializedName("course") val courseId: Int,
    val name: String,
    val description: String = "",
    @SerializedName("is_active") val isActive: Boolean = true
)

data class ClassroomPage(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ClassroomDto>? = null
)

data class EnrollmentDto(
    val id: Int = 0,
    @SerializedName("classroom") val classroomId: Int = 0,
    @SerializedName("classroom_name") val classroomName: String? = null,
    @SerializedName("student") val studentId: Int = 0,
    @SerializedName("student_email") val studentEmail: String? = null,
    @SerializedName("student_name") val studentName: String? = null,
    @SerializedName("total_xp") val totalXp: Int? = null,
    @SerializedName("current_streak") val currentStreak: Int? = null,
    @SerializedName("modules_completed") val modulesCompleted: Int? = null,
    @SerializedName("enrolled_at") val enrolledAt: String? = null
) {
    fun toDomain() = Enrollment(
        id               = id,
        classroomId      = classroomId,
        classroomName    = classroomName.orEmpty(),
        studentId        = studentId,
        studentEmail     = studentEmail.orEmpty(),
        studentName      = studentName?.ifBlank { studentEmail.orEmpty() } ?: studentEmail.orEmpty(),
        totalXp          = totalXp ?: 0,
        currentStreak    = currentStreak ?: 0,
        modulesCompleted = modulesCompleted ?: 0,
        enrolledAt       = enrolledAt.orEmpty()
    )
}

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

data class TeacherResourceDto(
    val id: Int = 0,
    @SerializedName("teacher_email") val teacherEmail: String? = null,
    val title: String? = null,
    val description: String? = null,
    @SerializedName("resource_type") val resourceType: String? = null,
    @SerializedName("resource_type_display") val resourceTypeDisplay: String? = null,
    @SerializedName("file_url") val fileUrl: String? = null,
    val course: Int? = null,
    @SerializedName("course_title") val courseTitle: String? = null,
    val lesson: Int? = null,
    @SerializedName("is_public") val isPublic: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("classroom") val classroomId: Int = 0,
    @SerializedName("classroom_name") val classroomName: String? = null
) {
    fun toDomain() = TeacherResource(
        id            = id,
        classroomId   = classroomId,
        classroomName = classroomName ?: courseTitle.orEmpty(),
        title         = title.orEmpty(),
        description   = description.orEmpty(),
        resourceType  = ResourceType.fromString(resourceType ?: "link"),
        url           = fileUrl.orEmpty(),
        fileUrl       = fileUrl,
        isActive      = isPublic ?: true,
        createdAt     = createdAt.orEmpty()
    )
}

data class TeacherResourceRequest(
    val title: String,
    val description: String = "",
    @SerializedName("resource_type") val resourceType: String = "link",
    @SerializedName("file_url") val fileUrl: String = "",
    val course: Int? = null,
    val lesson: Int? = null,
    @SerializedName("is_public") val isPublic: Boolean = true
)

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

data class TeacherDashboardDto(
    val classrooms: Int = 0,
    val students: Int = 0,
    val resources: Int = 0,
    val lessons: Int = 0,
    val certificates: Int = 0
)