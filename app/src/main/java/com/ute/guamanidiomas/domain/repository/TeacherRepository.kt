package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.teacher.*

interface TeacherRepository {

    suspend fun getTeacherStats(): Result<TeacherStats>

    suspend fun getClassrooms(): Result<List<Classroom>>
    suspend fun getClassroomById(id: Int): Result<Classroom>
    suspend fun createClassroom(payload: ClassroomPayload): Result<Classroom>
    suspend fun updateClassroom(id: Int, payload: ClassroomPayload): Result<Classroom>
    suspend fun deleteClassroom(id: Int): Result<Unit>

    suspend fun getEnrollments(classroomId: Int): Result<List<Enrollment>>
    suspend fun removeStudent(classroomId: Int, studentId: Int): Result<Unit>

    suspend fun getExams(classroomId: Int? = null): Result<List<Exam>>
    suspend fun getExamById(id: Int): Result<Exam>
    suspend fun createExam(payload: ExamPayload): Result<Exam>
    suspend fun updateExam(id: Int, payload: ExamPayload): Result<Exam>
    suspend fun deleteExam(id: Int): Result<Unit>
    suspend fun getExamResults(examId: Int): Result<List<ExamResult>>

    suspend fun getResources(courseId: Int? = null): Result<List<TeacherResource>>
    suspend fun createResource(payload: TeacherResourcePayload): Result<TeacherResource>
    suspend fun updateResource(id: Int, payload: TeacherResourcePayload): Result<TeacherResource>
    suspend fun deleteResource(id: Int): Result<Unit>
}