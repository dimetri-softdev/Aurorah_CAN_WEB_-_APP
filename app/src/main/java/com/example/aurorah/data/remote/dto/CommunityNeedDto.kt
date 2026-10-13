package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.CommunityNeedEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommunityNeedDto(
    @field:Json(name = "needId") val needId: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "priority") val priority: String,
    @field:Json(name = "category") val category: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "createdAt") val createdAt: String
)

fun CommunityNeedDto.toEntity(): CommunityNeedEntity = CommunityNeedEntity(
    id = needId,
    description = if (description.isBlank()) title else "$title - $description",
    assistanceType = category,
    priorityLevel = priority,
    status = status,
    area = location,
    dateCreated = createdAt,
    assignedVolunteerId = null
)
