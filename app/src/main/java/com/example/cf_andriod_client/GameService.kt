package com.example.cf_andriod_client

import com.auth0.android.jwt.JWT

class GameService {

    private val connectionManager = ConnectionService()
    private var loginToken: JWT? = null

    fun isLoggedIn(): Boolean {
        return loginToken != null && !loginToken!!.isExpired(0)
    }

    suspend fun loggIn(credentials: Login) {
        loginToken = connectionManager.fetchToken(credentials, loginUrl)
    }

    suspend fun createAccount(credentials: Login) {
        loginToken = connectionManager.fetchToken(credentials, createAccountUrl)
    }

    fun loggOut() {
        loginToken = null
    }

    fun getUsername(): String {
        return try {
            loginToken!!.getClaim("username").asString() ?: "Unknown username"
        } catch (e: Exception) {
            "Unknown username"
        }
    }
}