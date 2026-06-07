package com.ute.guamanidiomas.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ute.guamanidiomas.data.remote.api.ModuleApi
import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.domain.model.ModulePayload
import com.ute.guamanidiomas.domain.repository.ModuleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleRepositoryImpl @Inject constructor(
    private val api: ModuleApi
) : ModuleRepository {

    private val gson = Gson()

    override suspend fun getModules(): Result<List<Module>> = runCatching {
        val response = api.getModules()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun getModulesByCourse(courseId: Int): Result<List<Module>> = runCatching {
        val response = api.getModulesByCourse(courseId)
        if (response.isSuccessful) {
            val json = response.body() ?: return@runCatching emptyList()
            // El backend puede devolver una lista directa [...] o paginado {count, results: [...]}
            val modulesList = if (json.isJsonArray) {
                val type = object : TypeToken<List<Module>>() {}.type
                gson.fromJson<List<Module>>(json, type)
            } else if (json.isJsonObject) {
                val obj = json.asJsonObject
                val resultsElement = obj.get("results")
                if (resultsElement != null && resultsElement.isJsonArray) {
                    val type = object : TypeToken<List<Module>>() {}.type
                    gson.fromJson<List<Module>>(resultsElement, type)
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
            modulesList ?: emptyList()
        } else throw Exception("Error ${response.code()}")
    }

    override suspend fun getModuleById(id: Int): Result<Module> = runCatching {
        val response = api.getModuleById(id)
        if (response.isSuccessful) response.body() ?: throw Exception("Módulo no encontrado")
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun createModule(payload: ModulePayload): Result<Module> = runCatching {
        val response = api.createModule(payload)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun updateModule(id: Int, payload: ModulePayload): Result<Module> = runCatching {
        val response = api.updateModule(id, payload)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun deleteModule(id: Int): Result<Unit> = runCatching {
        val response = api.deleteModule(id)
        if (!response.isSuccessful) throw Exception("Error ${response.code()}")
    }
}
