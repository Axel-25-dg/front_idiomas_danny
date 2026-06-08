package com.ute.guamanidiomas.domain.model

data class Progress(
    val id: Int,
    val userId: Int,
    val username: String,
    val totalXp: Int,
    val currentStreak: Int,
    val lastActiveDate: String,
    val modulesCompleted: Int
)