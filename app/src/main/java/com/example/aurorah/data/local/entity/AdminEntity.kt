package com.example.aurorah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: String,
    val fullName: String,
    val email: String,
    val lastLogin: String
)
