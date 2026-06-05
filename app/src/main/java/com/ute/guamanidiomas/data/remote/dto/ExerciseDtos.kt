package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExerciseType

data class ExerciseDto(
    val id: Int,
    @SerializedName("module") val moduleId: Int,
    val question: String,
    @SerializedName("exercise_type") val type: String,
    @SerializedName("context_data") val contextData: String = "",
    @SerializedName("correct_answer") val correctAnswer: String = "",
    @SerializedName("xp_reward") val xpReward: Int = 10,
    @SerializedName("is_active") val isActive: Boolean = true,
    val order: Int = 0
) {
    fun toDomain() = Exercise(
        id            = id,
        moduleId      = moduleId,
        question      = question,
        type          = ExerciseType.fromString(type),
        contextData   = contextData,
        correctAnswer = correctAnswer,
        xpReward      = xpReward,
        isActive      = isActive
    )
}

data class ExerciseRequestDto(
    @SerializedName("module") val moduleId: Int,
    val question: String,
    @SerializedName("exercise_type") val type: String,
    @SerializedName("context_data") val contextData: String = "",
    @SerializedName("correct_answer") val correctAnswer: String = "",
    @SerializedName("xp_reward") val xpReward: Int = 10,
    @SerializedName("is_active") val isActive: Boolean = true,
    val order: Int = 0
)

data class ModuleWithExercisesDto(
    val id: Int,
    @SerializedName("course") val courseId: Int,
    @SerializedName("course_title") val courseTitle: String = "",
    val title: String,
    val description: String = "",
    val order: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true,
    val exercises: List<ExerciseDto> = emptyList()
)
