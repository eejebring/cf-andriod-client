package com.example.cf_andriod_client.Services

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.example.cf_andriod_client.classes.Challenge
import com.example.cf_andriod_client.classes.Game
import com.example.cf_andriod_client.classes.Login
import com.example.cf_andriod_client.classes.Player
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime

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
            return arrayOf(
                Player(
                    "Cannot connect to server",
                    0,
                    false,
                    LocalDateTime.now().toString()
                )
            )
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

    suspend fun getGames(): List<Pair<Int, Game>> {
        val games = mutableListOf<Pair<Int, Game>>()

        try {
            val gameIds = connectionManager.fetchGameIds(loginToken.value!!)
            for (id in gameIds) {
                games.add(
                    Pair(
                        id,
                        connectionManager.fetchGame(id, loginToken.value!!)
                    )
                )
            }
        } catch (e: Exception) {
            println(e.message)
        }

        return games.toList()
    }

    suspend fun getGame(gameId: Int): Game {
        try {
            return connectionManager.fetchGame(gameId, loginToken.value!!)
        } catch (e: Exception) {
            println(e.message)
            return Game()
        }
    }

    suspend fun makeMove(gameId: Int, column: Int) {
        connectionManager.makeMove(gameId, column, loginToken.value!!)
    }
}