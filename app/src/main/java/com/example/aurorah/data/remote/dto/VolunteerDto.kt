package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.VolunteerEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VolunteerDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phone") val phone: String,
    @field:Json(name = "area") val area: String,
    @field:Json(name = "skills") val skills: String,
    @field:Json(name = "availability") val availability: String,
    @field:Json(name = "address") val address: String,
    @field:Json(name = "status") val status: String = "Active",
    @field:Json(name = "registrationDate") val registrationDate: String,
    @field:Json(name = "popiaConsentTimestamp") val popiaConsentTimestamp: String = "",
    @field:Json(name = "popiaConsentVersion") val popiaConsentVersion: String = "v1.0"
)

fun VolunteerDto.toEntity(): VolunteerEntity = VolunteerEntity(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    area = area,
    skills = skills,
    availability = availability,
    address = address,
    status = status,
    registrationDate = registrationDate,
    popiaConsentTimestamp = popiaConsentTimestamp,
    popiaConsentVersion = popiaConsentVersion
)

fun VolunteerEntity.toDto(): VolunteerDto = VolunteerDto(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    area = area,
    skills = skills,
    availability = availability,
    address = address,
    status = status,
    registrationDate = registrationDate,
    popiaConsentTimestamp = popiaConsentTimestamp,
    popiaConsentVersion = popiaConsentVersion
)
