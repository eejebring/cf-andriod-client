package com.example.cf_andriod_client

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.cf_andriod_client.Services.GameService
import com.example.cf_andriod_client.classes.Game
import com.example.cf_andriod_client.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Composable
fun GamesList(gameService: GameService, gameId: Int) {

    val game = remember { mutableStateOf(Game()) }
    val userError = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    LaunchedEffect(scope) {
        while (true) {
            val newGameState = gameService.getGame(gameId)
            if (newGameState.redPlayedLast != game.value.redPlayedLast) userError.value = ""
            game.value = newGameState
            delay(1000)
        }
    }

    Column {
        val amRedPlayer = game.value.redPlayer == gameService.getUsername()
        val isLocalTurn = game.value.redPlayedLast != amRedPlayer

        Text("${game.value.redPlayer} vs ${game.value.yellowPlayer}", style = Typography.titleLarge)
        Row {
            Text(if (isLocalTurn) "Your turn" else "Opponent's turn")
            Text(userError.value, color = Color.Red)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0..6) {
                Button(
                    onClick = {
                        runBlocking {
                            try {
                                gameService.makeMove(gameId, i)
                            } catch (e: Exception) {
                                userError.value = e.message ?: "Unknown error"
                            }
                        }
                    },
                    modifier = Modifier.weight(1F)
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Place piece in column nr ${i + 1}"
                    )
                }
            }
        }

        Text(game.value.board.slice(0..6))
        Text(game.value.board.slice(7..13))
        Text(game.value.board.slice(14..20))
        Text(game.value.board.slice(21..27))
        Text(game.value.board.slice(28..34))
        Text(game.value.board.slice(35..41))

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            drawCircle(
                color = Color.Red,
                center = Offset(
                    x = 0.5F,
                    y = 0.5F
                ),
            )
        }
    }
}