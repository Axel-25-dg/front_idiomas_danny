package com.ute.guamanidiomas.data.remote.interceptors

import com.ute.guamanidiomas.data.local.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
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
            path.endsWith("register/") || path.contains("/auth/register")) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenDataStore.getAccessToken() }

        android.util.Log.d("DEBUG_TOKEN", "Enviando token: $token a la URL: ${request.url}")

        val newRequest = request.newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
        }.build()

        return chain.proceed(newRequest)
    }
}