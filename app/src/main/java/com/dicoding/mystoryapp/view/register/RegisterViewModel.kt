package com.dicoding.mystoryapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.repository.AuthRepositoryInterface
import com.dicoding.mystoryapp.response.RegisterResponse
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepositoryInterface): ViewModel() {

    private val _response = MutableLiveData<Status<RegisterResponse>>()
    val response: LiveData<Status<RegisterResponse>> get() = _response

    init {
        _response.value = Status.Loading(false)
    }

    fun userRegister(name: String, email: String, password: String) {
       viewModelScope.launch {
           _response.value = Status.Loading(true)
           _response.value = authRepository.userRegister(name, email, password)
       }
    }
}