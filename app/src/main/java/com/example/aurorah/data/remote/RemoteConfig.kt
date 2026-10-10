package com.example.aurorah.data.remote

object RemoteConfig {
    const val BASE_URL = "https://aurorah-sql-server.database.windows.net/"
    const val SERVER_NAME = "aurorah-sql-server.database.windows.net"
    const val DATABASE_NAME = "aurorah-db"
    const val AUTHENTICATION_TYPE = "SQL Server Authentication"
    const val ADMIN_LOGIN = "aurorahadmin"
    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 15L

    data class ServerStatusInfo(
        val serverName: String = SERVER_NAME,
        val databaseName: String = DATABASE_NAME,
        val authType: String = AUTHENTICATION_TYPE,
        val adminLogin: String = ADMIN_LOGIN,
        val isConnected: Boolean = true,
        val statusMessage: String = "Connected to Azure SQL Database ($DATABASE_NAME)",
        val lastCheckTimestamp: String = ""
    )
}
