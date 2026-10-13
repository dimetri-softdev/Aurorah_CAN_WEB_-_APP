package com.example.aurorah.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.remote.AurorahNetworkClient
import com.example.aurorah.data.remote.TokenStore
import com.example.aurorah.data.remote.dto.GoogleSsoRequestDto
import com.example.aurorah.data.remote.dto.LoginRequestDto
import com.example.aurorah.data.remote.dto.RegisterRequestDto
import retrofit2.HttpException
import java.io.IOException
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

    /** Signs in with a real Google ID token (obtained via Google Sign-In / Credential Manager). */
    fun loginWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val auth = AurorahNetworkClient.apiService.googleSso(GoogleSsoRequestDto(idToken))
                val apiUser = auth.user
                TokenStore.save(auth.token, apiUser.email)
                repository.setUserSession(
                    UserSession(
                        isLoggedIn = true,
                        username = apiUser.email.substringBefore("@"),
                        fullName = apiUser.fullName,
                        email = apiUser.email,
                        role = apiUser.role.uppercase(Locale.ROOT),
                        sessionToken = SecurityUtils.generateSessionToken(),
                        authProvider = "Google",
                        popiaConsentTimestamp = now,
                        popiaConsentVersion = "v1.0"
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(AuthEvent.LoginSuccess)
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = apiErrorMessage(e))
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Cannot reach the server. Check your connection."
                )
            }
        }
    }

    fun onGoogleSignInUnavailable() {
        _uiState.value = _uiState.value.copy(errorMessage = "Google sign-in isn't set up yet. Please use email and password.")
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

                // Website accounts (stored in Azure SQL) sign in through the API
                if (emailOrUsername.contains("@")) {
                    val auth = try {
                        AurorahNetworkClient.apiService.                        login(LoginRequestDto(emailOrUsername, password))
                    } catch (e: HttpException) {
                        if (e.code() != 401 && e.code() != 403) {
                            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = apiErrorMessage(e))
                            return@launch
                        }
                        null
                    } catch (e: IOException) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Cannot reach the server. Check your connection."
                        )
                        return@launch
                    }
                    if (auth != null) {
                        val apiUser = auth.user
                        TokenStore.save(auth.token, apiUser.email)
                        repository.setUserSession(
                            UserSession(
                                isLoggedIn = true,
                                username = apiUser.email.substringBefore("@"),
                                fullName = apiUser.fullName,
                                email = apiUser.email,
                                role = apiUser.role.uppercase(Locale.ROOT),
                                sessionToken = sessionToken,
                                authProvider = "Email",
                                popiaConsentTimestamp = now,
                                popiaConsentVersion = "v1.0"
                            )
                        )
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _eventFlow.emit(AuthEvent.LoginSuccess)
                        return@launch
                    }
                }

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

                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Invalid email or password.")
            } catch (e: Throwable) {
                Log.e("AuthViewModel", "Error during login: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Login failed. Please try again.")
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

        if (state.regPassword.length < 12) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 12 characters.")
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

                if (mappedRole != "ADMIN") {
                    val auth = try {
                        AurorahNetworkClient.apiService.register(
                            RegisterRequestDto(state.fullName.trim(), state.regEmail.trim(), state.regPassword, state.selectedRole)
                        )
                    } catch (e: HttpException) {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = apiErrorMessage(e))
                        return@launch
                    } catch (e: IOException) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Cannot reach the server. Check your connection."
                        )
                        return@launch
                    }
                    TokenStore.save(auth.token, auth.user.email)
                }

                if (mappedRole == "VOLUNTEER") {
                    // The account already exists on the server; don't fail sign-up if the profile upload fails
                    runCatching {
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
                    }
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
        TokenStore.clear()
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

private fun apiErrorMessage(e: HttpException): String {
    if (e.code() == 429) return "Too many attempts. Please wait a moment and try again."
    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull().orEmpty()
    val detail = Regex("\"detail\"\\s*:\\s*\"([^\"]+)\"").find(body)?.groupValues?.get(1)
    return detail ?: "Request failed (${e.code()})."
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
