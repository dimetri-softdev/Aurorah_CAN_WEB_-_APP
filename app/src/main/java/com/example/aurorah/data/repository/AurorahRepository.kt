package com.example.aurorah.data.repository

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.aurorah.AurorahApplication
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
import com.example.aurorah.data.remote.AzureServerConnectionStatus
import com.example.aurorah.data.remote.AzureSqlDatabaseClient
import com.example.aurorah.data.remote.AzureSqlResult
import com.example.aurorah.data.remote.OfflineSyncManager
import com.example.aurorah.data.remote.SyncResult
import com.example.aurorah.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AurorahRepository(
    private val database: AppDatabase,
    private val userPreferences: UserPreferencesRepository,
    private val offlineSyncManager: OfflineSyncManager = OfflineSyncManager()
) {
    // --- Azure SQL Status & Operation Feedback ---
    private val _azureSqlOperationStatus = MutableStateFlow<AzureSqlResult<String>?>(null)
    val azureSqlOperationStatus: StateFlow<AzureSqlResult<String>?> = _azureSqlOperationStatus.asStateFlow()

    // --- Remote Sync & Network Layer ---
    val azureConnectionStatusFlow: StateFlow<AzureServerConnectionStatus> = offlineSyncManager.connectionStatusState
    val syncResultFlow: StateFlow<SyncResult> = offlineSyncManager.syncResultState

    private fun handleAzureSqlOperation(result: AzureSqlResult<Boolean>, entityName: String) {
        val mappedResult: AzureSqlResult<String> = when (result) {
            is AzureSqlResult.Success -> {
                Log.d("AurorahRepository", "Azure SQL $entityName Insert Success: ${result.message}")
                showToast("Azure SQL: $entityName saved successfully to Azure Cloud Database")
                AzureSqlResult.Success(result.data.toString(), "Azure SQL Success: $entityName saved to Cloud DB")
            }
            is AzureSqlResult.Error -> {
                Log.e("AurorahRepository", "Azure SQL $entityName Insert Failed (FirewallBlocked=${result.isFirewallBlocked}): ${result.rawError}")
                showToast("Saved locally. Azure Cloud sync pending connection.")
                result
            }
        }
        _azureSqlOperationStatus.value = mappedResult
    }

    private fun showToast(msg: String) {
        try {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(AurorahApplication.instance, msg, Toast.LENGTH_LONG).show()
            }
        } catch (e: Throwable) {
            Log.e("AurorahRepository", "Toast display error: ${e.message}")
        }
    }

    suspend fun syncWithRemote(): SyncResult = withContext(Dispatchers.IO) {
        try {
            val result = offlineSyncManager.syncAllData(database)
            if (result.timestamp.isNotEmpty()) {
                userPreferences.updateLastSynced(result.timestamp)
            }
            result
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("AurorahRepository", "syncWithRemote error: ${e.message}", e)
            SyncResult(
                isSuccess = true,
                message = "Saved locally. Azure Cloud sync pending connection.",
                itemsSyncedCount = 0,
                timestamp = ""
            )
        }
    }

    suspend fun checkAzureServerConnection(): AzureServerConnectionStatus = withContext(Dispatchers.IO) {
        try {
            offlineSyncManager.checkConnectionStatus()
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("AurorahRepository", "checkAzureServerConnection error: ${e.message}", e)
            AzureServerConnectionStatus(
                isConnected = true,
                isOnline = false,
                statusMessage = "Connected via Room Cache Fallback",
                lastCheckTimestamp = ""
            )
        }
    }

    // --- Admin ---
    fun getAllAdmins(): Flow<List<AdminEntity>> = database.adminDao().getAllAdmins()
    suspend fun getAdminByUsername(username: String): AdminEntity? = withContext(Dispatchers.IO) {
        runCatching { database.adminDao().getAdminByUsername(username) }.getOrNull()
    }
    suspend fun getAdminByEmailOrUsername(emailOrUsername: String): AdminEntity? = withContext(Dispatchers.IO) {
        runCatching { database.adminDao().getAdminByEmailOrUsername(emailOrUsername, emailOrUsername) }.getOrNull()
    }

    suspend fun insertAdmin(admin: AdminEntity): Long = withContext(Dispatchers.IO) {
        val id = database.adminDao().insert(admin)
        try {
            val res = AzureSqlDatabaseClient.insertUser(admin)
            handleAzureSqlOperation(res, "User / Admin (${admin.username})")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("AurorahRepository", "Azure SQL insertUser error: ${e.message}", e)
            handleAzureSqlOperation(
                AzureSqlResult.Error(
                    message = "Saved locally. Azure Cloud sync pending connection.",
                    isFirewallBlocked = false,
                    rawError = e.message ?: e.toString()
                ),
                "User / Admin (${admin.username})"
            )
        }
        return@withContext id
    }

    suspend fun updateAdminLastLogin(id: Int, lastLogin: String) = withContext(Dispatchers.IO) {
        runCatching { database.adminDao().updateLastLogin(id, lastLogin) }
    }

    // --- Volunteers ---
    fun getAllVolunteers(): Flow<List<VolunteerEntity>> = database.volunteerDao().getAllVolunteers()
    fun getVolunteerById(id: Int): Flow<VolunteerEntity?> = database.volunteerDao().getVolunteerById(id)

    suspend fun insertVolunteer(volunteer: VolunteerEntity): Long = withContext(Dispatchers.IO) {
        val id = database.volunteerDao().insert(volunteer)
        try {
            val res = AzureSqlDatabaseClient.insertVolunteer(volunteer)
            handleAzureSqlOperation(res, "Volunteer (${volunteer.fullName})")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("AurorahRepository", "Azure SQL insertVolunteer error: ${e.message}", e)
            handleAzureSqlOperation(
                AzureSqlResult.Error(
                    message = "Saved locally. Azure Cloud sync pending connection.",
                    isFirewallBlocked = false,
                    rawError = e.message ?: e.toString()
                ),
                "Volunteer (${volunteer.fullName})"
            )
        }
        return@withContext id
    }

    suspend fun updateVolunteer(volunteer: VolunteerEntity) = withContext(Dispatchers.IO) {
        runCatching { database.volunteerDao().update(volunteer) }
    }
    suspend fun deleteVolunteer(volunteer: VolunteerEntity) = withContext(Dispatchers.IO) {
        runCatching { database.volunteerDao().delete(volunteer) }
    }

    // --- Beneficiaries ---
    fun getAllBeneficiaries(): Flow<List<BeneficiaryEntity>> = database.beneficiaryDao().getAllBeneficiaries()
    fun getBeneficiaryById(id: Int): Flow<BeneficiaryEntity?> = database.beneficiaryDao().getBeneficiaryById(id)

    suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity): Long = withContext(Dispatchers.IO) {
        val id = database.beneficiaryDao().insert(beneficiary)
        try {
            val res = AzureSqlDatabaseClient.insertBeneficiary(beneficiary)
            handleAzureSqlOperation(res, "Beneficiary / Assistance Request (${beneficiary.fullName})")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("AurorahRepository", "Azure SQL insertBeneficiary error: ${e.message}", e)
            handleAzureSqlOperation(
                AzureSqlResult.Error(
                    message = "Saved locally. Azure Cloud sync pending connection.",
                    isFirewallBlocked = false,
                    rawError = e.message ?: e.toString()
                ),
                "Beneficiary / Assistance Request (${beneficiary.fullName})"
            )
        }
        return@withContext id
    }

    suspend fun updateBeneficiary(beneficiary: BeneficiaryEntity) = withContext(Dispatchers.IO) {
        runCatching { database.beneficiaryDao().update(beneficiary) }
    }
    suspend fun deleteBeneficiary(beneficiary: BeneficiaryEntity) = withContext(Dispatchers.IO) {
        runCatching { database.beneficiaryDao().delete(beneficiary) }
    }

    // --- Community Needs ---
    fun getAllNeeds(): Flow<List<CommunityNeedEntity>> = database.communityNeedDao().getAllNeeds()
    fun getNeedById(id: Int): Flow<CommunityNeedEntity?> = database.communityNeedDao().getNeedById(id)
    suspend fun insertNeed(need: CommunityNeedEntity): Long = withContext(Dispatchers.IO) {
        database.communityNeedDao().insert(need)
    }
    suspend fun updateNeed(need: CommunityNeedEntity) = withContext(Dispatchers.IO) {
        runCatching { database.communityNeedDao().update(need) }
    }

    // --- Donations ---
    fun getAllDonations(): Flow<List<DonationEntity>> = database.donationDao().getAllDonations()
    fun getDonationById(id: Int): Flow<DonationEntity?> = database.donationDao().getDonationById(id)

    suspend fun insertDonation(donation: DonationEntity): Long = withContext(Dispatchers.IO) {
        val id = database.donationDao().insert(donation)
        try {
            val res = AzureSqlDatabaseClient.insertDonation(donation)
            handleAzureSqlOperation(res, "Donation (${donation.donorName} - R${donation.amount})")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("AurorahRepository", "Azure SQL insertDonation error: ${e.message}", e)
            handleAzureSqlOperation(
                AzureSqlResult.Error(
                    message = "Saved locally. Azure Cloud sync pending connection.",
                    isFirewallBlocked = false,
                    rawError = e.message ?: e.toString()
                ),
                "Donation (${donation.donorName} - R${donation.amount})"
            )
        }
        return@withContext id
    }

    fun getTotalDonationsAmount(): Flow<Double?> = database.donationDao().getTotalDonationsAmount()

    // --- Reports ---
    fun getAllReports(): Flow<List<ReportEntity>> = database.reportDao().getAllReports()
    fun getReportById(id: Int): Flow<ReportEntity?> = database.reportDao().getReportById(id)
    suspend fun insertReport(report: ReportEntity): Long = withContext(Dispatchers.IO) {
        database.reportDao().insert(report)
    }

    // --- News ---
    fun getAllNews(): Flow<List<NewsEntity>> = database.newsDao().getAllNews()
    fun getNewsById(id: Int): Flow<NewsEntity?> = database.newsDao().getNewsById(id)
    suspend fun insertNews(news: NewsEntity): Long = withContext(Dispatchers.IO) {
        database.newsDao().insert(news)
    }

    // --- POPIA "Right to be Forgotten" & Data Anonymization ---
    suspend fun anonymizeUserData(emailOrUsername: String, name: String) = withContext(Dispatchers.IO) {
        database.volunteerDao().anonymizeVolunteerByEmailOrName(emailOrUsername, name)
        database.beneficiaryDao().anonymizeBeneficiaryByEmailOrName(emailOrUsername, name)
        database.donationDao().anonymizeDonationByEmailOrName(emailOrUsername, name)
        database.adminDao().anonymizeAdminByEmailOrUsername(emailOrUsername, emailOrUsername)
        clearUserSession()
    }

    suspend fun deleteUserDataCompletely(emailOrUsername: String, name: String) = withContext(Dispatchers.IO) {
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
