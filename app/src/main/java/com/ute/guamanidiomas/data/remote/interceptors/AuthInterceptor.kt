package com.ute.guamanidiomas.data.remote.interceptors

import android.util.Log
import com.ute.guamanidiomas.data.local.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path.endsWith("login/") || path.contains("/auth/login") ||
            path.endsWith("register/") || path.contains("/auth/register") ||
            path.contains("token/refresh/")) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenDataStore.getAccessToken() }

        val authenticatedRequest = request.newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
        }.build()

        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401 && token != null) {
            response.close()

            val refreshToken = runBlocking { tokenDataStore.getRefreshToken() }
            if (refreshToken != null) {
                val newAccessToken = refreshAccessToken(chain, refreshToken)
                if (newAccessToken != null) {
                    runBlocking { tokenDataStore.saveTokens(newAccessToken, refreshToken) }

                    val retryRequest = request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(retryRequest)
                }
            }

            runBlocking { tokenDataStore.clearSession() }
            return chain.proceed(authenticatedRequest)
        }

        return response
    }

    private fun refreshAccessToken(chain: Interceptor.Chain, refreshToken: String): String? {
        return try {
            val baseUrl = chain.request().url.scheme + "://" + chain.request().url.host
            val port = if (chain.request().url.port != 80 && chain.request().url.port != 443)
                ":${chain.request().url.port}" else ""

            val refreshUrl = "$baseUrl$port/api/auth/token/refresh/"
            val body = JSONObject().put("refresh", refreshToken).toString()
                .toRequestBody("application/json".toMediaType())

            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post(body)
                .build()

            val refreshResponse = chain.proceed(refreshRequest)

            if (refreshResponse.isSuccessful) {
                val responseBody = refreshResponse.body?.string()
                refreshResponse.close()
                val json = JSONObject(responseBody ?: "")
                json.optString("access", null)
            } else {
                refreshResponse.close()
                Log.d("AuthInterceptor", "Token refresh failed: ${refreshResponse.code}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Error refreshing token: ${e.message}")
            null
        }
    }
}