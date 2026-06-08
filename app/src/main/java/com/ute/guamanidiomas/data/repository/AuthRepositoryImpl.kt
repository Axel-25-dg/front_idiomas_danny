package com.ute.guamanidiomas.data.repository

import android.util.Log
import com.google.gson.Gson
import com.ute.guamanidiomas.data.local.TokenDataStore
import com.ute.guamanidiomas.data.remote.api.AuthApi
import com.ute.guamanidiomas.data.remote.dto.LoginRequest
import com.ute.guamanidiomas.data.remote.dto.LogoutRequest
import com.ute.guamanidiomas.data.remote.dto.RegisterRequest
import com.ute.guamanidiomas.domain.model.LoggedUser
import com.ute.guamanidiomas.domain.repository.AuthRepository
import com.ute.guamanidiomas.util.ErrorUtils
import com.ute.guamanidiomas.util.JwtDecoder
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoggedUser> =
        runCatching {
            val response = api.login(LoginRequest(email, password))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: ""
                error(ErrorUtils.parseErrorMessage(errorBody, response.code()))
            }
            val body = response.body() ?: throw Exception("Respuesta vacía del servidor")

            val claims  = JwtDecoder.getClaims(body.access)
            val userId  = claims?.optInt("user_id", 0) ?: 0

            val role = claims
                ?.takeIf { it.has("role") && !it.isNull("role") }
                ?.optString("role", "student")
                ?.lowercase()
                ?.trim()
                ?: "student"

            val isStaff    = claims?.optBoolean("is_staff", false) ?: false
            val isSuperuser = claims?.optBoolean("is_superuser", false) ?: false

            Log.d("ROLE_DEBUG", "Login → role=$role  is_staff=$isStaff  is_superuser=$isSuperuser")

            tokenDataStore.saveTokens(body.access, body.refresh)
            tokenDataStore.saveUser(
                id       = userId,
                username = email.substringBefore("@"),
                email    = email,
                isStaff  = false,
                role     = role
            )

            LoggedUser(
                id       = userId,
                username = email.substringBefore("@"),
                email    = email,
                isStaff  = false,
                role     = role
            )
        }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        password2: String,
    ): Result<LoggedUser> = runCatching {
        val response = api.register(RegisterRequest(username, email, password, password2))
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: ""
            error(ErrorUtils.parseErrorMessage(errorBody, response.code()))
        }
        val body = response.body() ?: throw Exception("Respuesta vacía del servidor")

        val registeredUser = body.user
        val userId         = registeredUser?.id ?: 0
        val usernameStr    = registeredUser?.username ?: username
        val emailStr       = registeredUser?.email ?: email

        val loginResponse = api.login(LoginRequest(email, password))
        if (!loginResponse.isSuccessful) {
            error("Registro exitoso pero no se pudo iniciar sesión automáticamente.")
        }
        val loginBody = loginResponse.body() ?: throw Exception("Error al obtener tokens")

        val claims = JwtDecoder.getClaims(loginBody.access)
        val role   = claims
            ?.takeIf { it.has("role") && !it.isNull("role") }
            ?.optString("role", "student")
            ?.lowercase()
            ?.trim()
            ?: registeredUser?.role?.name?.lowercase()?.trim()
            ?: "student"

        val isStaff    = claims?.optBoolean("is_staff", false) ?: false
        val isSuperuser = claims?.optBoolean("is_superuser", false) ?: false

        Log.d("ROLE_DEBUG", "Register → role=$role  is_staff=$isStaff  is_superuser=$isSuperuser")

        tokenDataStore.saveTokens(loginBody.access, loginBody.refresh)
        tokenDataStore.saveUser(
            id       = userId,
            username = usernameStr,
            email    = emailStr,
            isStaff  = false,
            role     = role
        )

        LoggedUser(
            id       = userId,
            username = usernameStr,
            email    = emailStr,
            isStaff  = false,
            role     = role
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        val refresh = tokenDataStore.getRefreshToken()
        if (refresh != null) {
            runCatching { api.logout(LogoutRequest(refresh)) }
        }
        tokenDataStore.clearSession()
    }

    override suspend fun getStoredUser(): TokenDataStore.UserSnapshot? =
        tokenDataStore.userSnapshot.first()

    override suspend fun isLoggedIn(): Boolean =
        !tokenDataStore.getAccessToken().isNullOrBlank()
}