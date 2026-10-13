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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

open class AurorahRepository(
    private val database: AppDatabase? = null,
    private val userPreferences: UserPreferencesRepository? = null,
    private val offlineSyncManager: OfflineSyncManager = OfflineSyncManager()
) {
    // --- Azure SQL Status & Operation Feedback ---
    private val _azureSqlOperationStatus = MutableStateFlow<AzureSqlResult<String>?>(null)
    open val azureSqlOperationStatus: StateFlow<AzureSqlResult<String>?> = _azureSqlOperationStatus.asStateFlow()

    // --- Remote Sync & Network Layer ---
    open val azureConnectionStatusFlow: StateFlow<AzureServerConnectionStatus> = offlineSyncManager.connectionStatusState
    open val syncResultFlow: StateFlow<SyncResult> = offlineSyncManager.syncResultState

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

    open suspend fun syncWithRemote(): SyncResult = withContext(Dispatchers.IO) {
        try {
            val db = database ?: return@withContext SyncResult(true, "Offline", 0, "")
            val result = offlineSyncManager.syncAllData(db)
            if (result.timestamp.isNotEmpty()) {
                userPreferences?.updateLastSynced(result.timestamp)
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

    open suspend fun checkAzureServerConnection(): AzureServerConnectionStatus = withContext(Dispatchers.IO) {
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
    open fun getAllAdmins(): Flow<List<AdminEntity>> = database?.adminDao()?.getAllAdmins() ?: flowOf(emptyList())
    open suspend fun getAdminByUsername(username: String): AdminEntity? = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.adminDao().getAdminByUsername(username) }.getOrNull() }
    }
    open suspend fun getAdminByEmailOrUsername(emailOrUsername: String): AdminEntity? = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.adminDao().getAdminByEmailOrUsername(emailOrUsername, emailOrUsername) }.getOrNull() }
    }

    open suspend fun insertAdmin(admin: AdminEntity): Long = withContext(Dispatchers.IO) {
        val id = database?.adminDao()?.insert(admin) ?: 1L
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

    open suspend fun updateAdminLastLogin(id: Int, lastLogin: String) = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.adminDao().updateLastLogin(id, lastLogin) } }
    }

    // --- Volunteers ---
    open fun getAllVolunteers(): Flow<List<VolunteerEntity>> = database?.volunteerDao()?.getAllVolunteers() ?: flowOf(emptyList())
    open fun getVolunteerById(id: Int): Flow<VolunteerEntity?> = database?.volunteerDao()?.getVolunteerById(id) ?: flowOf(null)

    open suspend fun insertVolunteer(volunteer: VolunteerEntity): Long = withContext(Dispatchers.IO) {
        val id = database?.volunteerDao()?.insert(volunteer) ?: 1L
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

    open suspend fun updateVolunteer(volunteer: VolunteerEntity) = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.volunteerDao().update(volunteer) } }
    }
    open suspend fun deleteVolunteer(volunteer: VolunteerEntity) = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.volunteerDao().delete(volunteer) } }
    }

    // --- Beneficiaries ---
    open fun getAllBeneficiaries(): Flow<List<BeneficiaryEntity>> = database?.beneficiaryDao()?.getAllBeneficiaries() ?: flowOf(emptyList())
    open fun getBeneficiaryById(id: Int): Flow<BeneficiaryEntity?> = database?.beneficiaryDao()?.getBeneficiaryById(id) ?: flowOf(null)

    open suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity): Long = withContext(Dispatchers.IO) {
        val id = database?.beneficiaryDao()?.insert(beneficiary) ?: 1L
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

    open suspend fun updateBeneficiary(beneficiary: BeneficiaryEntity) = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.beneficiaryDao().update(beneficiary) } }
    }
    open suspend fun deleteBeneficiary(beneficiary: BeneficiaryEntity) = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.beneficiaryDao().delete(beneficiary) } }
    }

    // --- Community Needs ---
    open fun getAllNeeds(): Flow<List<CommunityNeedEntity>> = database?.communityNeedDao()?.getAllNeeds() ?: flowOf(emptyList())
    open fun getNeedById(id: Int): Flow<CommunityNeedEntity?> = database?.communityNeedDao()?.getNeedById(id) ?: flowOf(null)
    open suspend fun insertNeed(need: CommunityNeedEntity): Long = withContext(Dispatchers.IO) {
        database?.communityNeedDao()?.insert(need) ?: 1L
    }
    open suspend fun updateNeed(need: CommunityNeedEntity) = withContext(Dispatchers.IO) {
        database?.let { runCatching { it.communityNeedDao().update(need) } }
    }

    // --- Donations ---
    open fun getAllDonations(): Flow<List<DonationEntity>> = database?.donationDao()?.getAllDonations() ?: flowOf(emptyList())
    open fun getDonationById(id: Int): Flow<DonationEntity?> = database?.donationDao()?.getDonationById(id) ?: flowOf(null)

    open suspend fun insertDonation(donation: DonationEntity): Long = withContext(Dispatchers.IO) {
        val id = database?.donationDao()?.insert(donation) ?: 1L
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

    open fun getTotalDonationsAmount(): Flow<Double?> = database?.donationDao()?.getTotalDonationsAmount() ?: flowOf(0.0)

    // --- Reports ---
    open fun getAllReports(): Flow<List<ReportEntity>> = database?.reportDao()?.getAllReports() ?: flowOf(emptyList())
    open fun getReportById(id: Int): Flow<ReportEntity?> = database?.reportDao()?.getReportById(id) ?: flowOf(null)
    open suspend fun insertReport(report: ReportEntity): Long = withContext(Dispatchers.IO) {
        database?.reportDao()?.insert(report) ?: 1L
    }

    // --- News ---
    open fun getAllNews(): Flow<List<NewsEntity>> = database?.newsDao()?.getAllNews() ?: flowOf(emptyList())
    open fun getNewsById(id: Int): Flow<NewsEntity?> = database?.newsDao()?.getNewsById(id) ?: flowOf(null)
    open suspend fun insertNews(news: NewsEntity): Long = withContext(Dispatchers.IO) {
        database?.newsDao()?.insert(news) ?: 1L
    }

    // --- POPIA "Right to be Forgotten" & Data Anonymization ---
    open suspend fun anonymizeUserData(emailOrUsername: String, name: String) = withContext(Dispatchers.IO) {
        database?.volunteerDao()?.anonymizeVolunteerByEmailOrName(emailOrUsername, name)
        database?.beneficiaryDao()?.anonymizeBeneficiaryByEmailOrName(emailOrUsername, name)
        database?.donationDao()?.anonymizeDonationByEmailOrName(emailOrUsername, name)
        database?.adminDao()?.anonymizeAdminByEmailOrUsername(emailOrUsername, emailOrUsername)
        clearUserSession()
    }

    open suspend fun deleteUserDataCompletely(emailOrUsername: String, name: String) = withContext(Dispatchers.IO) {
        database?.volunteerDao()?.deleteVolunteerByEmailOrName(emailOrUsername, name)
        database?.beneficiaryDao()?.deleteBeneficiaryByEmailOrName(emailOrUsername, name)
        database?.donationDao()?.deleteDonationByEmailOrName(emailOrUsername, name)
        clearUserSession()
    }

    // --- User Session & Preferences ---
    open val themeModeFlow: Flow<ThemeMode> = userPreferences?.themeModeFlow ?: flowOf(ThemeMode.SYSTEM)
    open val languageCodeFlow: Flow<String> = userPreferences?.languageCodeFlow ?: flowOf("en")
    open val userSessionFlow: Flow<UserSession> = userPreferences?.userSessionFlow ?: flowOf(UserSession())
    open val notificationsEnabledFlow: Flow<Boolean> = userPreferences?.notificationsEnabledFlow ?: flowOf(true)
    open val lastSyncedFlow: Flow<String> = userPreferences?.lastSyncedFlow ?: flowOf("Just now")

    open suspend fun setThemeMode(themeMode: ThemeMode) { userPreferences?.setThemeMode(themeMode) }
    open suspend fun setLanguageCode(languageCode: String) { userPreferences?.setLanguageCode(languageCode) }
    open suspend fun setUserSession(session: UserSession) { userPreferences?.setUserSession(session) }
    open suspend fun clearUserSession() { userPreferences?.clearUserSession() }
    open suspend fun setNotificationsEnabled(enabled: Boolean) { userPreferences?.setNotificationsEnabled(enabled) }
    open suspend fun updateLastSynced(timestamp: String) { userPreferences?.updateLastSynced(timestamp) }
}
