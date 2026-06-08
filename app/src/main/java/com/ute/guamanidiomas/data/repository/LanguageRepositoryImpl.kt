package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.LanguageApi
import com.ute.guamanidiomas.domain.model.Language
import com.ute.guamanidiomas.domain.model.LanguagePayload
import com.ute.guamanidiomas.domain.repository.LanguageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepositoryImpl @Inject constructor(
    private val api: LanguageApi
) : LanguageRepository {

    override suspend fun getLanguages(): Result<List<Language>> = runCatching {
        val response = api.getLanguages()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun getLanguageById(id: Int): Result<Language> = runCatching {
        val response = api.getLanguageById(id)
        if (response.isSuccessful) response.body() ?: throw Exception("Not found")
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun createLanguage(payload: LanguagePayload): Result<Language> = runCatching {
        val response = api.createLanguage(payload)
        if (response.isSuccessful) response.body() ?: throw Exception("Empty response")
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun updateLanguage(id: Int, payload: LanguagePayload): Result<Language> = runCatching {
        val response = api.updateLanguage(id, payload)
        if (response.isSuccessful) response.body() ?: throw Exception("Empty response")
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun deleteLanguage(id: Int): Result<Unit> = runCatching {
        val response = api.deleteLanguage(id)
        if (!response.isSuccessful) throw Exception("Error ${response.code()}")
    }
}