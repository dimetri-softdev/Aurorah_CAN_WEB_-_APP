package com.example.aurorah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorName: String,
    val email: String,
    val phone: String,
    val type: String,
    val amount: Double,
    val cause: String,
    val isAnonymous: Boolean = false,
    val message: String = "",
    val paymentMethod: String,
    val date: String,
    val popiaConsentTimestamp: String = "",
    val popiaConsentVersion: String = "v1.0"
)
