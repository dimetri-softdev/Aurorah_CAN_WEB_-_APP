package com.example.aurorah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_needs")
data class CommunityNeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val assistanceType: String,
    val priorityLevel: String,
    val status: String = "Open",
    val area: String,
    val dateCreated: String,
    val assignedVolunteerId: Int? = null
)
