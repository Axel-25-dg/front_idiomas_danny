package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.GamificationApi
import com.ute.guamanidiomas.data.remote.dto.DjangoPage
import com.ute.guamanidiomas.data.remote.dto.PostProgressRequestDto
import com.ute.guamanidiomas.domain.model.Achievement
import com.ute.guamanidiomas.domain.model.LessonProgress
import com.ute.guamanidiomas.domain.model.StudentStats
import com.ute.guamanidiomas.domain.model.UserAchievement
import com.ute.guamanidiomas.domain.repository.GamificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationRepositoryImpl @Inject constructor(
    private val api: GamificationApi
) : GamificationRepository {

    override suspend fun getMyStats(): Result<List<StudentStats>> = runCatching {
        val response = api.getMyStats()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception("Error ${response.code()}: ${response.message()}")
    }

    override suspend fun postProgress(lessonId: Int, score: Int): Result<LessonProgress> = runCatching {
        val response = api.postProgress(PostProgressRequestDto(lessonId = lessonId, score = score))
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Respuesta vacía")
        } else {
            val errorBody = response.errorBody()?.string() ?: ""
            throw Exception("Error ${response.code()}: $errorBody")
        }
    }

    override suspend fun getProgressHistory(page: Int): Result<DjangoPage<LessonProgress>> = runCatching {
        val response = api.getProgressHistory(page = page)
        if (response.isSuccessful) {
            val raw = response.body() ?: throw Exception("Respuesta vacía")
            DjangoPage(
                count    = raw.count,
                next     = raw.next,
                previous = raw.previous,
                results  = raw.results.map { it.toDomain() }
            )
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getAllAchievements(): Result<List<Achievement>> = runCatching {
        val response = api.getAllAchievements()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getMyAchievements(): Result<List<UserAchievement>> = runCatching {
        val response = api.getMyAchievements()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }
}
