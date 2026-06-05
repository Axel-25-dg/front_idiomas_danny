package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.ExerciseApi
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExercisePayload
import com.ute.guamanidiomas.domain.repository.ExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val api: ExerciseApi
) : ExerciseRepository {

    override suspend fun getExercises(): Result<List<Exercise>> = runCatching {
        val response = api.getExercises()
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun getExercisesByModule(moduleId: Int): Result<List<Exercise>> = runCatching {
        val response = api.getExercisesByModule(moduleId)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun getExerciseById(id: Int): Result<Exercise> = runCatching {
        val response = api.getExerciseById(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun createExercise(payload: ExercisePayload): Result<Exercise> = runCatching {
        val response = api.createExercise(payload)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun updateExercise(id: Int, payload: ExercisePayload): Result<Exercise> = runCatching {
        val response = api.updateExercise(id, payload)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun deleteExercise(id: Int): Result<Unit> = runCatching {
        val response = api.deleteExercise(id)
        if (!response.isSuccessful) throw Exception("Error ${response.code()}")
    }
}
