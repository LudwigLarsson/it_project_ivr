package com.ludwiglarsson.antiplanner.utils

import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result

inline fun <R> Result<R>.onSuccess(action: (R) -> Unit): Result<R> {
    if (this is Result.Success) {
        action(value)
    }
    return this
}

inline fun <R> Result<R>.onFailure(action: (Result.Failure) -> Unit): Result<R> {
    if (this is Result.Failure) {
        action(this)
    }
    return this
}

inline fun <R> Result<R>.getOr(fallback: (Result.Failure) -> R): R {
    return when (this) {
        is Result.Failure -> fallback(this)
        is Result.Success -> value
    }
}

fun <R, T> Result<R>.map(transform: (R) -> T): Result<T> {
    return when (this) {
        is Result.Failure -> this
        is Result.Success -> Result.Success(transform(this.value))
    }
}