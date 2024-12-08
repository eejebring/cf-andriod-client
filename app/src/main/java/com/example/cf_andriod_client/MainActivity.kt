package com.example.cf_andriod_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cf_andriod_client.Services.GameService
import com.example.cf_andriod_client.ui.theme.CfAndriodClientTheme
import com.example.cf_andriod_client.ui.theme.Typography
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val gameService: GameService by viewModels()
            gameService.init(applicationContext)

            CfAndriodClientTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(Dp(5F))
                    ) {

                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                MainView(navController, gameService)
                            }
                            composable("login") {
                                LoginView(navController, gameService)
                            }
                            composable("createAccount") {
                                CreateAccountView(navController, gameService)
                            }
                            composable("game/{id}") {
                                val id = it.arguments?.getString("id")?.toInt()
                                    ?: throw Exception("No gameId provided for navigation")
                                GamesList(gameService, id)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainView(navController: NavController, gameService: GameService) {
    if (gameService.isLoggedIn()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    runBlocking {
                        gameService.loggOut()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("Log out")
            }
        }
        Column {
            Text("Welcome to the Lobby", style = Typography.titleLarge)
            Text("Logged in as ${gameService.getUsername()}")
            GamesList(gameService, navController)
            PlayerList(gameService)

        }
    } else {
        runBlocking { gameService.loadToken() }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { navController.navigate("login") }) {
                Text("Log in", style = Typography.titleLarge)
            }
        }
    }
}

