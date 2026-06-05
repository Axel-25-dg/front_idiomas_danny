package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.ClassroomApi
import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.domain.model.teacher.ResourceType
import com.ute.guamanidiomas.domain.model.teacher.TeacherResource
import com.ute.guamanidiomas.domain.repository.StudentClassroomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentClassroomRepositoryImpl @Inject constructor(
    private val api: ClassroomApi
) : StudentClassroomRepository {

    override suspend fun getMyClassrooms(): Result<List<Classroom>> = runCatching {
        val response = api.getMyClassrooms()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun getClassroomById(id: Int): Result<Classroom> = runCatching {
        val response = api.getClassroomById(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun getClassroomResources(classroomId: Int?): Result<List<TeacherResource>> = runCatching {
        val response = api.getClassroomResources(classroomId)
        if (response.isSuccessful) {
            response.body()?.map { dto ->
                TeacherResource(
                    id            = dto.id,
                    classroomId   = dto.classroomId,
                    classroomName = dto.classroomName,
                    title         = dto.title,
                    description   = dto.description,
                    resourceType  = ResourceType.fromString(dto.resourceType),
                    url           = dto.url,
                    fileUrl       = dto.fileUrl,
                    isActive      = dto.isActive,
                    createdAt     = dto.createdAt
                )
            } ?: emptyList()
        } else throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun leaveClassroom(classroomId: Int): Result<Unit> = runCatching {
        val response = api.leaveClassroom(classroomId)
        if (!response.isSuccessful) throw Exception(apiError(response.code(), response.errorBody()?.string()))
    }

    private fun apiError(code: Int, body: String?): String =
        "Error $code${body?.takeIf { it.isNotBlank() }?.let { ": $it" } ?: ""}"
}
