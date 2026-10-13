package com.example.aurorah.data.remote

sealed interface AzureSqlResult<out T> {
    data class Success<out T>(val data: T, val message: String) : AzureSqlResult<T>
    data class Error(
        val message: String,
        val isFirewallBlocked: Boolean,
        val rawError: String
    ) : AzureSqlResult<Nothing>
}
