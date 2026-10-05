package com.example.aurorah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beneficiaries")
data class BeneficiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val area: String,
    val familySize: Int,
    val assistanceNeeded: String,
    val urgency: String,
    val registrationDate: String,
    val status: String = "Pending"
)
