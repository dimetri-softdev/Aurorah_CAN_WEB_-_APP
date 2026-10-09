package com.example.aurorah.ui.impact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.repository.AurorahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

data class ImpactUiState(
    val selectedReport: ReportEntity? = null,
    val downloadNotificationMessage: String? = null
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

    fun triggerReportDownload(title: String) {
        _uiState.value = _uiState.value.copy(
            downloadNotificationMessage = "Generating & downloading $title (PDF)..."
        )
    }

    fun dismissDownloadMessage() {
        _uiState.value = _uiState.value.copy(downloadNotificationMessage = null)
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImpactViewModel(repository) as T
        }
    }
}
