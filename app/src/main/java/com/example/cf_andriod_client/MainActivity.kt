package com.example.cf_andriod_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cf_andriod_client.ui.theme.CfandriodclientTheme
import com.example.cf_andriod_client.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val gameService: GameService by viewModels()
            gameService.init(applicationContext)

            CfandriodclientTheme(darkTheme = false) {
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
        Column {
            Text("This is the barren main view", style = Typography.titleLarge)
            Row {
                Text("Logged in as ${gameService.getUsername()}")
                Button(onClick = {
                    runBlocking {
                        gameService.loggOut()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }) {
                    Text("Log out")
                }
            }

            val players = remember { mutableStateListOf<Player>() }

            val scope = rememberCoroutineScope()
            LaunchedEffect(scope) {
                while (true) {
                    val newPlayers = gameService.getPlayers()
                    players.clear()
                    players.addAll(newPlayers)
                    delay(1000)
                }
            }

            Text("Players:")
            LazyColumn {
                for (player in players) {
                    val isMe = player.name == gameService.getUsername()
                    val baseBoxModifier =
                        Modifier
                            .padding(Dp(5F))
                            .fillMaxWidth()
                            .background(Color.Cyan)
                    item {
                        Box(
                            modifier = if (isMe) baseBoxModifier.background(Color.Green) else baseBoxModifier
                        ) {
                            Column {
                                Row {
                                    Text(player.name)
                                    Text("Wins: ${player.wins}")
                                }
                                Text(
                                    "Last seen: ${
                                        ChronoUnit.SECONDS.between(
                                            LocalDateTime.parse(player.updatedAt),
                                            LocalDateTime.now()
                                        )
                                    } seconds ago"
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        runBlocking { gameService.loadToken() }
        Button(onClick = { navController.navigate("login") }) {
            Text("Log in")
        }
    }
}

