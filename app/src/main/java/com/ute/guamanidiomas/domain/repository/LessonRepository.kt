package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.Lesson
import com.ute.guamanidiomas.domain.model.LessonPayload

interface LessonRepository {
    suspend fun getAllLessons(): Result<List<Lesson>>
    suspend fun getLessonsByModule(moduleId: Int): Result<List<Lesson>>
    suspend fun getLessonById(id: Int): Result<Lesson>
    suspend fun createLesson(payload: LessonPayload): Result<Lesson>
    suspend fun updateLesson(id: Int, payload: LessonPayload): Result<Lesson>
    suspend fun deleteLesson(id: Int): Result<Unit>
}
