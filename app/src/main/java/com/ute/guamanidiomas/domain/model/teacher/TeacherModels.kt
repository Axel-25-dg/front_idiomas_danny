package com.ute.guamanidiomas.domain.model.teacher

// ─── Classroom ────────────────────────────────────────────────────────────────

data class Classroom(
    val id: Int,
    val courseId: Int,
    val courseTitle: String,
    val name: String,
    val description: String,
    val accessCode: String,
    val teacherId: Int,
    val teacherName: String,
    val studentCount: Int,
    val isActive: Boolean,
    val createdAt: String
)

data class ClassroomPayload(
    val courseId: Int,
    val name: String,
    val description: String,
    val isActive: Boolean = true
)

// ─── Enrollment ───────────────────────────────────────────────────────────────

data class Enrollment(
    val id: Int,
    val classroomId: Int,
    val classroomName: String,
    val studentId: Int,
    val studentEmail: String,
    val studentName: String,
    val totalXp: Int,
    val currentStreak: Int,
    val modulesCompleted: Int,
    val enrolledAt: String
)

// ─── Exam ─────────────────────────────────────────────────────────────────────

data class Exam(
    val id: Int,
    val classroomId: Int,
    val classroomName: String,
    val title: String,
    val description: String,
    val timeLimitMinutes: Int,
    val passingScore: Int,
    val autoGrade: Boolean,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean,
    val createdAt: String,
    val submissionCount: Int
)

data class ExamPayload(
    val classroomId: Int,
    val title: String,
    val description: String,
    val timeLimitMinutes: Int,
    val passingScore: Int,
    val autoGrade: Boolean,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean
)

// ─── ExamResult ───────────────────────────────────────────────────────────────

data class ExamResult(
    val id: Int,
    val examId: Int,
    val examTitle: String,
    val studentId: Int,
    val studentEmail: String,
    val studentName: String,
    val score: Int,
    val passed: Boolean,
    val submittedAt: String
)

// ─── TeacherResource ──────────────────────────────────────────────────────────

enum class ResourceType(val value: String, val label: String) {
    LINK("link", "Enlace"),
    PDF("pdf", "PDF"),
    WORD("word", "Word"),
    POWERPOINT("powerpoint", "PowerPoint"),
    AUDIO("audio", "Audio MP3"),
    VIDEO("video", "Video"),
    YOUTUBE("youtube", "YouTube");

    companion object {
        fun fromString(value: String): ResourceType =
            entries.firstOrNull { it.value == value.lowercase() } ?: LINK
    }
}

data class TeacherResource(
    val id: Int,
    val classroomId: Int,
    val classroomName: String,
    val title: String,
    val description: String,
    val resourceType: ResourceType,
    val url: String,
    val fileUrl: String?,
    val isActive: Boolean,
    val createdAt: String
)

data class TeacherResourcePayload(
    val classroomId: Int,
    val title: String,
    val description: String,
    val resourceType: String,
    val url: String,
    val isActive: Boolean
)

// ─── Teacher Stats ────────────────────────────────────────────────────────────

data class TeacherStats(
    val totalClassrooms: Int,
    val totalStudents: Int,
    val activeExams: Int,
    val totalResources: Int,
    val averageScore: Double
)
