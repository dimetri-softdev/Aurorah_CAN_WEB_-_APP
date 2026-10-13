package com.example.aurorah.data.remote

import com.example.aurorah.data.remote.dto.ApiMessageDto
import com.example.aurorah.data.remote.dto.ChangePasswordRequestDto
import com.example.aurorah.data.remote.dto.CommunityNeedDto
import com.example.aurorah.data.remote.dto.DonationRequestDto
import com.example.aurorah.data.remote.dto.GoogleSsoRequestDto
import com.example.aurorah.data.remote.dto.LoginRequestDto
import com.example.aurorah.data.remote.dto.NewsDto
import com.example.aurorah.data.remote.dto.OfferAssistanceRequestDto
import com.example.aurorah.data.remote.dto.RegisterRequestDto
import com.example.aurorah.data.remote.dto.AssistanceRequestDto
import com.example.aurorah.data.remote.dto.AuthResponseDto
import com.example.aurorah.data.remote.dto.VolunteerSignupRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Mirrors the FastAPI mobile routes under /api. The app never talks to the database directly. */
interface AurorahApiService {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/auth/google-sso")
    suspend fun googleSso(@Body body: GoogleSsoRequestDto): AuthResponseDto

    @POST("api/volunteers")
    suspend fun submitVolunteer(@Body body: VolunteerSignupRequestDto): ApiMessageDto

    @POST("api/assistance-requests")
    suspend fun submitAssistanceRequest(@Body body: AssistanceRequestDto): ApiMessageDto

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequestDto): ApiMessageDto

    @GET("api/news")
    suspend fun getNews(): List<NewsDto>

    @GET("api/needs")
    suspend fun getNeeds(): List<CommunityNeedDto>

    @POST("api/needs/offer-assistance")
    suspend fun offerAssistance(@Body body: OfferAssistanceRequestDto): ApiMessageDto

    @POST("api/donations")
    suspend fun createDonation(@Body body: DonationRequestDto): ApiMessageDto
}
