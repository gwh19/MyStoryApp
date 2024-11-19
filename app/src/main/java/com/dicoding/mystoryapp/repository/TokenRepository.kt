package com.dicoding.mystoryapp.repository

import com.dicoding.mystoryapp.preferences.TokenPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TokenRepository private constructor(private val preference: TokenPreference): TokenRepositoryInterface{
    override suspend fun saveSession(token: String) {
        preference.setToken(token)
    }

    override fun getSession(): String {
        return runBlocking { preference.getToken().first() }
    }

    override suspend fun logout() {
        preference.logout()
    }

    companion object {
        private var INSTANCE: TokenRepository? = null

        fun getInstance(preference: TokenPreference): TokenRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenRepository(preference)
            }.also { INSTANCE = it }
        }
    }
}

interface TokenRepositoryInterface {
    suspend fun saveSession(token: String)
    fun getSession(): String
    suspend fun logout()
}