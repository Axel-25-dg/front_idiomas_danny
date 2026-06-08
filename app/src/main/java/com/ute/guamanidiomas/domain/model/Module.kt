package com.ute.guamanidiomas.domain.model

data class Module(
    val id: Int = 0,
    val courseId: Int = 0,
    val courseTitle: String? = null,
    val title: String? = null,
    val description: String? = null,
    val order: Int = 0,
    val isActive: Boolean = true
)

data class ModulePayload(
    val courseId: Int,
    val title: String,
    val description: String,
    val order: Int,
    val isActive: Boolean
)