package com.example.aurorah.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

object SecurityUtils {

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = "$salt:$password".toByteArray(Charsets.UTF_8)
        val digest = md.digest(input)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun hashPasswordWithSalt(password: String): String {
        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        return "$salt:$hash"
    }

    fun verifyPassword(password: String, storedPasswordHash: String): Boolean {
        if (storedPasswordHash.isEmpty()) return false

        // Plaintext fallback for seeded default passwords
        if (storedPasswordHash == password) {
            return true
        }

        return if (storedPasswordHash.contains(":")) {
            val parts = storedPasswordHash.split(":", limit = 2)
            if (parts.size == 2) {
                val salt = parts[0]
                val hash = parts[1]
                val computedHash = hashPassword(password, salt)
                computedHash == hash
            } else {
                false
            }
        } else {
            // Unsalted SHA-256 comparison
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(password.toByteArray(Charsets.UTF_8))
            val computed = digest.joinToString("") { "%02x".format(it) }
            computed == storedPasswordHash
        }
    }

    fun generateSessionToken(): String {
        return "aurorah_sess_" + UUID.randomUUID().toString().replace("-", "")
    }
}
