package com.ute.guamanidiomas.data.repository

import com.ute.guamanidiomas.data.remote.api.CourseApi
import com.ute.guamanidiomas.data.remote.dto.toRequest
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.CoursePayload
import com.ute.guamanidiomas.domain.repository.CourseFilters
import com.ute.guamanidiomas.domain.repository.CourseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val api: CourseApi
) : CourseRepository {

    override suspend fun getCourses(filters: CourseFilters): Result<Pair<List<Course>, Int>> = runCatching {
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
        } else throw Exception(courseError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun getCourseById(id: Int): Result<Course> = runCatching {
        val response = api.getCourseById(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception(courseError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun createCourse(payload: CoursePayload): Result<Course> = runCatching {
        val response = api.createCourse(payload.toRequest())
        if (response.isSuccessful) response.body()!!
        else throw Exception(courseError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun updateCourse(id: Int, payload: CoursePayload): Result<Course> = runCatching {
        val response = api.updateCourse(id, payload.toRequest())
        if (response.isSuccessful) response.body()!!
        else throw Exception(courseError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun deleteCourse(id: Int): Result<Unit> = runCatching {
        val response = api.deleteCourse(id)
        if (!response.isSuccessful) throw Exception(courseError(response.code(), response.errorBody()?.string()))
    }

    override suspend fun joinClass(accessCode: String): Result<Course> = runCatching {
        val response = api.joinClass(mapOf("access_code" to accessCode))
        if (response.isSuccessful) response.body()!!
        else throw Exception(courseError(response.code(), response.errorBody()?.string()))
    }

    private fun courseError(code: Int, body: String?): String {
        return "Error $code${body?.takeIf { it.isNotBlank() }?.let { ": $it" } ?: ""}"
    }
}
