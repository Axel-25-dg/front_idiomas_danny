package com.ute.guamanidiomas.domain.model

data class Module(
    val id: Int,
    val courseId: Int,
    val courseTitle: String,
    val title: String,
    val description: String,
    val order: Int,
    val isActive: Boolean
)

data class ModulePayload(
    val courseId: Int,
    val title: String,
    val description: String,
    val order: Int,
    val isActive: Boolean
)
