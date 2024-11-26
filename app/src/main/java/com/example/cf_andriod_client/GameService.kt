package com.example.cf_andriod_client

import android.content.Context
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT

class GameService(private val appContext: Context) : ViewModel() {

    private val connectionManager = ConnectionService()
    private var loginToken: JWT? = null

    fun isLoggedIn(): Boolean {
        return loginToken != null && !loginToken!!.isExpired(0)
    }

    suspend fun loggIn(credentials: Login) {
        loginToken = connectionManager.fetchToken(credentials, loginUrl)
        DataService.saveToken(appContext, loginToken!!)
    }

    suspend fun loadToken() {
        loginToken = DataService.readToken(appContext)
    }

    suspend fun createAccount(credentials: Login) {
        loginToken = connectionManager.fetchToken(credentials, createAccountUrl)
    }

    suspend fun loggOut() {
        loginToken = null
        DataService.saveToken(appContext, JWT(""))
    }

    fun getUsername(): String {
        return try {
            loginToken!!.getClaim("username").asString() ?: "Unknown username"
        } catch (e: Exception) {
            "Unknown username"
        }
    }
}