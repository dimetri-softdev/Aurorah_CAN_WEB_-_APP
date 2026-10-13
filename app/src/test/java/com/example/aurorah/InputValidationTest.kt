package com.example.aurorah

import com.example.aurorah.util.InputValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidationTest {

    @Test
    fun isValidEmail_acceptsValidEmailAddresses() {
        assertTrue(InputValidator.isValidEmail("latifah@aurorahcan.org"))
        assertTrue(InputValidator.isValidEmail("test.user@gmail.com"))
        assertTrue(InputValidator.isValidEmail("info@sub.domain.co.za"))
    }

    @Test
    fun isValidEmail_rejectsInvalidEmailAddresses() {
        assertFalse(InputValidator.isValidEmail("invalidemail"))
        assertFalse(InputValidator.isValidEmail("missingdomain@"))
        assertFalse(InputValidator.isValidEmail("@missingusername.com"))
        assertFalse(InputValidator.isValidEmail(""))
        assertFalse(InputValidator.isValidEmail(null))
    }

    @Test
    fun isValidPhone_acceptsValidSouthAfricanPhoneNumbers() {
        assertTrue(InputValidator.isValidPhone("+27 82 123 4567"))
        assertTrue(InputValidator.isValidPhone("0821234567"))
        assertTrue(InputValidator.isValidPhone("+27219030000"))
        assertTrue(InputValidator.isValidPhone("N/A"))
    }

    @Test
    fun isValidPhone_rejectsInvalidPhoneNumbers() {
        assertFalse(InputValidator.isValidPhone("123"))
        assertFalse(InputValidator.isValidPhone("abcdefghij"))
        assertFalse(InputValidator.isValidPhone(""))
        assertFalse(InputValidator.isValidPhone(null))
    }

    @Test
    fun validateRequiredField_returnsTrueForNonEmptyStrings() {
        assertTrue(InputValidator.validateRequiredField("Latifah Jacobs"))
        assertTrue(InputValidator.validateRequiredField("Kuils River"))
    }

    @Test
    fun validateRequiredField_returnsFalseForBlankOrNull() {
        assertFalse(InputValidator.validateRequiredField(""))
        assertFalse(InputValidator.validateRequiredField("   "))
        assertFalse(InputValidator.validateRequiredField(null))
    }

    @Test
    fun isValidDonationAmount_acceptsPositiveAmounts() {
        assertTrue(InputValidator.isValidDonationAmount(100.0))
        assertTrue(InputValidator.isValidDonationAmount(25000.50))
    }

    @Test
    fun isValidDonationAmount_rejectsZeroNegativeOrNaN() {
        assertFalse(InputValidator.isValidDonationAmount(0.0))
        assertFalse(InputValidator.isValidDonationAmount(-50.0))
        assertFalse(InputValidator.isValidDonationAmount(Double.NaN))
        assertFalse(InputValidator.isValidDonationAmount(null))
    }

    @Test
    fun validateRegistrationForm_returnsTrueForValidRegistrationInputs() {
        val isValid = InputValidator.validateRegistrationForm(
            fullName = "Amina Adams",
            email = "amina.adams@gmail.com",
            password = "SecurePassword123",
            confirmPassword = "SecurePassword123"
        )
        assertTrue(isValid)
    }

    @Test
    fun validateRegistrationForm_returnsFalseWhenPasswordsMismatch() {
        val isValid = InputValidator.validateRegistrationForm(
            fullName = "Amina Adams",
            email = "amina.adams@gmail.com",
            password = "SecurePassword123",
            confirmPassword = "DifferentPassword123"
        )
        assertFalse(isValid)
    }

    @Test
    fun validateRegistrationForm_returnsFalseWhenEmailIsInvalid() {
        val isValid = InputValidator.validateRegistrationForm(
            fullName = "Amina Adams",
            email = "notanemail",
            password = "SecurePassword123",
            confirmPassword = "SecurePassword123"
        )
        assertFalse(isValid)
    }
}
