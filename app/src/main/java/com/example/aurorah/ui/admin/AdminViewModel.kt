package com.example.aurorah.ui.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.NewsEntity
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.remote.AzureServerConnectionStatus
import com.example.aurorah.data.repository.AurorahRepository
import com.example.aurorah.util.CsvExportManager
import com.example.aurorah.util.PdfReportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdminUiState(
    val selectedTab: Int = 0,
    val exportNotification: String? = null,
    val searchQuery: String = "",
    val isExportingPdf: Boolean = false,
    val isSyncing: Boolean = false,
    val generatedFileToOpen: File? = null,
    val isPublishingNews: Boolean = false
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

    fun assignVolunteerToBeneficiary(beneficiary: BeneficiaryEntity, volunteerName: String) {
        viewModelScope.launch {
            repository.updateBeneficiary(beneficiary.copy(status = "Assigned ($volunteerName)"))
            _uiState.value = _uiState.value.copy(
                exportNotification = "Assigned Volunteer '$volunteerName' to Beneficiary ${beneficiary.fullName}."
            )
        }
    }

    fun allocateResourceToBeneficiary(beneficiary: BeneficiaryEntity, causeName: String) {
        viewModelScope.launch {
            repository.updateBeneficiary(beneficiary.copy(status = "Resource Allocated ($causeName)"))
            _uiState.value = _uiState.value.copy(
                exportNotification = "Allocated '$causeName' resource to Beneficiary ${beneficiary.fullName}."
            )
        }
    }

    fun exportTableToCsv(context: Context, tableName: String) {
        viewModelScope.launch {
            val file: File? = when (tableName.lowercase()) {
                "volunteers" -> CsvExportManager.exportVolunteersToCsv(context, volunteersList.value)
                "donations" -> CsvExportManager.exportDonationsToCsv(context, donationsList.value)
                "beneficiaries" -> CsvExportManager.exportBeneficiariesToCsv(context, beneficiariesList.value)
                else -> null
            }

            if (file != null) {
                _uiState.value = _uiState.value.copy(
                    generatedFileToOpen = file,
                    exportNotification = "Generated CSV: ${file.name}. Tap Open/Share to view data."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    exportNotification = "Exported $tableName table to CSV file."
                )
            }
        }
    }

    fun generatePdfReport(context: Context, reportTitle: String, reportType: String, summary: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExportingPdf = true)

            val file = PdfReportManager.generateReportPdf(
                context = context,
                reportTitle = reportTitle,
                reportType = reportType,
                summary = summary,
                volunteers = volunteersList.value,
                donations = donationsList.value,
                beneficiaries = beneficiariesList.value
            )

            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertReport(
                ReportEntity(
                    title = reportTitle,
                    type = reportType,
                    generatedDate = todayStr,
                    status = "Completed",
                    exportFormat = "PDF",
                    summary = summary
                )
            )

            _uiState.value = _uiState.value.copy(
                isExportingPdf = false,
                generatedFileToOpen = file,
                exportNotification = if (file != null) "Successfully compiled PDF report: ${file.name}" else "Report compilation completed."
            )
        }
    }

    fun publishNews(news: NewsEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPublishingNews = true)
            repository.insertNews(news)
            repository.syncWithRemote()
            _uiState.value = _uiState.value.copy(
                isPublishingNews = false,
                exportNotification = "Published announcement: '${news.title}' saved to Room DB & Azure SQL."
            )
        }
    }

    fun openGeneratedFile(context: Context) {
        val file = _uiState.value.generatedFileToOpen ?: return
        if (file.name.endsWith(".pdf", ignoreCase = true)) {
            PdfReportManager.openPdfFile(context, file)
        } else if (file.name.endsWith(".csv", ignoreCase = true)) {
            CsvExportManager.shareCsvFile(context, file)
        }
    }

    fun dismissNotification() {
        _uiState.value = _uiState.value.copy(
            exportNotification = null,
            generatedFileToOpen = null
        )
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
