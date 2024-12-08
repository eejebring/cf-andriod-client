package com.example.cf_andriod_client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.cf_andriod_client.Services.GameService
import com.example.cf_andriod_client.Services.connectionInterval
import com.example.cf_andriod_client.classes.Game
import kotlinx.coroutines.delay


@Composable
fun GamesList(gameService: GameService, navController: NavController) {
    val games = remember { mutableStateListOf<Pair<Int, Game>>() }
    val showActiveGames = remember { mutableStateOf(true) }
    val showInactiveGames = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(scope) {
        while (true) {
            val newGames = gameService.getGames()
            games.clear()
            games.addAll(newGames)
            delay(connectionInterval)
        }
    }

    Row(modifier = Modifier.clickable { showActiveGames.value = !showActiveGames.value }) {
        Icon(
            if (showActiveGames.value) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Toggle active games"
        )
        Text("Active games:")
    }
    if (showActiveGames.value)
        LazyColumn {
            for ((gameId, game) in games) {
                val amRedPlayer = game.redPlayer == gameService.getUsername()
                val isLocalTurn = game.isRedTurn == amRedPlayer

                if (game.winner != "TBD") continue

                item {
                    Button(
                        onClick = { navController.navigate("game/${gameId}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${if (isLocalTurn) "Your turn:" else "Opponent's turn:"} ${game.redPlayer} vs ${game.yellowPlayer}")
                    }
                }
            }
        }
    Row(modifier = Modifier.clickable { showInactiveGames.value = !showInactiveGames.value }) {
        Icon(
            if (showInactiveGames.value) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Toggle inactive games"
        )
        Text("Inactive games:")
    }

    if (showInactiveGames.value)
        LazyColumn {
            for ((gameId, game) in games) {
                val amRedPlayer = game.redPlayer == gameService.getUsername()

                if (game.winner == "TBD") continue

                item {
                    Button(
                        onClick = { navController.navigate("game/${gameId}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        if (game.winner == "NONE") Text("Draw!")
                        else if (game.winner == gameService.getUsername()) Text("You won against ${game.winner}!")
                        else Text("You lost against ${game.winner}!")
                    }
                }
            }
        }
}