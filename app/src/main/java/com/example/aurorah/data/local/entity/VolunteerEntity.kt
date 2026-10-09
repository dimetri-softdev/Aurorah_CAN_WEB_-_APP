package com.example.aurorah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val area: String,
    val skills: String,
    val availability: String,
    val address: String,
    val status: String = "Active",
    val registrationDate: String,
    val popiaConsentTimestamp: String = "",
    val popiaConsentVersion: String = "v1.0"
)
