package com.example.cf_andriod_client.classes

data class Player(
    val name: String,
    val wins: Int,
    val redPlayedLast: Boolean,
    val updatedAt: String
)