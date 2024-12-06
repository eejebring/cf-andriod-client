package com.example.cf_andriod_client

import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType

const val gameServerUrl = "http://cf.ejebring.com"
val loginUrl = Url("$gameServerUrl/login")
val createAccountUrl = Url("$gameServerUrl/user")
val playersUrl = Url("$gameServerUrl/users")

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
}