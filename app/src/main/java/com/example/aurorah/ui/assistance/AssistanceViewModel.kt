package com.example.aurorah.ui.assistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.repository.AurorahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AssistanceUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "+27 ",
    val selectedArea: String = "Kuils River",
    val selectedAssistanceType: String = "Food Parcels & Hampers",
    val describeNeed: String = "",
    val urgencyLevel: String = "Normal", // Normal, Urgent, Emergency
    val familySize: String = "3",
    val popiaConsented: Boolean = false,
    val isLoading: Boolean = false,
    val referenceNumber: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AssistanceViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistanceUiState())
    val uiState: StateFlow<AssistanceUiState> = _uiState.asStateFlow()

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

    fun onAssistanceTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(selectedAssistanceType = value)
    }

    fun onDescribeNeedChange(value: String) {
        _uiState.value = _uiState.value.copy(describeNeed = value, errorMessage = null)
    }

    fun onUrgencyChange(value: String) {
        _uiState.value = _uiState.value.copy(urgencyLevel = value)
    }

    fun onFamilySizeChange(value: String) {
        _uiState.value = _uiState.value.copy(familySize = value)
    }

    fun onPopiaConsentChange(consented: Boolean) {
        _uiState.value = _uiState.value.copy(popiaConsented = consented, errorMessage = null)
    }

    fun submitRequest() {
        val state = _uiState.value
        if (state.firstName.isBlank() || state.lastName.isBlank() || state.describeNeed.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter your Name and describe your need.")
            return
        }
        if (!state.popiaConsented) {
            _uiState.value = state.copy(errorMessage = "You must accept the POPIA consent to submit a request.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val fullName = "${state.firstName.trim()} ${state.lastName.trim()}"
            val refCode = "AUR-${(1000..9999).random()}"

            repository.insertBeneficiary(
                BeneficiaryEntity(
                    fullName = fullName,
                    email = if (state.email.isBlank()) "applicant@aurorahcan.org" else state.email.trim(),
                    phone = state.phone.trim(),
                    address = "${state.selectedArea}, Cape Town",
                    area = state.selectedArea,
                    familySize = state.familySize.toIntOrNull() ?: 1,
                    assistanceNeeded = "${state.selectedAssistanceType}: ${state.describeNeed.trim()}",
                    urgency = state.urgencyLevel,
                    registrationDate = today,
                    status = "Pending",
                    popiaConsentTimestamp = now,
                    popiaConsentVersion = "v1.0"
                )
            )

            _uiState.value = AssistanceUiState(
                referenceNumber = refCode,
                successMessage = "Your request (#$refCode) has been submitted. Our team will contact you shortly."
            )
        }
    }

    fun dismissSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, referenceNumber = null)
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssistanceViewModel(repository) as T
        }
    }
}
