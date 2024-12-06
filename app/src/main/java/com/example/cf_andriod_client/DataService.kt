package com.example.cf_andriod_client

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loginToken")

object DataService {

    private val key = stringPreferencesKey("token")

    suspend fun saveToken(context: Context, token: JWT) {
        context.dataStore.edit { loginToken ->
            loginToken[key] = token.toString()
        }
    }

    suspend fun removeToken(context: Context) {
        context.dataStore.edit { loginToken ->
            loginToken.remove(key)
        }
    }

    suspend fun readToken(context: Context): JWT? {
        return (context.dataStore.data.map {
            if (it[key] != null) {
                JWT(it[key]!!)
            } else {
                null
            }
        }).first()
    }

    suspend fun getHighScore(context: Context): Int {
        return context.dataStore.data.first()[intPreferencesKey("high_score")] ?: 0
    }
}