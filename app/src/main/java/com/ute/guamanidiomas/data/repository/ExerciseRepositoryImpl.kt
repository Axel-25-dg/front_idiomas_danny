package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.ExerciseApi
import com.ute.guamanidiomas.data.remote.dto.ExerciseRequestDto
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExercisePayload
import com.ute.guamanidiomas.domain.repository.ExerciseRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val api: ExerciseApi
) : ExerciseRepository {

    override suspend fun getExercisesByLesson(lessonId: Int): Result<List<Exercise>> = runCatching {
        try {
            val response = api.getExercises(lessonId = lessonId)
            if (response.isSuccessful) {
                response.body()?.results?.map { it.toDomain() } ?: emptyList()
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getExercisesByModule(moduleId: Int): Result<List<Exercise>> = runCatching {
        try {
            val response = api.getExercisesByModule(moduleId)
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                if (response.code() == 404) emptyList()
                else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getExerciseById(id: Int): Result<Exercise> = runCatching {
        try {
            val response = api.getExerciseById(id)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun createExercise(payload: ExercisePayload): Result<Exercise> = runCatching {
        try {
            val request = ExerciseRequestDto(
                lessonId      = payload.lessonId,
                questionText  = payload.questionText,
                exerciseType  = payload.exerciseType,
                correctAnswer = payload.correctAnswer,
                xpReward      = payload.xpReward
            )
            val response = api.createExercise(request)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun deleteExercise(id: Int): Result<Unit> = runCatching {
        try {
            val response = api.deleteExercise(id)
            if (!response.isSuccessful) throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }
}
