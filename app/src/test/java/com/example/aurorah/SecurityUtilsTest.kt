package com.example.aurorah

import com.example.aurorah.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun generateSalt_returnsNonEmpty32CharHexString() {
        val salt = SecurityUtils.generateSalt()
        assertNotNull(salt)
        assertEquals(32, salt.length) // 16 bytes = 32 hex chars
    }

    @Test
    fun hashPassword_returnsDeterministicHashForSameInputAndSalt() {
        val password = "SecretPassword123"
        val salt = "abcdef0123456789abcdef0123456789"

        val hash1 = SecurityUtils.hashPassword(password, salt)
        val hash2 = SecurityUtils.hashPassword(password, salt)

        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // SHA-256 = 64 hex chars
    }

    @Test
    fun hashPasswordWithSalt_generatesSaltedFormat() {
        val password = "MySecurePassword"
        val saltedHash = SecurityUtils.hashPasswordWithSalt(password)

        assertTrue(saltedHash.contains(":"))
        val parts = saltedHash.split(":")
        assertEquals(2, parts.size)
        assertEquals(32, parts[0].length) // salt
        assertEquals(64, parts[1].length) // hash
    }

    @Test
    fun verifyPassword_returnsTrueForCorrectPassword() {
        val password = "CorrectPassword1!"
        val saltedHash = SecurityUtils.hashPasswordWithSalt(password)

        val isValid = SecurityUtils.verifyPassword(password, saltedHash)
        assertTrue(isValid)
    }

    @Test
    fun verifyPassword_returnsFalseForIncorrectPassword() {
        val password = "CorrectPassword1!"
        val saltedHash = SecurityUtils.hashPasswordWithSalt(password)

        val isValid = SecurityUtils.verifyPassword("WrongPassword", saltedHash)
        assertFalse(isValid)
    }

    @Test
    fun verifyPassword_supportsPlaintextFallbackForSeededDefaults() {
        val plaintextSeededPass = "admin123"
        assertTrue(SecurityUtils.verifyPassword("admin123", plaintextSeededPass))
        assertFalse(SecurityUtils.verifyPassword("wrongpass", plaintextSeededPass))
    }

    @Test
    fun generateSessionToken_returnsValidPrefixedToken() {
        val token = SecurityUtils.generateSessionToken()
        assertNotNull(token)
        assertTrue(token.startsWith("aurorah_sess_"))
        assertTrue(token.length > 20)
    }
}
