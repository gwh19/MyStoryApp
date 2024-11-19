package com.dicoding.mystoryapp.repository

import com.dicoding.mystoryapp.response.LoginResponse
import com.dicoding.mystoryapp.response.RegisterResponse
import com.dicoding.mystoryapp.retrofit.ApiConfig
import com.dicoding.mystoryapp.util.Status
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository : AuthRepositoryInterface {
    override suspend fun userLogin(email:String, password: String): Status<LoginResponse> {
        return suspendCoroutine { continuation ->
            var loginResponse: Status<LoginResponse>
            val client = ApiConfig.getApiService().login(email, password)
            client.enqueue(
                object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            loginResponse = Status.Success(response.body() as LoginResponse)
                            continuation.resume(loginResponse)
                            return
                        }
                        val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                        loginResponse = Status.Failure(jsonObject.getString("message") ?: response.message())
                        continuation.resume(loginResponse)
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        loginResponse = Status.Failure(t.toString())
                        continuation.resume(loginResponse)
                    }
                }
            )
        }
    }

    override suspend fun userRegister(name: String, email: String, password: String): Status<RegisterResponse> {
        return suspendCoroutine { continuation ->
            var registerResponse: Status<RegisterResponse>
            val client = ApiConfig.getApiService().register(name, email, password)
            client.enqueue(
                object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            registerResponse = Status.Success(response.body() as RegisterResponse)
                            continuation.resume(registerResponse)
                            return
                        }
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        registerResponse = Status.Failure(jsonObj.getString("message") ?: response.message())
                        continuation.resume(registerResponse)
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        registerResponse = Status.Failure(t.toString())
                        continuation.resume(registerResponse)
                    }
                }
            )
        }
    }
}

interface AuthRepositoryInterface {
    suspend fun userLogin(email:String, password:String): Status<LoginResponse>
    suspend fun userRegister(name: String, email: String, password: String): Status<RegisterResponse>
}