package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExercisePayload

interface ExerciseRepository {
    suspend fun getExercises(): Result<List<Exercise>>
    suspend fun getExercisesByModule(moduleId: Int): Result<List<Exercise>>
    suspend fun getExerciseById(id: Int): Result<Exercise>
    suspend fun createExercise(payload: ExercisePayload): Result<Exercise>
    suspend fun updateExercise(id: Int, payload: ExercisePayload): Result<Exercise>
    suspend fun deleteExercise(id: Int): Result<Unit>
}
