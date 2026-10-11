package com.example.aurorah.ui.donate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.repository.AurorahRepository
import com.example.aurorah.util.PaymentGatewayManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DonateUiState(
    val selectedTierAmount: Double? = 200.0,
    val customAmountText: String = "200",
    val cause: String = "Emergency Food Relief Fund",
    val isAnonymous: Boolean = false,
    val donorName: String = "",
    val donorEmail: String = "",
    val donorPhone: String = "+27 ",
    val message: String = "",
    val paymentMethod: String = "Credit / Debit Card", // Card, EFT, SnapScan
    val popiaConsented: Boolean = false,
    val isProcessing: Boolean = false,
    val receiptData: DonationEntity? = null,
    val receiptPdfFile: File? = null,
    val errorMessage: String? = null
)

class DonateViewModel(
    private val repository: AurorahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonateUiState())
    val uiState: StateFlow<DonateUiState> = _uiState.asStateFlow()

    fun selectPresetTier(amount: Double) {
        _uiState.value = _uiState.value.copy(
            selectedTierAmount = amount,
            customAmountText = amount.toInt().toString(),
            errorMessage = null
        )
    }

    fun onCustomAmountChange(text: String) {
        val cleanText = text.filter { it.isDigit() || it == '.' }
        val amount = cleanText.toDoubleOrNull()
        _uiState.value = _uiState.value.copy(
            customAmountText = cleanText,
            selectedTierAmount = amount,
            errorMessage = null
        )
    }

    fun onCauseChange(value: String) {
        _uiState.value = _uiState.value.copy(cause = value)
    }

    fun onAnonymousChange(anonymous: Boolean) {
        _uiState.value = _uiState.value.copy(isAnonymous = anonymous)
    }

    fun onDonorNameChange(value: String) {
        _uiState.value = _uiState.value.copy(donorName = value, errorMessage = null)
    }

    fun onDonorEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(donorEmail = value, errorMessage = null)
    }

    fun onDonorPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(donorPhone = value)
    }

    fun onMessageChange(value: String) {
        _uiState.value = _uiState.value.copy(message = value)
    }

    fun onPaymentMethodChange(value: String) {
        _uiState.value = _uiState.value.copy(paymentMethod = value)
    }

    fun onPopiaConsentChange(consented: Boolean) {
        _uiState.value = _uiState.value.copy(popiaConsented = consented, errorMessage = null)
    }

    fun processDonation(context: Context) {
        val state = _uiState.value
        val amount = state.customAmountText.toDoubleOrNull() ?: 0.0

        if (amount < 10.0) {
            _uiState.value = state.copy(errorMessage = "Minimum donation amount is R10 (ZAR).")
            return
        }

        if (!state.isAnonymous && (state.donorName.isBlank() || state.donorEmail.isBlank())) {
            _uiState.value = state.copy(errorMessage = "Please enter your Name and Email address (or check Anonymous).")
            return
        }

        if (!state.popiaConsented) {
            _uiState.value = state.copy(errorMessage = "Please check the POPIA consent box to proceed.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isProcessing = true, errorMessage = null)
            delay(1200) // Simulate secure gateway processing

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val donation = DonationEntity(
                donorName = if (state.isAnonymous) "Anonymous Benefactor" else state.donorName.trim(),
                email = if (state.isAnonymous) "anonymous@aurorahcan.org" else state.donorEmail.trim(),
                phone = state.donorPhone.trim(),
                type = "Financial Support",
                amount = amount,
                cause = state.cause,
                isAnonymous = state.isAnonymous,
                message = state.message.ifBlank { "Supporting Kuils River & Cape Metro Community" },
                paymentMethod = state.paymentMethod,
                date = today,
                popiaConsentTimestamp = now,
                popiaConsentVersion = "v1.0"
            )

            val id = repository.insertDonation(donation)
            val savedDonation = donation.copy(id = id.toInt())

            val pdfFile = PaymentGatewayManager.generateSection18aReceiptPdf(context, savedDonation)

            _uiState.value = state.copy(
                isProcessing = false,
                receiptData = savedDonation,
                receiptPdfFile = pdfFile
            )
        }
    }

    fun openTaxReceiptPdf(context: Context) {
        val file = _uiState.value.receiptPdfFile ?: return
        PaymentGatewayManager.openTaxReceiptPdf(context, file)
    }

    fun dismissReceipt() {
        _uiState.value = DonateUiState()
    }

    class Factory(private val repository: AurorahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DonateViewModel(repository) as T
        }
    }
}
