package com.ute.guamanidiomas.domain.model.teacher

data class Classroom(
    val id: Int = 0,
    val courseId: Int = 0,
    val courseTitle: String? = null,
    val name: String? = null,
    val description: String? = null,
    val accessCode: String? = null,
    val teacherId: Int = 0,
    val teacherName: String? = null,
    val studentCount: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

data class ClassroomPayload(
    val courseId: Int,
    val name: String,
    val description: String,
    val isActive: Boolean = true
)

data class Enrollment(
    val id: Int = 0,
    val classroomId: Int = 0,
    val classroomName: String? = null,
    val studentId: Int = 0,
    val studentEmail: String? = null,
    val studentName: String? = null,
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val modulesCompleted: Int = 0,
    val enrolledAt: String? = null
)

data class Exam(
    val id: Int = 0,
    val classroomId: Int = 0,
    val classroomName: String? = null,
    val title: String? = null,
    val description: String? = null,
    val timeLimitMinutes: Int = 0,
    val passingScore: Int = 0,
    val autoGrade: Boolean = true,
    val startDate: String? = null,
    val endDate: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val submissionCount: Int = 0
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

data class ExamResult(
    val id: Int = 0,
    val examId: Int = 0,
    val examTitle: String? = null,
    val studentId: Int = 0,
    val studentEmail: String? = null,
    val studentName: String? = null,
    val score: Int = 0,
    val passed: Boolean = false,
    val submittedAt: String? = null
)

enum class ResourceType(val value: String, val label: String) {
    PDF("pdf", "PDF"),
    AUDIO("audio", "Audio"),
    VIDEO("video", "Video"),
    WORD("word", "Word"),
    IMAGE("image", "Imagen"),
    LINK("link", "Enlace"),
    OTHER("other", "Otro");

    companion object {
        fun fromString(value: String): ResourceType =
            entries.firstOrNull { it.value == value.lowercase() } ?: LINK
    }
}

data class TeacherResource(
    val id: Int = 0,
    val classroomId: Int = 0,
    val classroomName: String? = null,
    val title: String? = null,
    val description: String? = null,
    val resourceType: ResourceType = ResourceType.LINK,
    val url: String? = null,
    val fileUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

data class TeacherResourcePayload(
    val title: String,
    val description: String,
    val resourceType: String,
    val fileUrl: String,
    val courseId: Int? = null,
    val lessonId: Int? = null,
    val isPublic: Boolean = true
)

data class TeacherStats(
    val totalClassrooms: Int,
    val totalStudents: Int,
    val activeExams: Int,
    val totalResources: Int,
    val averageScore: Double
)