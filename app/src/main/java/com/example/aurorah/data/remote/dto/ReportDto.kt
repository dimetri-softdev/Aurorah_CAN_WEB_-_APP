package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.ReportEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "generatedDate") val generatedDate: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "exportFormat") val exportFormat: String,
    @field:Json(name = "summary") val summary: String
)

fun ReportDto.toEntity(): ReportEntity = ReportEntity(
    id = id,
    title = title,
    type = type,
    generatedDate = generatedDate,
    status = status,
    exportFormat = exportFormat,
    summary = summary
)

fun ReportEntity.toDto(): ReportDto = ReportDto(
    id = id,
    title = title,
    type = type,
    generatedDate = generatedDate,
    status = status,
    exportFormat = exportFormat,
    summary = summary
)
