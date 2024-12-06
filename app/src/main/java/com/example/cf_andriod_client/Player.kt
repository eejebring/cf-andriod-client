package com.example.cf_andriod_client

import kotlinx.serialization.Serializable

@Serializable
data class Player(val name: String, val wins: Int, val updatedAt: String)