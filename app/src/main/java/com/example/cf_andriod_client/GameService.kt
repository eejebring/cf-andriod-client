package com.example.cf_andriod_client

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.MutableStateFlow

class GameService : ViewModel() {

    fun init(context: Context) {
        appContext = context
    }

    private var appContext: Context? = null
    private val connectionManager = ConnectionService()
    val loginToken: MutableState<JWT?> = mutableStateOf(null)
    val x = MutableStateFlow(2)


    fun isLoggedIn(): Boolean {
        return loginToken.value != null && !loginToken.value!!.isExpired(0)
    }

    suspend fun loggIn(credentials: Login) {
        loginToken.value = connectionManager.fetchToken(credentials, loginUrl)
        DataService.saveToken(appContext!!, loginToken.value!!)
    }

    suspend fun loadToken() {
        val loadedToken = DataService.readToken(appContext!!)
        if (loadedToken != loginToken.value)
            loginToken.value = loadedToken
    }

    suspend fun createAccount(credentials: Login) {
        loginToken.value = connectionManager.fetchToken(credentials, createAccountUrl)
        DataService.saveToken(appContext!!, loginToken.value!!)
    }

    suspend fun loggOut() {
        loginToken.value = null
        DataService.removeToken(appContext!!)
    }

    fun getUsername(): String {
        return try {
            loginToken.value!!.subject ?: "Unknown username"
        } catch (e: Exception) {
            "Unknown username"
        }
    }

    suspend fun getPlayers(): Array<Player> {
        try {
            return connectionManager.fetchPlayers()
        } catch (e: Exception) {
            println(e.message)
            return arrayOf(Player("Cannot connect to server", 0, "Unknown"))
        }
    }

    suspend fun getChallenges(): Array<Challenge> {
        try {
            return connectionManager.getChallenges(loginToken.value!!)
        } catch (e: Exception) {
            println(e.message)
            return emptyArray()
        }
    }

    suspend fun challenge(opponent: String) {
        try {
            connectionManager.challenge(loginToken.value!!, opponent)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun getGames(): List<Game> {
        val gameIds = connectionManager.fetchGameIds(loginToken.value!!)
        val games = mutableListOf<Game>()

        try {
            for (id in gameIds) {
                games.add(
                    connectionManager.fetchGame(id, loginToken.value!!)
                )
            }
        } catch (e: Exception) {
            println(e.message)
        }

        return games.toList()
    }
}