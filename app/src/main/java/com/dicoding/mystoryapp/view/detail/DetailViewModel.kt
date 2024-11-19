package com.dicoding.mystoryapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.repository.StoryRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.response.StoryDetailResponse
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.launch

class DetailViewModel (private val storyRepository: StoryRepositoryInterface, private val tokenRepository: TokenRepositoryInterface) : ViewModel() {

    private val _storyDetail = MutableLiveData<Status<StoryDetailResponse>>()
    val storyDetail: LiveData<Status<StoryDetailResponse>> get() = _storyDetail

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun getSession() {
        _token.value = tokenRepository.getSession()
    }

    fun getDetailStory (id: String) {
        viewModelScope.launch {
            _storyDetail.value = Status.Loading(true)
            val temp = storyRepository.getStoryDetail(tokenRepository.getSession(), id)
            _storyDetail.value = Status.Loading(false)
            _storyDetail.value = temp
        }
    }
}