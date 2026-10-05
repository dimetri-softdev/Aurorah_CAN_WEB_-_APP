package com.example.aurorah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val type: String,
    val generatedDate: String,
    val status: String,
    val exportFormat: String,
    val summary: String
)
