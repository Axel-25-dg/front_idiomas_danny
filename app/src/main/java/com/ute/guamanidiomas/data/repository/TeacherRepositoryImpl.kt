package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.TeacherClassroomApi
import com.ute.guamanidiomas.data.remote.api.TeacherExamApi
import com.ute.guamanidiomas.data.remote.api.TeacherResourceApi
import com.ute.guamanidiomas.data.remote.dto.ClassroomRequest
import com.ute.guamanidiomas.data.remote.dto.ExamRequest
import com.ute.guamanidiomas.data.remote.dto.TeacherResourceRequest
import com.ute.guamanidiomas.data.remote.dto.TeacherStatsDto
import com.ute.guamanidiomas.domain.model.teacher.*
import com.ute.guamanidiomas.domain.repository.TeacherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepositoryImpl @Inject constructor(
    private val classroomApi: TeacherClassroomApi,
    private val examApi: TeacherExamApi,
    private val resourceApi: TeacherResourceApi
) : TeacherRepository {

    // ── Stats ─────────────────────────────────────────────────────────────────

    override suspend fun getTeacherStats(): Result<TeacherStats> = runCatching {
        val response = classroomApi.getTeacherStats()
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: TeacherStats(0, 0, 0, 0, 0.0)
        } else {
            // Fallback: construir stats desde classrooms si teacher/stats/ no existe
            try {
                val classrooms = classroomApi.getClassrooms().body()?.results ?: emptyList()
                val exams      = try { examApi.getExams().body()?.results ?: emptyList() } catch (_: Exception) { emptyList() }
                val resources  = try { resourceApi.getResources().body()?.results ?: emptyList() } catch (_: Exception) { emptyList() }
                val totalStudents = classrooms.sumOf { it.studentCount }
                val activeExams   = exams.count { it.isActive }
                TeacherStats(
                    totalClassrooms = classrooms.size,
                    totalStudents   = totalStudents,
                    activeExams     = activeExams,
                    totalResources  = resources.size,
                    averageScore    = 0.0
                )
            } catch (_: Exception) {
                TeacherStats(0, 0, 0, 0, 0.0)
            }
        }
    }

    // ── Classrooms ────────────────────────────────────────────────────────────

    override suspend fun getClassrooms(): Result<List<Classroom>> = runCatching {
        val response = classroomApi.getClassrooms()
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception(apiError(response.code(), errorBody))
        }
    }

    override suspend fun getClassroomById(id: Int): Result<Classroom> = runCatching {
        val response = classroomApi.getClassroomById(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun createClassroom(payload: ClassroomPayload): Result<Classroom> = runCatching {
        val request = ClassroomRequest(
            courseId    = payload.courseId,
            name        = payload.name,
            description = payload.description,
            isActive    = payload.isActive
        )
        val response = classroomApi.createClassroom(request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun updateClassroom(id: Int, payload: ClassroomPayload): Result<Classroom> = runCatching {
        val request = ClassroomRequest(
            courseId    = payload.courseId,
            name        = payload.name,
            description = payload.description,
            isActive    = payload.isActive
        )
        val response = classroomApi.updateClassroom(id, request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun deleteClassroom(id: Int): Result<Unit> = runCatching {
        val response = classroomApi.deleteClassroom(id)
        if (!response.isSuccessful) throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    // ── Enrollments ───────────────────────────────────────────────────────────

    override suspend fun getEnrollments(classroomId: Int): Result<List<Enrollment>> = runCatching {
        val response = classroomApi.getEnrollments(classroomId)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            // Fallback: si /enrollments/ no existe, devolver lista vacía sin crashear
            emptyList()
        }
    }

    override suspend fun removeStudent(classroomId: Int, studentId: Int): Result<Unit> = runCatching {
        val response = classroomApi.removeStudent(classroomId, mapOf("student_id" to studentId))
        if (!response.isSuccessful) throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    // ── Exams ─────────────────────────────────────────────────────────────────

    override suspend fun getExams(classroomId: Int?): Result<List<Exam>> = runCatching {
        val response = examApi.getExams(classroomId = classroomId)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun getExamById(id: Int): Result<Exam> = runCatching {
        val response = examApi.getExamById(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun createExam(payload: ExamPayload): Result<Exam> = runCatching {
        val request = ExamRequest(
            classroomId      = payload.classroomId,
            title            = payload.title,
            description      = payload.description,
            timeLimitMinutes = payload.timeLimitMinutes,
            passingScore     = payload.passingScore,
            autoGrade        = payload.autoGrade,
            startDate        = payload.startDate,
            endDate          = payload.endDate,
            isActive         = payload.isActive
        )
        val response = examApi.createExam(request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun updateExam(id: Int, payload: ExamPayload): Result<Exam> = runCatching {
        val request = ExamRequest(
            classroomId      = payload.classroomId,
            title            = payload.title,
            description      = payload.description,
            timeLimitMinutes = payload.timeLimitMinutes,
            passingScore     = payload.passingScore,
            autoGrade        = payload.autoGrade,
            startDate        = payload.startDate,
            endDate          = payload.endDate,
            isActive         = payload.isActive
        )
        val response = examApi.updateExam(id, request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun deleteExam(id: Int): Result<Unit> = runCatching {
        val response = examApi.deleteExam(id)
        if (!response.isSuccessful) throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun getExamResults(examId: Int): Result<List<ExamResult>> = runCatching {
        val response = examApi.getExamResults(examId)
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    // ── Resources ─────────────────────────────────────────────────────────────

    override suspend fun getResources(classroomId: Int?): Result<List<TeacherResource>> = runCatching {
        val response = resourceApi.getResources(classroomId)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            // Si el endpoint no existe en el backend, devolver lista vacía sin crashear
            emptyList()
        }
    }

    override suspend fun createResource(payload: TeacherResourcePayload): Result<TeacherResource> = runCatching {
        val request = TeacherResourceRequest(
            classroomId  = payload.classroomId,
            title        = payload.title,
            description  = payload.description,
            resourceType = payload.resourceType,
            url          = payload.url,
            isActive     = payload.isActive
        )
        val response = resourceApi.createResource(request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun updateResource(id: Int, payload: TeacherResourcePayload): Result<TeacherResource> = runCatching {
        val request = TeacherResourceRequest(
            classroomId  = payload.classroomId,
            title        = payload.title,
            description  = payload.description,
            resourceType = payload.resourceType,
            url          = payload.url,
            isActive     = payload.isActive
        )
        val response = resourceApi.updateResource(id, request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun deleteResource(id: Int): Result<Unit> = runCatching {
        val response = resourceApi.deleteResource(id)
        if (!response.isSuccessful) throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun apiError(code: Int, body: String?): String =
        "Error $code${body?.takeIf { it.isNotBlank() }?.let { ": $it" } ?: ""}"
}
