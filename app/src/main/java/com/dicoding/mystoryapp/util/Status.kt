package com.dicoding.mystoryapp.util

sealed class Status<out T> {
    data class Loading<out T>(val state: Boolean): Status<T>()
    data class Success<out T>(val data: T): Status<T>()
    data class Failure<out T>(val throwable: String): Status<T>()
}
