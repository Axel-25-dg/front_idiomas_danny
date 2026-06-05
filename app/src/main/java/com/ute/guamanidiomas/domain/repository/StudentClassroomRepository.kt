package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.domain.model.teacher.TeacherResource

interface StudentClassroomRepository {
    suspend fun getMyClassrooms(): Result<List<Classroom>>
    suspend fun getClassroomById(id: Int): Result<Classroom>
    suspend fun getClassroomResources(classroomId: Int? = null): Result<List<TeacherResource>>
    suspend fun leaveClassroom(classroomId: Int): Result<Unit>
}
