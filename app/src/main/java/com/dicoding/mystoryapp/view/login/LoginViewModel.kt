package com.dicoding.mystoryapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.repository.AuthRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.response.LoginResponse
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepositoryInterface, private val tokenRepository: TokenRepositoryInterface): ViewModel() {

    private val _user = MutableLiveData<Status<LoginResponse>>()
    val user: LiveData<Status<LoginResponse>> get() = _user

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    init {
        _user.value = Status.Loading(false)
    }

    fun userLogin(email: String, password: String) {
        viewModelScope.launch {
            _user.value = Status.Loading(true)
            _user.value = authRepository.userLogin(email, password)
        }
    }

    fun saveSession(token: String) {
        _token.value = token
        viewModelScope.launch {
            tokenRepository.saveSession(token)
        }
    }
}