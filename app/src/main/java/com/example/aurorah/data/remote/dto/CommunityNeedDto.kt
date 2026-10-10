package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.CommunityNeedEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommunityNeedDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "assistanceType") val assistanceType: String,
    @field:Json(name = "priorityLevel") val priorityLevel: String,
    @field:Json(name = "status") val status: String = "Open",
    @field:Json(name = "area") val area: String,
    @field:Json(name = "dateCreated") val dateCreated: String,
    @field:Json(name = "assignedVolunteerId") val assignedVolunteerId: Int? = null
)

fun CommunityNeedDto.toEntity(): CommunityNeedEntity = CommunityNeedEntity(
    id = id,
    description = description,
    assistanceType = assistanceType,
    priorityLevel = priorityLevel,
    status = status,
    area = area,
    dateCreated = dateCreated,
    assignedVolunteerId = assignedVolunteerId
)

fun CommunityNeedEntity.toDto(): CommunityNeedDto = CommunityNeedDto(
    id = id,
    description = description,
    assistanceType = assistanceType,
    priorityLevel = priorityLevel,
    status = status,
    area = area,
    dateCreated = dateCreated,
    assignedVolunteerId = assignedVolunteerId
)
