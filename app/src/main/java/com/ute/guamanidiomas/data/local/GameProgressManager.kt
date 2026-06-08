package com.ute.guamanidiomas.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private val Context.gameDataStore by preferencesDataStore(name = "game_progress")

@Singleton
class GameProgressManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOTAL_XP = intPreferencesKey("total_xp")
        private val CURRENT_STREAK = intPreferencesKey("current_streak")
        private val LONGEST_STREAK = intPreferencesKey("longest_streak")
        private val LAST_PLAY_DATE = stringPreferencesKey("last_play_date")
        private val GAMES_COMPLETED = intPreferencesKey("games_completed")
        private val WORD_MATCH_LEVEL = intPreferencesKey("word_match_level")
        private val FLASHCARDS_LEVEL = intPreferencesKey("flashcards_level")
        private val SENTENCE_LEVEL = intPreferencesKey("sentence_level")
        private val QUIZ_LEVEL = intPreferencesKey("quiz_level")
        private val HANGMAN_LEVEL = intPreferencesKey("hangman_level")
        private val LISTENING_LEVEL = intPreferencesKey("listening_level")
        private val VERB_LEVEL = intPreferencesKey("verb_level")
    }

    val totalXp: Flow<Int> = context.gameDataStore.data.map { it[TOTAL_XP] ?: 0 }
    val currentStreak: Flow<Int> = context.gameDataStore.data.map { it[CURRENT_STREAK] ?: 0 }
    val longestStreak: Flow<Int> = context.gameDataStore.data.map { it[LONGEST_STREAK] ?: 0 }
    val gamesCompleted: Flow<Int> = context.gameDataStore.data.map { it[GAMES_COMPLETED] ?: 0 }
    val lastPlayDate: Flow<String> = context.gameDataStore.data.map { it[LAST_PLAY_DATE] ?: "" }

    fun getGameLevel(gameId: String): Flow<Int> = context.gameDataStore.data.map { prefs ->
        when (gameId) {
            "word_match"          -> prefs[WORD_MATCH_LEVEL] ?: 1
            "flashcards"          -> prefs[FLASHCARDS_LEVEL] ?: 1
            "sentence_builder"    -> prefs[SENTENCE_LEVEL] ?: 1
            "vocab_quiz"          -> prefs[QUIZ_LEVEL] ?: 1
            "hangman"             -> prefs[HANGMAN_LEVEL] ?: 1
            "listening_challenge" -> prefs[LISTENING_LEVEL] ?: 1
            "verb_conjugation"    -> prefs[VERB_LEVEL] ?: 1
            else -> 1
        }
    }

    data class StatsSnapshot(
        val totalXp: Int,
        val currentStreak: Int,
        val longestStreak: Int,
        val gamesCompleted: Int,
        val lastPlayDate: String
    )

    suspend fun getSnapshot(): StatsSnapshot {
        val prefs = context.gameDataStore.data.first()
        return StatsSnapshot(
            totalXp        = prefs[TOTAL_XP] ?: 0,
            currentStreak  = prefs[CURRENT_STREAK] ?: 0,
            longestStreak  = prefs[LONGEST_STREAK] ?: 0,
            gamesCompleted = prefs[GAMES_COMPLETED] ?: 0,
            lastPlayDate   = prefs[LAST_PLAY_DATE] ?: ""
        )
    }

    suspend fun getLevel(gameId: String): Int {
        return getGameLevel(gameId).first()
    }

    suspend fun addXp(xp: Int) {
        context.gameDataStore.edit { prefs ->
            val current = prefs[TOTAL_XP] ?: 0
            prefs[TOTAL_XP] = current + xp
        }
    }

    suspend fun recordGameCompleted(gameId: String, score: Int) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        context.gameDataStore.edit { prefs ->
            val currentXp = prefs[TOTAL_XP] ?: 0
            prefs[TOTAL_XP] = currentXp + score

            val completed = prefs[GAMES_COMPLETED] ?: 0
            prefs[GAMES_COMPLETED] = completed + 1

            val lastDate = prefs[LAST_PLAY_DATE] ?: ""
            val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

            val streak = prefs[CURRENT_STREAK] ?: 0
            val newStreak = when {
                lastDate == today -> streak
                lastDate == yesterday -> streak + 1
                else -> 1
            }
            prefs[CURRENT_STREAK] = newStreak

            val longest = prefs[LONGEST_STREAK] ?: 0
            if (newStreak > longest) prefs[LONGEST_STREAK] = newStreak

            prefs[LAST_PLAY_DATE] = today

            val levelKey = when (gameId) {
                "word_match"          -> WORD_MATCH_LEVEL
                "flashcards"          -> FLASHCARDS_LEVEL
                "sentence_builder"    -> SENTENCE_LEVEL
                "vocab_quiz"          -> QUIZ_LEVEL
                "hangman"             -> HANGMAN_LEVEL
                "listening_challenge" -> LISTENING_LEVEL
                "verb_conjugation"    -> VERB_LEVEL
                else -> null
            }
            if (levelKey != null && score >= 60) {
                val currentLevel = prefs[levelKey] ?: 1
                if (currentLevel < 10) {
                    prefs[levelKey] = currentLevel + 1
                }
            }
        }
    }

    suspend fun syncWithBackend(remoteXp: Int, remoteStreak: Int, remoteLongest: Int) {
        context.gameDataStore.edit { prefs ->
            val localXp = prefs[TOTAL_XP] ?: 0
            prefs[TOTAL_XP] = maxOf(localXp, remoteXp)
            prefs[CURRENT_STREAK] = maxOf(prefs[CURRENT_STREAK] ?: 0, remoteStreak)
            prefs[LONGEST_STREAK] = maxOf(prefs[LONGEST_STREAK] ?: 0, remoteLongest)
        }
    }
}