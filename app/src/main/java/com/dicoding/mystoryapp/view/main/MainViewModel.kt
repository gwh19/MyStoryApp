package com.dicoding.mystoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mystoryapp.repository.StoryRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepositoryInterface, private val tokenRepository: TokenRepositoryInterface) : ViewModel() {

    private val _story = MutableLiveData<Status<LiveData<PagingData<ListStoryItem>>>>()
    val story: LiveData<Status<LiveData<PagingData<ListStoryItem>>>> get() = _story

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun getSession() {
        _token.value = tokenRepository.getSession()
    }

    fun getStory() {
        _story.value = Status.Loading(true)
        val temp = Status.Success(storyRepository.getStory(tokenRepository.getSession()).cachedIn(viewModelScope))
        _story.value = Status.Loading(false)
        _story.value = temp
    }

    fun logout() {
        viewModelScope.launch{
            tokenRepository.logout()
        }
        _token.value = ""
    }
}