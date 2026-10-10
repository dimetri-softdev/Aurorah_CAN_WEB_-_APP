package com.example.aurorah.data.remote

import com.example.aurorah.data.remote.dto.AdminDto
import com.example.aurorah.data.remote.dto.BeneficiaryDto
import com.example.aurorah.data.remote.dto.CommunityNeedDto
import com.example.aurorah.data.remote.dto.DonationDto
import com.example.aurorah.data.remote.dto.NewsDto
import com.example.aurorah.data.remote.dto.ReportDto
import com.example.aurorah.data.remote.dto.VolunteerDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AurorahApiService {

    // --- Server Health / Azure SQL Status ---
    @GET("api/health")
    suspend fun checkHealth(): Response<Map<String, String>>

    // --- Volunteers ---
    @GET("api/volunteers")
    suspend fun getVolunteers(): List<VolunteerDto>

    @GET("api/volunteers/{id}")
    suspend fun getVolunteerById(@Path("id") id: Int): VolunteerDto

    @POST("api/volunteers")
    suspend fun createVolunteer(@Body volunteer: VolunteerDto): VolunteerDto

    @PUT("api/volunteers/{id}")
    suspend fun updateVolunteer(@Path("id") id: Int, @Body volunteer: VolunteerDto): VolunteerDto

    @DELETE("api/volunteers/{id}")
    suspend fun deleteVolunteer(@Path("id") id: Int): Response<Unit>

    // --- Beneficiaries ---
    @GET("api/beneficiaries")
    suspend fun getBeneficiaries(): List<BeneficiaryDto>

    @GET("api/beneficiaries/{id}")
    suspend fun getBeneficiaryById(@Path("id") id: Int): BeneficiaryDto

    @POST("api/beneficiaries")
    suspend fun createBeneficiary(@Body beneficiary: BeneficiaryDto): BeneficiaryDto

    @PUT("api/beneficiaries/{id}")
    suspend fun updateBeneficiary(@Path("id") id: Int, @Body beneficiary: BeneficiaryDto): BeneficiaryDto

    @DELETE("api/beneficiaries/{id}")
    suspend fun deleteBeneficiary(@Path("id") id: Int): Response<Unit>

    // --- Donations ---
    @GET("api/donations")
    suspend fun getDonations(): List<DonationDto>

    @GET("api/donations/{id}")
    suspend fun getDonationById(@Path("id") id: Int): DonationDto

    @POST("api/donations")
    suspend fun createDonation(@Body donation: DonationDto): DonationDto

    // --- Community Needs ---
    @GET("api/needs")
    suspend fun getNeeds(): List<CommunityNeedDto>

    @GET("api/needs/{id}")
    suspend fun getNeedById(@Path("id") id: Int): CommunityNeedDto

    @POST("api/needs")
    suspend fun createNeed(@Body need: CommunityNeedDto): CommunityNeedDto

    @PUT("api/needs/{id}")
    suspend fun updateNeed(@Path("id") id: Int, @Body need: CommunityNeedDto): CommunityNeedDto

    // --- Admins ---
    @GET("api/admins")
    suspend fun getAdmins(): List<AdminDto>

    @POST("api/admins")
    suspend fun createAdmin(@Body admin: AdminDto): AdminDto

    // --- News ---
    @GET("api/news")
    suspend fun getNews(): List<NewsDto>

    @POST("api/news")
    suspend fun createNews(@Body news: NewsDto): NewsDto

    // --- Reports ---
    @GET("api/reports")
    suspend fun getReports(): List<ReportDto>

    @POST("api/reports")
    suspend fun createReport(@Body report: ReportDto): ReportDto
}
