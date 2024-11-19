package com.dicoding.mystoryapp.retrofit

import com.dicoding.mystoryapp.response.LoginResponse
import com.dicoding.mystoryapp.response.RegisterResponse
import com.dicoding.mystoryapp.response.StoryDetailResponse
import com.dicoding.mystoryapp.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<StoryDetailResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): Call<RegisterResponse>

    @GET("stories")
    fun getStoryWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1,
    ): Call<StoryResponse>
}