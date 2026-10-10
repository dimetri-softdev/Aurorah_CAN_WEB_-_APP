package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.NewsEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "author") val author: String,
    @field:Json(name = "publishDate") val publishDate: String,
    @field:Json(name = "category") val category: String,
    @field:Json(name = "urgency") val urgency: String
)

fun NewsDto.toEntity(): NewsEntity = NewsEntity(
    id = id,
    title = title,
    content = content,
    author = author,
    publishDate = publishDate,
    category = category,
    urgency = urgency
)

fun NewsEntity.toDto(): NewsDto = NewsDto(
    id = id,
    title = title,
    content = content,
    author = author,
    publishDate = publishDate,
    category = category,
    urgency = urgency
)
