package com.dicoding.mystoryapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.mystoryapp.db.StoryDatabase
import com.dicoding.mystoryapp.db.StoryRemoteMediator
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.response.RegisterResponse
import com.dicoding.mystoryapp.response.StoryDetailResponse
import com.dicoding.mystoryapp.response.StoryResponse
import com.dicoding.mystoryapp.retrofit.ApiConfig
import com.dicoding.mystoryapp.util.Status
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StoryRepository (private val storyDatabase: StoryDatabase) : StoryRepositoryInterface {
    @OptIn(ExperimentalPagingApi::class)
    override fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    override suspend fun getStoryDetail(token: String, id: String): Status<StoryDetailResponse> {
        return suspendCoroutine { continuation ->
            var detailStoryResult: Status<StoryDetailResponse>
            val client = ApiConfig.getApiService().getStoryDetail("Bearer $token", id)
            client.enqueue(
                object : Callback<StoryDetailResponse> {
                    override fun onResponse(
                        call: Call<StoryDetailResponse>,
                        response: Response<StoryDetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            detailStoryResult = Status.Success(response.body() as StoryDetailResponse)
                            continuation.resume(detailStoryResult)
                            return
                        }
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        detailStoryResult = Status.Failure(jsonObj.getString("message") ?: response.message())
                        continuation.resume(detailStoryResult)
                    }

                    override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                        detailStoryResult = Status.Failure(t.toString())
                        continuation.resume(detailStoryResult)
                    }
                }
            )
        }
    }

    override suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): Status<RegisterResponse> {
        return suspendCoroutine { continuation -> 
            var postStoryResult: Status<RegisterResponse>
            val client = ApiConfig.getApiService().postStory("Bearer $token", file, description, lat, lon)
            client.enqueue(
                object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            postStoryResult = Status.Success(response.body() as RegisterResponse)
                            continuation.resume(postStoryResult)
                            return
                        }
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        postStoryResult = Status.Failure(jsonObj.getString("message") ?: response.message())
                        continuation.resume(postStoryResult)
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        postStoryResult = Status.Failure(t.toString())
                        continuation.resume(postStoryResult)
                    }
                }
            )
        }
    }

    override suspend fun getStoryWithLocation(token: String): Status<StoryResponse> {
        return suspendCoroutine { continuation ->
            var storyResult: Status<StoryResponse>
            val client = ApiConfig.getApiService().getStoryWithLocation("Bearer $token")
            client.enqueue(
                object : Callback<StoryResponse> {
                    override fun onResponse(
                        call: Call<StoryResponse>,
                        response: Response<StoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            storyResult = Status.Success(response.body() as StoryResponse)
                            continuation.resume(storyResult)
                            return
                        }
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        storyResult = Status.Failure(jsonObj.getString("message") ?: response.message())
                        continuation.resume(storyResult)
                    }

                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        storyResult = Status.Failure(t.toString())
                        continuation.resume(storyResult)
                    }
                }
            )
        }
    }
}

interface StoryRepositoryInterface {
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>>
    suspend fun getStoryDetail(token: String, id: String): Status<StoryDetailResponse>
    suspend fun postStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double? = null, lon: Double? = null): Status<RegisterResponse>
    suspend fun getStoryWithLocation(token: String): Status<StoryResponse>
}