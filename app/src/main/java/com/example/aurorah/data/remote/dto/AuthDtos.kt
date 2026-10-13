package com.example.aurorah.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GoogleSsoRequestDto(
    @field:Json(name = "idToken") val idToken: String
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequestDto(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "oldPassword") val oldPassword: String,
    @field:Json(name = "newPassword") val newPassword: String
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "role") val role: String
)

@JsonClass(generateAdapter = true)
data class VolunteerSignupRequestDto(
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "phone") val phone: String,
    @field:Json(name = "skills") val skills: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "availability") val availability: String
)

@JsonClass(generateAdapter = true)
data class AssistanceRequestDto(
    @field:Json(name = "contactName") val contactName: String,
    @field:Json(name = "contactPhone") val contactPhone: String,
    @field:Json(name = "area") val area: String,
    @field:Json(name = "category") val category: String,
    @field:Json(name = "requestDetails") val requestDetails: String,
    @field:Json(name = "urgency") val urgency: String
)

/** Server shape for register, login and Google sign-in: {success, message, token, user}. */
@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @field:Json(name = "success") val success: Boolean = true,
    @field:Json(name = "message") val message: String = "",
    @field:Json(name = "token") val token: String,
    @field:Json(name = "user") val user: UserDto
)

@JsonClass(generateAdapter = true)
data class OfferAssistanceRequestDto(
    @field:Json(name = "needId") val needId: Int,
    @field:Json(name = "userEmail") val userEmail: String,
    @field:Json(name = "volunteerSkill") val volunteerSkill: String,
    @field:Json(name = "notes") val notes: String = ""
)

@JsonClass(generateAdapter = true)
data class DonationRequestDto(
    @field:Json(name = "category") val category: String,
    @field:Json(name = "amount") val amount: Double,
    @field:Json(name = "paymentMethod") val paymentMethod: String,
    @field:Json(name = "userEmail") val userEmail: String
)

@JsonClass(generateAdapter = true)
data class ApiMessageDto(
    @field:Json(name = "success") val success: Boolean = true,
    @field:Json(name = "message") val message: String = ""
)
