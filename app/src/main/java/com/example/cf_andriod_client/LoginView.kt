package com.example.cf_andriod_client

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun LoginView(navController: NavController, gameService: GameService) {
    val coroutineScope = rememberCoroutineScope()
    val username = remember { mutableStateOf("") }
    val passcode = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    Box(contentAlignment = Alignment.CenterEnd) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Login view")
            Text(errorMessage.value, color = androidx.compose.ui.graphics.Color.Red)
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
                errorMessage.value = ""
                coroutineScope.launch {
                    try {
                        gameService.loggIn(username.value, passcode.value)
                    } catch (e: Exception) {
                        errorMessage.value = e.message ?: "Unknown error"
                    }
                    if (gameService.isLoggedIn()) {
                        navController.popBackStack()
                    }
                }
            }) {
                Text("Login!")
            }
        }
    }
}