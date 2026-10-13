package com.example.aurorah.util

object InputValidator {

    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )

    // Supports South African numbers (+27 xx xxx xxxx, 0xx xxx xxxx, or 10-14 digits)
    private val PHONE_REGEX = Regex(
        "^(\\+27|0)\\s?(\\d{2})\\s?(\\d{3})\\s?(\\d{4})$|^\\+?\\d{10,14}$"
    )

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return EMAIL_REGEX.matches(email.trim())
    }

    fun isValidPhone(phone: String?): Boolean {
        if (phone.isNullOrBlank()) return false
        val cleanPhone = phone.trim()
        if (cleanPhone.equals("N/A", ignoreCase = true)) return true
        return PHONE_REGEX.matches(cleanPhone)
    }

    fun validateRequiredField(value: String?): Boolean {
        return !value.isNullOrBlank()
    }

    fun isValidDonationAmount(amount: Double?): Boolean {
        return amount != null && amount > 0.0 && !amount.isNaN() && !amount.isInfinite()
    }

    fun validateRegistrationForm(
        fullName: String?,
        email: String?,
        password: String?,
        confirmPassword: String?
    ): Boolean {
        if (!validateRequiredField(fullName)) return false
        if (!isValidEmail(email)) return false
        if (!validateRequiredField(password) || (password?.length ?: 0) < 4) return false
        if (password != confirmPassword) return false
        return true
    }
}
