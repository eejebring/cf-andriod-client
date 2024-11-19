package com.example.cf_andriod_client

import android.net.http.HttpEngine
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.auth0.android.jwt.JWT
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.launch
import java.net.URL

@Composable
fun LoginView(navController: NavController, gameService: GameService) {
    val coreruotineScope = rememberCoroutineScope()
    val username = remember { mutableStateOf("") }
    val passcode = remember { mutableStateOf("") }

    Column {
        Text("Login view")
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") }
        )
        TextField(
            value = passcode.value,
            onValueChange = { passcode.value = it },
            label = { Text("Passcode") }
        )
        Button(onClick = {
            coreruotineScope.launch {
                gameService.loggIn(username.value, passcode.value)
                if (gameService.isLoggedIn()) {
                    navController.popBackStack()
                }
            }
        }) {
            Text("Login!")
        }
    }
}