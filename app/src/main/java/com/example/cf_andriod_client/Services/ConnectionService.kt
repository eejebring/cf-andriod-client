package com.example.cf_andriod_client.Services

import com.auth0.android.jwt.JWT
import com.example.cf_andriod_client.classes.Challenge
import com.example.cf_andriod_client.classes.Game
import com.example.cf_andriod_client.classes.Login
import com.example.cf_andriod_client.classes.Player
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType

const val gameServerUrl = "http://cf.ejebring.com"
val loginUrl = Url("$gameServerUrl/login")
val createAccountUrl = Url("$gameServerUrl/user")
val playersUrl = Url("$gameServerUrl/users")
val gamesUrl = Url("$gameServerUrl/games")
val gameUrl = Url("$gameServerUrl/game")
val challengesUrl = Url("$gameServerUrl/challenges")
val challengeUrl = Url("$gameServerUrl/challenge")
val makeMoveUrl = Url("$gameServerUrl/move")

class ConnectionService {

    private val gson = Gson()
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
        }
    }

    suspend fun fetchToken(credentials: Login, url: Url): JWT {

        val response = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(gson.toJson(credentials))
        }

        if (response.status.value != 200) {
            throw Exception(response.body<String>())
        }

        return JWT(response.body<String>())
    }

    suspend fun fetchPlayers(): Array<Player> {
        val response = httpClient.get(playersUrl)

        if (response.status.value != 200) {
            throw Exception(response.body<String>())
        }
        println("Players: ${response.body<String>()}")

        return gson.fromJson(response.body<String>(), Array<Player>::class.java)
    }

    suspend fun fetchGameIds(loginToken: JWT): Array<Int> {
        val response = httpClient.get(gamesUrl) {
            headers {
                append("Authorization", "Bearer $loginToken")
            }
        }

        if (response.status.value != 200) {
            throw Exception(response.body<String>())
        }
        println("Players: ${response.body<String>()}")

        return gson.fromJson(response.body<String>(), Array<Int>::class.java)
    }

    suspend fun fetchGame(gameId: Int, loginToken: JWT): Game {
        val response = httpClient.get("$gameUrl/$gameId") {
            headers {
                append("Authorization", "Bearer $loginToken")
            }
        }

        if (response.status.value != 200) {
            throw Exception(response.body<String>())
        }

        return gson.fromJson(response.body<String>(), Game::class.java)
    }

    suspend fun getChallenges(loginToken: JWT): Array<Challenge> {
        val response = httpClient.get(challengesUrl) {
            headers {
                append("Authorization", "Bearer $loginToken")
            }
        }
        if (response.status.value != 200) {
            throw Exception(response.body<String>())
        }

        println("Challenges: ${response.body<String>()}")

        return gson.fromJson(response.body<String>(), Array<Challenge>::class.java)
    }

    suspend fun challenge(loginToken: JWT, opponent: String) {
        val response = httpClient.post("$challengeUrl/$opponent") {
            headers {
                append("Authorization", "Bearer $loginToken")
            }
        }
        if (response.status.value != 200 && response.status.value != 202) {
            throw Exception("${response.status.value}: ${response.body<String>()}")
        }
    }
}