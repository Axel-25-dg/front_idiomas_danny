package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.LessonApi
import com.ute.guamanidiomas.domain.model.Lesson
import com.ute.guamanidiomas.domain.model.LessonPayload
import com.ute.guamanidiomas.domain.repository.LessonRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepositoryImpl @Inject constructor(
    private val api: LessonApi
) : LessonRepository {

    override suspend fun getAllLessons(): Result<List<Lesson>> = runCatching {
        try {
            val response = api.getLessons(moduleId = null, pageSize = 200)
            if (response.isSuccessful) {
                response.body()?.results?.map { it.toDomain() } ?: emptyList()
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getLessonsByModule(moduleId: Int): Result<List<Lesson>> = runCatching {
        try {
            val response = api.getLessons(moduleId = moduleId)
            if (response.isSuccessful) {
                response.body()?.results?.map { it.toDomain() } ?: emptyList()
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getLessonById(id: Int): Result<Lesson> = runCatching {
        try {
            val response = api.getLessonById(id)
            if (response.isSuccessful) {
                response.body()?.toDomain() ?: throw Exception("Respuesta vacía")
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun createLesson(payload: LessonPayload): Result<Lesson> = runCatching {
        try {
            val body = mapOf(
                "module" to payload.moduleId,
                "title" to payload.title,
                "content" to payload.content,
                "content_type" to payload.contentType,
                "order" to payload.order,
                "xp_reward" to payload.xpReward,
                "is_active" to payload.isActive
            )
            val response = api.createLesson(body)
            if (response.isSuccessful) {
                response.body()?.toDomain() ?: throw Exception("Respuesta vacía")
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun updateLesson(id: Int, payload: LessonPayload): Result<Lesson> = runCatching {
        try {
            val body = mapOf(
                "module" to payload.moduleId,
                "title" to payload.title,
                "content" to payload.content,
                "order" to payload.order,
                "xp_reward" to payload.xpReward,
                "is_active" to payload.isActive
            )
            val response = api.updateLesson(id, body)
            if (response.isSuccessful) {
                response.body()?.toDomain() ?: throw Exception("Respuesta vacía")
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun deleteLesson(id: Int): Result<Unit> = runCatching {
        try {
            val response = api.deleteLesson(id)
            if (!response.isSuccessful) throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }
}
