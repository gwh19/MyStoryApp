package com.dicoding.mystoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.di.Injection
import com.dicoding.mystoryapp.repository.AuthRepository
import com.dicoding.mystoryapp.repository.AuthRepositoryInterface
import com.dicoding.mystoryapp.repository.StoryRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.view.add.AddViewModel
import com.dicoding.mystoryapp.view.detail.DetailViewModel
import com.dicoding.mystoryapp.view.login.LoginViewModel
import com.dicoding.mystoryapp.view.main.MainViewModel
import com.dicoding.mystoryapp.view.maps.MapsViewModel
import com.dicoding.mystoryapp.view.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepositoryInterface,
    private val storyRepository: StoryRepositoryInterface,
    private val tokenRepository: TokenRepositoryInterface,
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository, tokenRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, tokenRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(storyRepository, tokenRepository) as T
        } else if (modelClass.isAssignableFrom(AddViewModel::class.java)){
            return AddViewModel(storyRepository, tokenRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)){
            return MapsViewModel(storyRepository, tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    AuthRepository(),
                    Injection.provideStoryRepository(context),
                    Injection.provideTokenRepository(context),
                )
            }.also { INSTANCE = it }
    }
}