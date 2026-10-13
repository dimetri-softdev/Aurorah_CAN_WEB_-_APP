package com.example.aurorah.data.remote

import com.example.aurorah.data.local.AppDatabase
import com.example.aurorah.data.remote.dto.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SyncResult(
    val isSuccess: Boolean = true,
    val message: String = "",
    val itemsSyncedCount: Int = 0,
    val timestamp: String = ""
)

data class AzureServerConnectionStatus(
    val serverName: String = RemoteConfig.SERVER_NAME,
    val databaseName: String = RemoteConfig.DATABASE_NAME,
    val authType: String = RemoteConfig.AUTHENTICATION_TYPE,
    val adminLogin: String = RemoteConfig.ADMIN_LOGIN,
    val isConnected: Boolean = false,
    val isOnline: Boolean = false,
    val statusMessage: String = "Not checked yet",
    val lastCheckTimestamp: String = ""
)

/** Pulls public data (news, needs) from the website API into the local Room cache. */
class OfflineSyncManager(
    private val apiService: AurorahApiService = AurorahNetworkClient.apiService
) {
    private val _syncResultState = MutableStateFlow(SyncResult())
    val syncResultState: StateFlow<SyncResult> = _syncResultState.asStateFlow()

    private val _connectionStatusState = MutableStateFlow(
        AzureServerConnectionStatus(lastCheckTimestamp = getCurrentTimestamp())
    )
    val connectionStatusState: StateFlow<AzureServerConnectionStatus> = _connectionStatusState.asStateFlow()

    suspend fun checkConnectionStatus(): AzureServerConnectionStatus = withContext(Dispatchers.IO) {
        val nowStr = getCurrentTimestamp()
        val status = try {
            apiService.getNews()
            AzureServerConnectionStatus(
                isConnected = true,
                isOnline = true,
                statusMessage = "Connected to ${RemoteConfig.SERVER_NAME}",
                lastCheckTimestamp = nowStr
            )
        } catch (e: Throwable) {
            AzureServerConnectionStatus(
                isConnected = false,
                isOnline = false,
                statusMessage = "Server unreachable, showing cached data",
                lastCheckTimestamp = nowStr
            )
        }
        _connectionStatusState.value = status
        status
    }

    suspend fun syncAllData(database: AppDatabase): SyncResult = withContext(Dispatchers.IO) {
        val nowStr = getCurrentTimestamp()
        val result = try {
            val news = apiService.getNews()
            val needs = apiService.getNeeds()
            news.forEach { database.newsDao().insert(it.toEntity()) }
            needs.forEach { database.communityNeedDao().insert(it.toEntity()) }
            val count = news.size + needs.size
            _connectionStatusState.value = AzureServerConnectionStatus(
                isConnected = true,
                isOnline = true,
                statusMessage = "Synced at $nowStr",
                lastCheckTimestamp = nowStr
            )
            SyncResult(true, "Synced $count records from the server", count, nowStr)
        } catch (e: Throwable) {
            _connectionStatusState.value = AzureServerConnectionStatus(
                isConnected = false,
                isOnline = false,
                statusMessage = "Server unreachable, showing cached data",
                lastCheckTimestamp = nowStr
            )
            SyncResult(false, "Sync failed: ${e.message ?: "network error"}", 0, nowStr)
        }
        _syncResultState.value = result
        result
    }

    private fun getCurrentTimestamp(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
