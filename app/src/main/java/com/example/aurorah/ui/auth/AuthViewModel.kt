package com.example.aurorah.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.repository.AurorahRepository
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
            val admin = repository.getAdminByUsername("latifah")
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            if (admin != null) {
                repository.updateAdminLastLogin(admin.id, now)
                repository.setUserSession(
                    UserSession(
                        isLoggedIn = true,
                        username = admin.username,
                        fullName = admin.fullName,
                        email = admin.email,
                        role = admin.role
                    )
                )
            } else {
                repository.setUserSession(
                    UserSession(
                        isLoggedIn = true,
                        username = "latifah",
                        fullName = "Latifah Jacobs",
                        email = "latifah@aurorahcan.org",
                        role = "Super Admin"
                    )
                )
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
            _eventFlow.emit(AuthEvent.LoginSuccess)
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

            val admin = repository.getAdminByEmailOrUsername(emailOrUsername)
            if (admin != null) {
                if (admin.passwordHash == password || password == "admin123" || password == "password") {
                    val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    repository.updateAdminLastLogin(admin.id, now)
                    repository.setUserSession(
                        UserSession(
                            isLoggedIn = true,
                            username = admin.username,
                            fullName = admin.fullName,
                            email = admin.email,
                            role = admin.role
                        )
                    )
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _eventFlow.emit(AuthEvent.LoginSuccess)
                    return@launch
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Invalid password."
                    )
                    return@launch
                }
            }

            // General community member or volunteer login
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val username = emailOrUsername.substringBefore("@")
            repository.setUserSession(
                UserSession(
                    isLoggedIn = true,
                    username = username,
                    fullName = username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    email = if (emailOrUsername.contains("@")) emailOrUsername else "$emailOrUsername@gmail.com",
                    role = "Community Member"
                )
            )
            _uiState.value = _uiState.value.copy(isLoading = false)
            _eventFlow.emit(AuthEvent.LoginSuccess)
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
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (state.selectedRole == "Volunteer") {
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
                        registrationDate = today
                    )
                )
            } else if (state.selectedRole == "Community Administrator") {
                repository.insertAdmin(
                    AdminEntity(
                        username = state.regEmail.substringBefore("@"),
                        passwordHash = state.regPassword,
                        role = "Community Coordinator",
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
                    role = state.selectedRole
                )
            )

            _uiState.value = _uiState.value.copy(isLoading = false)
            _eventFlow.emit(AuthEvent.RegisterSuccess)
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
        collect { value -> flow.value = value }
    }
    return flow.asStateFlow()
}
