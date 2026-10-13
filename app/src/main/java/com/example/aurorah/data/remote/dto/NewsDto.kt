package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.NewsEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsDto(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "summary") val summary: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "category") val category: String
)

fun NewsDto.toEntity(): NewsEntity = NewsEntity(
    id = id,
    title = title,
    content = summary,
    author = "Aurorah Team",
    publishDate = timestamp,
    category = category,
    urgency = "Normal"
)
