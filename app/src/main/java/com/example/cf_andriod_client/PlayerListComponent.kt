package com.example.cf_andriod_client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun PlayerList(gameService: GameService) {

    val players = remember { mutableStateListOf<Player>() }
    val challenges = remember { mutableStateListOf<Challenge>() }

    val scope = rememberCoroutineScope()
    LaunchedEffect(scope) {
        while (true) {
            val newPlayers = gameService.getPlayers()
            players.clear()
            players.addAll(newPlayers)

            val newChallenges = gameService.getChallenges()
            challenges.clear()
            challenges.addAll(newChallenges)

            delay(1000)
        }
    }

    Text("Players:")
    LazyColumn {
        for (player in players) {
            val seenSecondsAgo = ChronoUnit.SECONDS.between(
                LocalDateTime.parse(player.updatedAt),
                LocalDateTime.now()
            )

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
                            if (seenSecondsAgo < 10)
                                "Online now!"
                            else if (seenSecondsAgo < 60)
                                "Last seen: $seenSecondsAgo seconds ago"
                            else if (seenSecondsAgo < 60 * 60)
                                "Last seen: ${seenSecondsAgo / 60} minutes ago"
                            else
                                "Last seen: ${seenSecondsAgo / 60 / 60} hours ago"

                        )
                    }
                    if (!isMe) {
                        val challengeText =
                            if (challenges.any { challenge -> challenge.challenger == player.name })
                                "Accept challenge"
                            else if (challenges.any { challenge -> challenge.challenged == player.name })
                                "Challenge sent"
                            else
                                "Challenge"

                        Button(
                            onClick = {
                                runBlocking {
                                    gameService.challenge(player.name)
                                }
                            },
                            enabled = challengeText != "Challenge sent",
                            modifier = Modifier.align(
                                Alignment.CenterEnd
                            )
                        ) {
                            Text(challengeText)
                        }
                    }
                }
            }
        }
    }
}