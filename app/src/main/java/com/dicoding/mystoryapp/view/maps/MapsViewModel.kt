package com.dicoding.mystoryapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.repository.StoryRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.response.StoryResponse
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.launch

class MapsViewModel (private val storyRepository: StoryRepositoryInterface, private val tokenRepository: TokenRepositoryInterface) : ViewModel() {
    private val _location = MutableLiveData<Status<StoryResponse>>()
    val location: LiveData<Status<StoryResponse>> get() = _location

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun getSession() {
        _token.value = tokenRepository.getSession()
    }

    fun getStoryWithLocation() {
        viewModelScope.launch {
            _location.value = Status.Loading(true)
            val temp = storyRepository.getStoryWithLocation(tokenRepository.getSession())
            _location.value = Status.Loading(false)
            _location.value = temp
        }
    }
}