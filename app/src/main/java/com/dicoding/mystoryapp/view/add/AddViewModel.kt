package com.dicoding.mystoryapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.repository.StoryRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.response.RegisterResponse
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel (private val storyRepository: StoryRepositoryInterface, private val tokenRepository: TokenRepositoryInterface): ViewModel() {

    private val _response = MutableLiveData<Status<RegisterResponse>>()
    val response: LiveData<Status<RegisterResponse>> get() = _response

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    init {
        _response.value = Status.Loading(false)
    }

    fun getSession() {
        _token.value = tokenRepository.getSession()
    }

    fun postStories(file: MultipartBody.Part, description: RequestBody, lat: Double? = null, lon: Double? = null) {

        viewModelScope.launch {
            _response.value = Status.Loading(true)
            val temp = storyRepository.postStory(tokenRepository.getSession(), file, description, lat, lon)
            _response.value = Status.Loading(false)
            _response.value = temp
        }
    }
}