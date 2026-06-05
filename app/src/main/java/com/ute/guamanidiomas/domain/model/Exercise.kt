package com.ute.guamanidiomas.domain.model

enum class ExerciseType(val value: String) {
    MULTIPLE_CHOICE("multiple_choice"),
    TRANSLATION("translation"),
    LISTENING("listening");

    companion object {
        fun fromString(value: String): ExerciseType = when (value.lowercase().trim()) {
            "multiple_choice", "multiplechoice", "choice" -> MULTIPLE_CHOICE
            "translation", "translate"                    -> TRANSLATION
            "listening", "listen"                         -> LISTENING
            else                                          -> MULTIPLE_CHOICE
        }
    }
}

data class Exercise(
    val id: Int,
    val moduleId: Int,
    val question: String,
    val type: ExerciseType,
    val contextData: String,
    val correctAnswer: String,
    val xpReward: Int,
    val isActive: Boolean
)

data class ExercisePayload(
    val moduleId: Int,
    val question: String,
    val type: String,
    val contextData: String,
    val xpReward: Int,
    val isActive: Boolean
)
