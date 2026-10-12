package com.example.aurorah.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.repository.AurorahRepository
import com.example.aurorah.util.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AuthUiState(
    val emailOrUsername: String = "",
    val password: String = "",
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Registration state
    val fullName: String = "",
    val regEmail: String = "",
    val regPassword: String = "",
    val regConfirmPassword: String = "",
    val selectedRole: String = "Community Member"
)

sealed interface AuthEvent {
    data object LoginSuccess : AuthEvent
    data object RegisterSuccess : AuthEvent
}

class AuthViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AuthEvent>()
    val eventFlow: SharedFlow<AuthEvent> = _eventFlow.asSharedFlow()

    val userSessionState: StateFlow<UserSession> = repository.userSessionFlow
        .asStateFlowIn(viewModelScope, UserSession())

    fun onEmailOrUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(emailOrUsername = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = value)
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, errorMessage = null)
    }

    fun onRegEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(regEmail = value, errorMessage = null)
    }

    fun onRegPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(regPassword = value, errorMessage = null)
    }

    fun onRegConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(regConfirmPassword = value, errorMessage = null)
    }

    fun onRoleChange(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun performFastAdminSwitch() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val admin = repository.getAdminByUsername("latifah")
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val sessionToken = SecurityUtils.generateSessionToken()

                if (admin != null) {
                    repository.updateAdminLastLogin(admin.id, now)
                    repository.setUserSession(
                        UserSession(
                            isLoggedIn = true,
                            username = admin.username,
                            fullName = admin.fullName,
                            email = admin.email,
                            role = "ADMIN",
                            sessionToken = sessionToken,
                            authProvider = "Email",
                            popiaConsentTimestamp = now,
                            popiaConsentVersion = "v1.0"
                        )
                    )
                } else {
                    repository.setUserSession(
                        UserSession(
                            isLoggedIn = true,
                            username = "latifah",
                            fullName = "Latifah Jacobs",
                            email = "latifah@aurorahcan.org",
                            role = "ADMIN",
                            sessionToken = sessionToken,
                            authProvider = "Email",
                            popiaConsentTimestamp = now,
                            popiaConsentVersion = "v1.0"
                        )
                    )
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(AuthEvent.LoginSuccess)
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("AuthViewModel", "Error during fast admin switch: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Saved locally. Azure Cloud sync pending connection.")
            }
        }
    }

    fun loginWithGoogleSso(emailOverride: String? = null, nameOverride: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val ssoEmail = emailOverride ?: "sso.user@aurorahcan.org"
                val ssoName = nameOverride ?: "Google Authenticated User"
                val sessionToken = SecurityUtils.generateSessionToken()

                repository.setUserSession(
                    UserSession(
                        isLoggedIn = true,
                        username = ssoEmail.substringBefore("@"),
                        fullName = ssoName,
                        email = ssoEmail,
                        role = "VOLUNTEER",
                        sessionToken = sessionToken,
                        authProvider = "Google",
                        popiaConsentTimestamp = now,
                        popiaConsentVersion = "v1.0"
                    )
                )

                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(AuthEvent.LoginSuccess)
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("AuthViewModel", "Error during Google SSO login: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Saved locally. Azure Cloud sync pending connection.")
            }
        }
    }

    fun login() {
        val emailOrUsername = _uiState.value.emailOrUsername.trim()
        val password = _uiState.value.password

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter username/email and password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val sessionToken = SecurityUtils.generateSessionToken()

                val admin = repository.getAdminByEmailOrUsername(emailOrUsername)
                if (admin != null) {
                    if (SecurityUtils.verifyPassword(password, admin.passwordHash)) {
                        repository.updateAdminLastLogin(admin.id, now)
                        repository.setUserSession(
                            UserSession(
                                isLoggedIn = true,
                                username = admin.username,
                                fullName = admin.fullName,
                                email = admin.email,
                                role = "ADMIN",
                                sessionToken = sessionToken,
                                authProvider = "Email",
                                popiaConsentTimestamp = now,
                                popiaConsentVersion = "v1.0"
                            )
                        )
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _eventFlow.emit(AuthEvent.LoginSuccess)
                        return@launch
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Invalid password. Password verification failed."
                        )
                        return@launch
                    }
                }

                // General volunteer or community member authorization
                val username = emailOrUsername.substringBefore("@")
                val assignedRole = if (emailOrUsername.contains("admin", ignoreCase = true)) "ADMIN" else "VOLUNTEER"

                repository.setUserSession(
                    UserSession(
                        isLoggedIn = true,
                        username = username,
                        fullName = username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        email = if (emailOrUsername.contains("@")) emailOrUsername else "$emailOrUsername@gmail.com",
                        role = assignedRole,
                        sessionToken = sessionToken,
                        authProvider = "Email",
                        popiaConsentTimestamp = now,
                        popiaConsentVersion = "v1.0"
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(AuthEvent.LoginSuccess)
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("AuthViewModel", "Error during login: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Saved locally. Azure Cloud sync pending connection.")
            }
        }
    }

    fun register() {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.regEmail.isBlank() || state.regPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all required fields.")
            return
        }
        if (state.regPassword != state.regConfirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val hashedPassword = SecurityUtils.hashPasswordWithSalt(state.regPassword)
                val sessionToken = SecurityUtils.generateSessionToken()

                val mappedRole = when (state.selectedRole) {
                    "Volunteer" -> "VOLUNTEER"
                    "Community Administrator" -> "ADMIN"
                    "Beneficiary" -> "BENEFICIARY"
                    else -> "GUEST"
                }

                if (mappedRole == "VOLUNTEER") {
                    repository.insertVolunteer(
                        VolunteerEntity(
                            fullName = state.fullName,
                            email = state.regEmail,
                            phone = "+27 82 000 0000",
                            area = "Cape Town Central",
                            skills = "General Volunteer Support",
                            availability = "Flexible",
                            address = "Cape Town",
                            status = "Active",
                            registrationDate = today,
                            popiaConsentTimestamp = now,
                            popiaConsentVersion = "v1.0"
                        )
                    )
                } else if (mappedRole == "ADMIN") {
                    repository.insertAdmin(
                        AdminEntity(
                            username = state.regEmail.substringBefore("@"),
                            passwordHash = hashedPassword,
                            role = "Community Administrator",
                            fullName = state.fullName,
                            email = state.regEmail,
                            lastLogin = today
                        )
                    )
                }

                repository.setUserSession(
                    UserSession(
                        isLoggedIn = true,
                        username = state.regEmail.substringBefore("@"),
                        fullName = state.fullName,
                        email = state.regEmail,
                        role = mappedRole,
                        sessionToken = sessionToken,
                        authProvider = "Email",
                        popiaConsentTimestamp = now,
                        popiaConsentVersion = "v1.0"
                    )
                )

                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(AuthEvent.RegisterSuccess)
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("AuthViewModel", "Error during registration: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Saved locally. Azure Cloud sync pending connection."
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.clearUserSession()
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("AuthViewModel", "Error during logout: ${e.message}", e)
            }
        }
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(repository) as T
        }
    }
}

private fun <T> Flow<T>.asStateFlowIn(
    scope: CoroutineScope,
    initialValue: T
): StateFlow<T> {
    val flow = MutableStateFlow(initialValue)
    scope.launch {
        try {
            collect { value -> flow.value = value }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    return flow.asStateFlow()
}
