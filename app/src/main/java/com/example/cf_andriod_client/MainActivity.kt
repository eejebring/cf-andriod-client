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
import com.auth0.android.jwt.JWT
import com.example.cf_andriod_client.ui.theme.CfandriodclientTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val loginToken: JWT? = null

            CfandriodclientTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .padding(Dp(5F))) {

                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                MainView(navController, loginToken)
                            }
                            composable ( "login" ) {
                                Login(navController, loginToken)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainView(navController: NavController, loginToken: JWT?) {
    Text("Hi")

    if (loginToken == null || loginToken.isExpired(0)) {
        navController.navigate("login")
    }
}

@Composable
fun Login(navController: NavController, loginToken: JWT?) {
    Column {
        Text("Login view")
        Button(onClick = {navController.navigate("main")}) {
            Text("Login!")
        }
    }
}