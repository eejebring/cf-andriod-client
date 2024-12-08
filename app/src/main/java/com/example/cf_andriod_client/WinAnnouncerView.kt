package com.example.cf_andriod_client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.cf_andriod_client.ui.theme.Typography

@Composable
fun WinAnnouncerView(navController: NavController, winner: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to main menu",
            modifier = Modifier
                .align(Alignment.TopStart)
                .clickable { navController.popBackStack() }
        )
        Column {
            if (winner == "NONE") Text("Draw", style = Typography.titleLarge)
            else Text("The winner is $winner", style = Typography.titleLarge)
            Button(onClick = {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }
            }) {
                Text("Back to main menu")
            }
        }
    }
}