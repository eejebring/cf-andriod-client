package com.example.cf_andriod_client

import com.auth0.android.jwt.JWT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

const val gameServerUrl = "http://cf.ejebring.com"
const val loginUrl = "$gameServerUrl/login"

class GameService {

    private val httpClient = HttpClient(CIO)
    private var loginToken: JWT? = null

    fun isLoggedIn(): Boolean {
        return loginToken != null && !loginToken!!.isExpired(0)
    }

    suspend fun loggIn(username: String, passcode: String) {
        val loginJson = """{"username": "$username", "passcode": "$passcode"}"""

        val response = httpClient.post(loginUrl) {
            contentType(ContentType.Application.Json)
            setBody(loginJson)
        }

        println(response.status)

        if (response.status.value != 200) {
            throw Exception(response.body<String>())
        }

        loginToken = JWT(response.body<String>())
    }

    fun getUsername(): String {
        return try {
            loginToken!!.getClaim("username").asString() ?: "Unknown username"
        } catch (e: Exception) {
            "Unknown username"
        }
    }
}