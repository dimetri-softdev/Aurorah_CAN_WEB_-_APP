package com.example.aurorah.data.repository

import com.example.aurorah.data.local.AppDatabase
import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.CommunityNeedEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.NewsEntity
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserPreferencesRepository
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

class AurorahRepository(
    private val database: AppDatabase,
    private val userPreferences: UserPreferencesRepository
) {
    // --- Admin ---
    fun getAllAdmins(): Flow<List<AdminEntity>> = database.adminDao().getAllAdmins()
    suspend fun getAdminByUsername(username: String): AdminEntity? = database.adminDao().getAdminByUsername(username)
    suspend fun getAdminByEmailOrUsername(emailOrUsername: String): AdminEntity? =
        database.adminDao().getAdminByEmailOrUsername(emailOrUsername, emailOrUsername)
    suspend fun insertAdmin(admin: AdminEntity): Long = database.adminDao().insert(admin)
    suspend fun updateAdminLastLogin(id: Int, lastLogin: String) = database.adminDao().updateLastLogin(id, lastLogin)

    // --- Volunteers ---
    fun getAllVolunteers(): Flow<List<VolunteerEntity>> = database.volunteerDao().getAllVolunteers()
    fun getVolunteerById(id: Int): Flow<VolunteerEntity?> = database.volunteerDao().getVolunteerById(id)
    suspend fun insertVolunteer(volunteer: VolunteerEntity): Long = database.volunteerDao().insert(volunteer)
    suspend fun updateVolunteer(volunteer: VolunteerEntity) = database.volunteerDao().update(volunteer)
    suspend fun deleteVolunteer(volunteer: VolunteerEntity) = database.volunteerDao().delete(volunteer)

    // --- Beneficiaries ---
    fun getAllBeneficiaries(): Flow<List<BeneficiaryEntity>> = database.beneficiaryDao().getAllBeneficiaries()
    fun getBeneficiaryById(id: Int): Flow<BeneficiaryEntity?> = database.beneficiaryDao().getBeneficiaryById(id)
    suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity): Long = database.beneficiaryDao().insert(beneficiary)
    suspend fun updateBeneficiary(beneficiary: BeneficiaryEntity) = database.beneficiaryDao().update(beneficiary)
    suspend fun deleteBeneficiary(beneficiary: BeneficiaryEntity) = database.beneficiaryDao().delete(beneficiary)

    // --- Community Needs ---
    fun getAllNeeds(): Flow<List<CommunityNeedEntity>> = database.communityNeedDao().getAllNeeds()
    fun getNeedById(id: Int): Flow<CommunityNeedEntity?> = database.communityNeedDao().getNeedById(id)
    suspend fun insertNeed(need: CommunityNeedEntity): Long = database.communityNeedDao().insert(need)
    suspend fun updateNeed(need: CommunityNeedEntity) = database.communityNeedDao().update(need)

    // --- Donations ---
    fun getAllDonations(): Flow<List<DonationEntity>> = database.donationDao().getAllDonations()
    fun getDonationById(id: Int): Flow<DonationEntity?> = database.donationDao().getDonationById(id)
    suspend fun insertDonation(donation: DonationEntity): Long = database.donationDao().insert(donation)
    fun getTotalDonationsAmount(): Flow<Double?> = database.donationDao().getTotalDonationsAmount()

    // --- Reports ---
    fun getAllReports(): Flow<List<ReportEntity>> = database.reportDao().getAllReports()
    fun getReportById(id: Int): Flow<ReportEntity?> = database.reportDao().getReportById(id)
    suspend fun insertReport(report: ReportEntity): Long = database.reportDao().insert(report)

    // --- News ---
    fun getAllNews(): Flow<List<NewsEntity>> = database.newsDao().getAllNews()
    fun getNewsById(id: Int): Flow<NewsEntity?> = database.newsDao().getNewsById(id)
    suspend fun insertNews(news: NewsEntity): Long = database.newsDao().insert(news)

    // --- POPIA "Right to be Forgotten" & Data Anonymization ---
    suspend fun anonymizeUserData(emailOrUsername: String, name: String) {
        database.volunteerDao().anonymizeVolunteerByEmailOrName(emailOrUsername, name)
        database.beneficiaryDao().anonymizeBeneficiaryByEmailOrName(emailOrUsername, name)
        database.donationDao().anonymizeDonationByEmailOrName(emailOrUsername, name)
        database.adminDao().anonymizeAdminByEmailOrUsername(emailOrUsername, emailOrUsername)
        clearUserSession()
    }

    suspend fun deleteUserDataCompletely(emailOrUsername: String, name: String) {
        database.volunteerDao().deleteVolunteerByEmailOrName(emailOrUsername, name)
        database.beneficiaryDao().deleteBeneficiaryByEmailOrName(emailOrUsername, name)
        database.donationDao().deleteDonationByEmailOrName(emailOrUsername, name)
        clearUserSession()
    }

    // --- User Session & Preferences ---
    val themeModeFlow: Flow<ThemeMode> = userPreferences.themeModeFlow
    val languageCodeFlow: Flow<String> = userPreferences.languageCodeFlow
    val userSessionFlow: Flow<UserSession> = userPreferences.userSessionFlow
    val notificationsEnabledFlow: Flow<Boolean> = userPreferences.notificationsEnabledFlow
    val lastSyncedFlow: Flow<String> = userPreferences.lastSyncedFlow

    suspend fun setThemeMode(themeMode: ThemeMode) = userPreferences.setThemeMode(themeMode)
    suspend fun setLanguageCode(languageCode: String) = userPreferences.setLanguageCode(languageCode)
    suspend fun setUserSession(session: UserSession) = userPreferences.setUserSession(session)
    suspend fun clearUserSession() = userPreferences.clearUserSession()
    suspend fun setNotificationsEnabled(enabled: Boolean) = userPreferences.setNotificationsEnabled(enabled)
    suspend fun updateLastSynced(timestamp: String) = userPreferences.updateLastSynced(timestamp)
}
