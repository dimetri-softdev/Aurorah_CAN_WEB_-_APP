package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BeneficiaryDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phone") val phone: String,
    @field:Json(name = "address") val address: String,
    @field:Json(name = "area") val area: String,
    @field:Json(name = "familySize") val familySize: Int,
    @field:Json(name = "assistanceNeeded") val assistanceNeeded: String,
    @field:Json(name = "urgency") val urgency: String,
    @field:Json(name = "registrationDate") val registrationDate: String,
    @field:Json(name = "status") val status: String = "Pending",
    @field:Json(name = "popiaConsentTimestamp") val popiaConsentTimestamp: String = "",
    @field:Json(name = "popiaConsentVersion") val popiaConsentVersion: String = "v1.0"
)

fun BeneficiaryDto.toEntity(): BeneficiaryEntity = BeneficiaryEntity(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    address = address,
    area = area,
    familySize = familySize,
    assistanceNeeded = assistanceNeeded,
    urgency = urgency,
    registrationDate = registrationDate,
    status = status,
    popiaConsentTimestamp = popiaConsentTimestamp,
    popiaConsentVersion = popiaConsentVersion
)

fun BeneficiaryEntity.toDto(): BeneficiaryDto = BeneficiaryDto(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    address = address,
    area = area,
    familySize = familySize,
    assistanceNeeded = assistanceNeeded,
    urgency = urgency,
    registrationDate = registrationDate,
    status = status,
    popiaConsentTimestamp = popiaConsentTimestamp,
    popiaConsentVersion = popiaConsentVersion
)
