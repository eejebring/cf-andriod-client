package com.example.cf_andriod_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cf_andriod_client.ui.theme.CfandriodclientTheme
import com.example.cf_andriod_client.ui.theme.Typography
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val gameService = GameService(applicationContext)

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

    Column {
        Text("This is the barren main view", style = Typography.titleLarge)
        Text("Logged in as ${gameService.getUsername()}")
        Button(onClick = {
            runBlocking {
                gameService.loggOut()
            }
        }) {
            Text("Log out")
        }
    }

    runBlocking {
        if (!gameService.isLoggedIn()) {
            gameService.loadToken()
            if (!gameService.isLoggedIn()) {
                navController.navigate("login")
            }
        }
    }
}

