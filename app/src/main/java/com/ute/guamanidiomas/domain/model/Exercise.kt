package com.ute.guamanidiomas.domain.model

enum class ExerciseType(val value: String) {
    MULTIPLE_CHOICE("multiple_choice"),
    TRANSLATION("translate"),
    LISTENING("listen"),
    FILL_BLANK("fill_blank"),
    MATCH("match");

    companion object {
        fun fromString(value: String): ExerciseType = when (value.lowercase().trim()) {
            "multiple_choice", "multiplechoice", "choice" -> MULTIPLE_CHOICE
            "translation", "translate"                    -> TRANSLATION
            "listening", "listen"                         -> LISTENING
            "fill_blank", "fill"                          -> FILL_BLANK
            "match"                                       -> MATCH
            else                                          -> MULTIPLE_CHOICE
        }
    }
}

data class Exercise(
    val id: Int,
    val moduleId: Int,
    val lessonId: Int = 0,
    val question: String,
    val type: ExerciseType,
    val contextData: String,
    val correctAnswer: String,
    val xpReward: Int,
    val isActive: Boolean
)

data class ExercisePayload(
    val lessonId: Int,
    val questionText: String,
    val exerciseType: String,
    val correctAnswer: String,
    val xpReward: Int = 10
)
