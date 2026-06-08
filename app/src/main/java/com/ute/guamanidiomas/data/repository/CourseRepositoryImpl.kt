package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.CourseApi
import com.ute.guamanidiomas.data.remote.dto.toRequest
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.CoursePayload
import com.ute.guamanidiomas.domain.repository.CourseFilters
import com.ute.guamanidiomas.domain.repository.CourseRepository
import com.ute.guamanidiomas.util.ErrorUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val api: CourseApi
) : CourseRepository {

    override suspend fun getCourses(filters: CourseFilters): Result<Pair<List<Course>, Int>> = runCatching {
        try {
            val params = buildMap<String, String> {
                filters.search?.let { put("search", it) }
                filters.languageId?.let { put("language", it.toString()) }
                filters.level?.let { put("level", it) }
                filters.isActive?.let { put("is_active", it.toString()) }
                put("page", filters.page.toString())
                put("page_size", filters.pageSize.toString())
            }
            val response = api.getCourses(params)
            if (response.isSuccessful) {
                val body = response.body()!!
                Pair(body.results, body.count)
            } else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun getCourseById(id: Int): Result<Course> = runCatching {
        try {
            val response = api.getCourseById(id)
            if (response.isSuccessful) response.body() ?: throw Exception("El servidor devolvió un cuerpo vacío")
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun createCourse(payload: CoursePayload): Result<Course> = runCatching {
        try {
            val response = api.createCourse(payload.toRequest())
            if (response.isSuccessful) response.body()!!
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun updateCourse(id: Int, payload: CoursePayload): Result<Course> = runCatching {
        try {
            val response = api.updateCourse(id, payload.toRequest())
            if (response.isSuccessful) response.body()!!
            else throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun deleteCourse(id: Int): Result<Unit> = runCatching {
        try {
            val response = api.deleteCourse(id)
            if (!response.isSuccessful) throw Exception(ErrorUtils.parseErrorMessage(response.errorBody()?.string(), response.code()))
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }

    override suspend fun joinClass(accessCode: String): Result<Course> = runCatching {
        try {
            val response = api.joinClass(mapOf("access_code" to accessCode))
            if (response.isSuccessful) {
                val body = response.body() ?: emptyMap()
                val id = (body["id"] as? Number)?.toInt()
                    ?: (body["classroom"] as? Number)?.toInt()
                    ?: 0
                val name = (body["name"] as? String)
                    ?: (body["message"] as? String)
                    ?: "Clase"
                Course(
                    id = id,
                    languageId = null,
                    title = name,
                    description = "",
                    level = "",
                    isActive = true
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                throw Exception(ErrorUtils.parseErrorMessage(errorBody, response.code()))
            }
        } catch (e: Exception) {
            throw Exception(ErrorUtils.parseError(e))
        }
    }
}