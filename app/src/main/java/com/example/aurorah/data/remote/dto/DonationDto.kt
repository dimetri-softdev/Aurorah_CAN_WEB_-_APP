package com.example.aurorah.data.remote.dto

import com.example.aurorah.data.local.entity.DonationEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DonationDto(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "donorName") val donorName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phone") val phone: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "amount") val amount: Double,
    @field:Json(name = "cause") val cause: String,
    @field:Json(name = "isAnonymous") val isAnonymous: Boolean = false,
    @field:Json(name = "message") val message: String = "",
    @field:Json(name = "paymentMethod") val paymentMethod: String,
    @field:Json(name = "date") val date: String,
    @field:Json(name = "popiaConsentTimestamp") val popiaConsentTimestamp: String = "",
    @field:Json(name = "popiaConsentVersion") val popiaConsentVersion: String = "v1.0"
)

fun DonationDto.toEntity(): DonationEntity = DonationEntity(
    id = id,
    donorName = donorName,
    email = email,
    phone = phone,
    type = type,
    amount = amount,
    cause = cause,
    isAnonymous = isAnonymous,
    message = message,
    paymentMethod = paymentMethod,
    date = date,
    popiaConsentTimestamp = popiaConsentTimestamp,
    popiaConsentVersion = popiaConsentVersion
)

fun DonationEntity.toDto(): DonationDto = DonationDto(
    id = id,
    donorName = donorName,
    email = email,
    phone = phone,
    type = type,
    amount = amount,
    cause = cause,
    isAnonymous = isAnonymous,
    message = message,
    paymentMethod = paymentMethod,
    date = date,
    popiaConsentTimestamp = popiaConsentTimestamp,
    popiaConsentVersion = popiaConsentVersion
)
