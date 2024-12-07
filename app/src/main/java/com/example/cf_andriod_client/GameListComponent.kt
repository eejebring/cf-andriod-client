package com.example.cf_andriod_client

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.cf_andriod_client.Services.GameService
import com.example.cf_andriod_client.classes.Game
import kotlinx.coroutines.delay


@Composable
fun GamesList(gameService: GameService) {
    val games = remember { mutableStateListOf<Game>() }

    val scope = rememberCoroutineScope()
    LaunchedEffect(scope) {
        while (true) {
            val newGames = gameService.getGames()
            games.clear()
            games.addAll(newGames)
            delay(1000)
        }
    }

    Text("Active games:")
    LazyColumn {
        for (game in games) {
            val amRedPlayer = game.redPlayer == gameService.getUsername()
            val isLocalTurn = game.redPlayedLast == amRedPlayer

            item {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${if (isLocalTurn) "Your turn:" else "Opponent's turn:"} ${game.redPlayer} vs ${game.yellowPlayer}")
                }
            }
        }
    }
}