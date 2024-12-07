package com.example.cf_andriod_client.classes

class Game(
    val redPlayer: String,
    val yellowPlayer: String,
    var redPlayedLast: Boolean,
    var isFinished: Boolean,
    var board: String
)