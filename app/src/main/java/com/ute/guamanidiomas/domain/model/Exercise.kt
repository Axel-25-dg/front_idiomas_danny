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
    val id: Int = 0,
    val moduleId: Int = 0,
    val lessonId: Int = 0,
    val question: String? = null,
    val type: ExerciseType = ExerciseType.MULTIPLE_CHOICE,
    val contextData: String? = null,
    val correctAnswer: String? = null,
    val xpReward: Int = 0,
    val isActive: Boolean = true
)

data class ExercisePayload(
    val lessonId: Int,
    val questionText: String,
    val exerciseType: String,
    val correctAnswer: String,
    val xpReward: Int = 10
)
