package com.example.aurorah.ui.impact

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.repository.AurorahRepository
import com.example.aurorah.util.PdfReportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class ImpactUiState(
    val selectedReport: ReportEntity? = null,
    val downloadNotificationMessage: String? = null,
    val downloadedPdfFile: File? = null
)

class ImpactViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImpactUiState())
    val uiState: StateFlow<ImpactUiState> = _uiState.asStateFlow()

    val reportsList: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalDonations: StateFlow<Double?> = repository.getTotalDonationsAmount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 350000.0
        )

    fun selectReport(report: ReportEntity) {
        _uiState.value = _uiState.value.copy(selectedReport = report)
    }

    fun dismissReportModal() {
        _uiState.value = _uiState.value.copy(selectedReport = null)
    }

    fun triggerReportDownload(context: Context, report: ReportEntity) {
        viewModelScope.launch {
            val vols = repository.getAllVolunteers().first()
            val dons = repository.getAllDonations().first()
            val bens = repository.getAllBeneficiaries().first()

            val pdfFile = PdfReportManager.generateReportPdf(
                context = context,
                reportTitle = report.title,
                reportType = report.type,
                summary = report.summary,
                volunteers = vols,
                donations = dons,
                beneficiaries = bens
            )

            _uiState.value = _uiState.value.copy(
                downloadedPdfFile = pdfFile,
                downloadNotificationMessage = if (pdfFile != null) "Successfully compiled ${report.title} (PDF)." else "Downloaded ${report.title}."
            )
        }
    }

    fun openDownloadedPdf(context: Context) {
        val file = _uiState.value.downloadedPdfFile ?: return
        PdfReportManager.openPdfFile(context, file)
    }

    fun dismissDownloadMessage() {
        _uiState.value = _uiState.value.copy(
            downloadNotificationMessage = null,
            downloadedPdfFile = null
        )
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImpactViewModel(repository) as T
        }
    }
}
