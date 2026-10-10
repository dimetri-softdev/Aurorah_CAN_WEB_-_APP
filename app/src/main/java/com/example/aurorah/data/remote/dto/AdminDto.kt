package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.AdminEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdminDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "passwordHash") val passwordHash: String,
    @field:Json(name = "role") val role: String,
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "lastLogin") val lastLogin: String
)

fun AdminDto.toEntity(): AdminEntity = AdminEntity(
    id = id,
    username = username,
    passwordHash = passwordHash,
    role = role,
    fullName = fullName,
    email = email,
    lastLogin = lastLogin
)

fun AdminEntity.toDto(): AdminDto = AdminDto(
    id = id,
    username = username,
    passwordHash = passwordHash,
    role = role,
    fullName = fullName,
    email = email,
    lastLogin = lastLogin
)
