package com.example.cf_andriod_client

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.example.cf_andriod_client.Services.GameService
import com.example.cf_andriod_client.Services.connectionInterval
import com.example.cf_andriod_client.classes.Game
import com.example.cf_andriod_client.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Composable
fun GamesList(gameService: GameService, navController: NavController, gameId: Int) {

    val game = remember { mutableStateOf(Game()) }
    val userError = remember { mutableStateOf("") }
    val hasPlayed = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(scope) {
        while (true) {
            val newGameState = gameService.getGame(gameId)
            if (newGameState.board != game.value.board) userError.value = ""
            if (newGameState.redPlayer == "unknown") userError.value = "  Connection error"
            println("${newGameState.winner != "TBD" && hasPlayed.value} ${newGameState.winner} ${hasPlayed.value}")
            if (newGameState.winner != "TBD" && hasPlayed.value) {
                navController.navigate("win/${newGameState.winner}")
                hasPlayed.value = false
            }
            game.value = newGameState
            delay(connectionInterval)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val amRedPlayer = game.value.redPlayer == gameService.getUsername()
        val isLocalTurn = game.value.isRedTurn == amRedPlayer

        Row {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to main menu",
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Text(
                "${game.value.redPlayer} vs ${game.value.yellowPlayer}",
                style = Typography.titleLarge
            )
        }
        Row {
            Text(
                if (game.value.winner != "TBD") "${game.value.winner} is victorious!"
                else if (isLocalTurn) "Your turn"
                else "Opponent's turn"
            )
            Text(userError.value, color = Color.Red)
        }
        Box(
            modifier = Modifier.heightIn(min = Dp(LocalConfiguration.current.screenHeightDp * 0.8F)),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0..6) {
                        Button(
                            onClick = {
                                runBlocking {
                                    try {
                                        gameService.makeMove(gameId, i)
                                        hasPlayed.value = true
                                    } catch (e: Exception) {
                                        userError.value = e.message ?: "Unknown error"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1F),
                            enabled = game.value.winner == "TBD" && isLocalTurn
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Place piece in column nr ${i + 1}"
                            )
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(7F / 6F)
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    var xCursor = 1
                    var yCursor = 1

                    game.value.board.forEach { slot ->
                        drawCircle(
                            color = when (slot) {
                                'r' -> Color.Red
                                'y' -> Color.Yellow
                                else -> Color.White
                            },
                            center = Offset(
                                x = size.width / 8 * xCursor,
                                y = size.height / 7 * yCursor
                            ),
                            radius = size.minDimension / 17
                        )

                        xCursor++
                        if (xCursor > 7) {
                            xCursor = 1
                            yCursor++
                        }
                    }
                }
            }
        }
    }
}