package com.psianturi.near_kotlin.model

/**
 * Sealed class representing different types of errors that can occur
 */
sealed class NearError {
    data class NetworkError(val message: String, val cause: Throwable? = null) : NearError()
    data class RpcError(val code: Int, val message: String) : NearError()
    data class ParseError(val message: String) : NearError()
    data class AuthError(val message: String) : NearError()
    data class TransactionError(val message: String, val details: String? = null) : NearError()
    data class Unknown(val message: String, val cause: Throwable? = null) : NearError()
    
    fun toDisplayMessage(): String = when (this) {
        is NetworkError -> "Network Error: $message"
        is RpcError -> "RPC Error ($code): $message"
        is ParseError -> "Parse Error: $message"
        is AuthError -> "Authentication Error: $message"
        is TransactionError -> "Transaction Error: $message${details?.let { " - $it" } ?: ""}"
        is Unknown -> "Unknown Error: $message"
    }
}

/**
 * Result wrapper for operations that can fail
 */
sealed class NearResult<out T> {
    data class Success<T>(val data: T) : NearResult<T>()
    data class Error(val error: NearError) : NearResult<Nothing>()
    data object Loading : NearResult<Nothing>()
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    
    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): NearError? = (this as? Error)?.error
}