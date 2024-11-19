package com.dicoding.mystoryapp.di

import android.content.Context
import com.dicoding.mystoryapp.db.StoryDatabase
import com.dicoding.mystoryapp.preferences.TokenPreference
import com.dicoding.mystoryapp.preferences.dataStore
import com.dicoding.mystoryapp.repository.StoryRepository
import com.dicoding.mystoryapp.repository.TokenRepository

object Injection {
    fun provideTokenRepository(context: Context): TokenRepository {
        val preference = TokenPreference.getInstance(context.dataStore)
        return TokenRepository.getInstance(preference)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getStoryDatabase(context)
        return StoryRepository(database)
    }
}