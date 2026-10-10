package com.example.aurorah.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.remote.AzureServerConnectionStatus
import com.example.aurorah.data.repository.AurorahRepository
import com.example.aurorah.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isSyncing: Boolean = false,
    val syncStatusMessage: String? = null
)

class SettingsViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val themeModeState: StateFlow<ThemeMode> = repository.themeModeFlow
        .asStateFlowIn(viewModelScope, ThemeMode.SYSTEM)

    val languageCodeState: StateFlow<String> = repository.languageCodeFlow
        .asStateFlowIn(viewModelScope, "en")

    val notificationsEnabledState: StateFlow<Boolean> = repository.notificationsEnabledFlow
        .asStateFlowIn(viewModelScope, true)

    val lastSyncedState: StateFlow<String> = repository.lastSyncedFlow
        .asStateFlowIn(viewModelScope, "Just now")

    val userSessionState: StateFlow<UserSession> = repository.userSessionFlow
        .asStateFlowIn(viewModelScope, UserSession())

    val azureConnectionStatus: StateFlow<AzureServerConnectionStatus> = repository.azureConnectionStatusFlow

    init {
        checkAzureConnection()
    }

    fun checkAzureConnection() {
        viewModelScope.launch {
            repository.checkAzureServerConnection()
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(themeMode)
        }
    }

    fun setLanguageCode(languageCode: String) {
        viewModelScope.launch {
            repository.setLanguageCode(languageCode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    fun syncDataNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSyncing = true,
                syncStatusMessage = "Connecting to aurorah-sql-server.database.windows.net (aurorah-db)..."
            )
            val result = repository.syncWithRemote()
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                syncStatusMessage = result.message
            )
        }
    }

    fun requestRightToBeForgotten(anonymizeOnly: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSyncing = true,
                syncStatusMessage = "Processing POPIA Right to be Forgotten request..."
            )
            val session = repository.userSessionFlow.first()
            val identifier = session.email.ifEmpty { session.username }

            if (anonymizeOnly) {
                repository.anonymizeUserData(identifier, session.fullName)
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncStatusMessage = "Your personal data has been anonymized in compliance with POPIA Act No. 4 of 2013."
                )
            } else {
                repository.deleteUserDataCompletely(identifier, session.fullName)
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncStatusMessage = "All personal records have been completely erased from the database."
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearUserSession()
        }
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}

private fun <T> Flow<T>.asStateFlowIn(
    scope: CoroutineScope,
    initialValue: T
): StateFlow<T> {
    val flow = MutableStateFlow(initialValue)
    scope.launch {
        collect { value -> flow.value = value }
    }
    return flow.asStateFlow()
}
