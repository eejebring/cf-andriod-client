package com.example.cf_andriod_client

import com.auth0.android.jwt.JWT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

const val gameServerUrl = "localhost:8080"
const val loginUrl = "http://$gameServerUrl/login"

class GameService {

    val httpClient = HttpClient(CIO)
    private var loginToken: JWT? = null

    fun isLoggedIn(): Boolean {
        return loginToken != null && !loginToken!!.isExpired(0)
    }

    suspend fun loggIn(username: String, passcode: String) {
        val loginJson = """{"username": "$username", "passcode": "$passcode"}"""

        val response = httpClient.get(loginUrl) {
            contentType(ContentType.Application.Json)
            setBody(loginJson)
        }

        println(response.status)

        loginToken = JWT(response.body<String>())
    }
}