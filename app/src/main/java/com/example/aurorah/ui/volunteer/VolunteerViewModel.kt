package com.example.aurorah.ui.volunteer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.repository.AurorahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class VolunteerUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "+27 ",
    val selectedArea: String = "Kuils River",
    val skills: String = "",
    val selectedAvailability: String = "Weekends & Evenings",
    val communityExperience: String = "",
    val popiaConsented: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class VolunteerViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VolunteerUiState())
    val uiState: StateFlow<VolunteerUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, errorMessage = null)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, errorMessage = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, errorMessage = null)
    }

    fun onAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(selectedArea = value)
    }

    fun onSkillsChange(value: String) {
        _uiState.value = _uiState.value.copy(skills = value, errorMessage = null)
    }

    fun onAvailabilityChange(value: String) {
        _uiState.value = _uiState.value.copy(selectedAvailability = value)
    }

    fun onCommunityExperienceChange(value: String) {
        _uiState.value = _uiState.value.copy(communityExperience = value)
    }

    fun onPopiaConsentChange(consented: Boolean) {
        _uiState.value = _uiState.value.copy(popiaConsented = consented, errorMessage = null)
    }

    fun submitRegistration() {
        val state = _uiState.value
        if (state.firstName.isBlank() || state.lastName.isBlank() || state.email.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please complete all required fields (First Name, Last Name, Email).")
            return
        }
        if (!state.popiaConsented) {
            _uiState.value = state.copy(errorMessage = "You must accept the POPIA consent to proceed with registration.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val fullName = "${state.firstName.trim()} ${state.lastName.trim()}"

            repository.insertVolunteer(
                VolunteerEntity(
                    fullName = fullName,
                    email = state.email.trim(),
                    phone = state.phone.trim(),
                    area = state.selectedArea,
                    skills = if (state.skills.isBlank()) "General Volunteer Support" else state.skills.trim(),
                    availability = state.selectedAvailability,
                    address = "${state.selectedArea}, Cape Town",
                    status = "Active",
                    registrationDate = today
                )
            )

            _uiState.value = VolunteerUiState(
                successMessage = "Thank you $fullName! Your volunteer registration has been successfully submitted."
            )
        }
    }

    fun dismissSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VolunteerViewModel(repository) as T
        }
    }
}
