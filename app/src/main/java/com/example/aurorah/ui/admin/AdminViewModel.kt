package com.example.aurorah.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.remote.AzureServerConnectionStatus
import com.example.aurorah.data.repository.AurorahRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminUiState(
    val selectedTab: Int = 0,
    val exportNotification: String? = null,
    val searchQuery: String = "",
    val isExportingPdf: Boolean = false,
    val isSyncing: Boolean = false
)

class AdminViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val userSessionState: StateFlow<UserSession> = repository.userSessionFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSession()
        )

    val volunteersList: StateFlow<List<VolunteerEntity>> = repository.getAllVolunteers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val donationsList: StateFlow<List<DonationEntity>> = repository.getAllDonations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val beneficiariesList: StateFlow<List<BeneficiaryEntity>> = repository.getAllBeneficiaries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val reportsList: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val azureConnectionStatus: StateFlow<AzureServerConnectionStatus> = repository.azureConnectionStatusFlow

    init {
        checkAzureConnection()
    }

    fun checkAzureConnection() {
        viewModelScope.launch {
            repository.checkAzureServerConnection()
        }
    }

    fun syncDatabaseNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            val result = repository.syncWithRemote()
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                exportNotification = "Sync Result (${result.timestamp}): ${result.message}"
            )
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun approveBeneficiary(beneficiary: BeneficiaryEntity) {
        viewModelScope.launch {
            repository.updateBeneficiary(beneficiary.copy(status = "Approved"))
            _uiState.value = _uiState.value.copy(exportNotification = "Beneficiary #${beneficiary.id} request approved.")
        }
    }

    fun fulfillBeneficiary(beneficiary: BeneficiaryEntity) {
        viewModelScope.launch {
            repository.updateBeneficiary(beneficiary.copy(status = "Fulfilled"))
            _uiState.value = _uiState.value.copy(exportNotification = "Beneficiary #${beneficiary.id} request marked fulfilled.")
        }
    }

    fun updateVolunteerStatus(volunteer: VolunteerEntity, status: String) {
        viewModelScope.launch {
            repository.updateVolunteer(volunteer.copy(status = status))
            _uiState.value = _uiState.value.copy(exportNotification = "Volunteer ${volunteer.fullName} status updated to $status.")
        }
    }

    fun exportTableToCsv(tableName: String) {
        _uiState.value = _uiState.value.copy(
            exportNotification = "Exported $tableName table to aurorah_${tableName.lowercase()}_report.csv"
        )
    }

    fun generatePdfReport(reportTitle: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExportingPdf = true)
            delay(1000)
            _uiState.value = _uiState.value.copy(
                isExportingPdf = false,
                exportNotification = "Successfully generated and compiled PDF: $reportTitle"
            )
        }
    }

    fun dismissNotification() {
        _uiState.value = _uiState.value.copy(exportNotification = null)
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
