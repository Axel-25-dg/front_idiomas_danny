package com.ute.guamanidiomas.data.repository

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

    override suspend fun getModules(): Result<List<Module>> = runCatching {
        val response = api.getModules()
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun getModulesByCourse(courseId: Int): Result<List<Module>> = runCatching {
        val response = api.getModulesByCourse(courseId)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error ${response.code()}")
    }

    override suspend fun getModuleById(id: Int): Result<Module> = runCatching {
        val response = api.getModuleById(id)
        if (response.isSuccessful) response.body()!!
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
