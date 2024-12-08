package com.example.cf_andriod_client.classes

class Game(
    val redPlayer: String = "unknown",
    val yellowPlayer: String = "unknown",
    var isRedTurn: Boolean = false,
    var winner: String = "NONE",
    var board: String = "n".repeat(42)
)