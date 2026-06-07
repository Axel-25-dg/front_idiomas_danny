package com.ute.guamanidiomas.util

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {
    private val gson = Gson()

    fun parseError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> when (throwable) {
                is UnknownHostException, is ConnectException -> "No se pudo conectar al servidor. Verifica tu conexión."
                is SocketTimeoutException -> "Tiempo de espera agotado. El servidor tarda demasiado en responder."
                else -> "Error de red: ${throwable.localizedMessage ?: "Error desconocido"}"
            }
            is HttpException -> {
                val errorBody = throwable.response()?.errorBody()?.string()
                val code = throwable.code()
                parseErrorMessage(errorBody, code)
            }
            else -> throwable.localizedMessage ?: "Ha ocurrido un error inesperado"
        }
    }

    fun parseErrorMessage(body: String?, code: Int): String {
        if (body.isNullOrBlank()) return "Error $code"
        
        return try {
            val map = gson.fromJson(body, Map::class.java)
            
            // 1. Buscar "detail" (usado por Django Rest Framework para errores generales)
            map["detail"]?.toString()
                // 2. Buscar "non_field_errors"
                ?: map["non_field_errors"]?.let { errors ->
                    if (errors is List<*>) errors.joinToString(", ")
                    else errors.toString()
                }
                // 3. Buscar errores por campo (e.g. {"email": ["Ya existe un usuario..."]})
                ?: map.entries.firstOrNull { it.key !in setOf("detail", "non_field_errors") }
                    ?.let { entry ->
                        val value = entry.value
                        val msg = if (value is List<*>) value.joinToString(", ") else value.toString()
                        // Capitalizar la clave para que se vea mejor (e.g. "Email: ...")
                        val field = entry.key.toString().replaceFirstChar { it.uppercase() }
                        "$field: $msg"
                    }
                ?: "Error $code"
        } catch (e: Exception) {
            // Si no es JSON, intentar limpiar HTML si el backend devolvió una página de error
            if (body.contains("<!DOCTYPE html>", ignoreCase = true)) {
                "Error del servidor ($code). Por favor, intenta más tarde."
            } else {
                body.take(100) // Mostrar el inicio del error si no es JSON
            }
        }
    }
}
