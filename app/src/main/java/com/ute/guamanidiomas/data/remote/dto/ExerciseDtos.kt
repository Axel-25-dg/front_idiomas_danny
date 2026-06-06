package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExerciseType

data class ExerciseDto(
    val id: Int = 0,
    @SerializedName("lesson") val lessonId: Int? = null,
    @SerializedName("lesson_title") val lessonTitle: String? = null,
    @SerializedName("module") val moduleId: Int? = null,
    @SerializedName("question_text") val questionText: String? = null,
    // Compatibilidad con campo viejo
    val question: String? = null,
    @SerializedName("exercise_type") val exerciseType: String? = null,
    @SerializedName("correct_answer") val correctAnswer: String? = null,
    @SerializedName("context_data") val contextData: String? = null,
    @SerializedName("xp_reward") val xpReward: Int = 10,
    @SerializedName("is_active") val isActive: Boolean = true,
    val order: Int = 0
) {
    fun toDomain() = Exercise(
        id            = id,
        moduleId      = moduleId ?: 0,
        lessonId      = lessonId ?: 0,
        question      = questionText ?: question.orEmpty(),
        type          = ExerciseType.fromString(exerciseType ?: "multiple_choice"),
        contextData   = contextData.orEmpty(),
        correctAnswer = correctAnswer.orEmpty(),
        xpReward      = xpReward,
        isActive      = isActive
    )
}

data class ExerciseRequestDto(
    @SerializedName("lesson") val lessonId: Int,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("exercise_type") val exerciseType: String = "multiple_choice",
    @SerializedName("correct_answer") val correctAnswer: String = "",
    @SerializedName("xp_reward") val xpReward: Int = 10,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class ExercisePageDto(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ExerciseDto>? = null
)
