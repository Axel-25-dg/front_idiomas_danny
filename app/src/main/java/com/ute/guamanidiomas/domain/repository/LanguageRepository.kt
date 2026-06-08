package com.ute.guamanidiomas.domain.repository

import com.ute.guamanidiomas.domain.model.Language
import com.ute.guamanidiomas.domain.model.LanguagePayload

interface LanguageRepository {
    suspend fun getLanguages(): Result<List<Language>>
    suspend fun getLanguageById(id: Int): Result<Language>
    suspend fun createLanguage(payload: LanguagePayload): Result<Language>
    suspend fun updateLanguage(id: Int, payload: LanguagePayload): Result<Language>
    suspend fun deleteLanguage(id: Int): Result<Unit>
}