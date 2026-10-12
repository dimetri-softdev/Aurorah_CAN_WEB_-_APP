package com.example.aurorah.data.remote

import android.util.Log
import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

sealed interface AzureSqlResult<out T> {
    data class Success<out T>(val data: T, val message: String) : AzureSqlResult<T>
    data class Error(val message: String, val isFirewallBlocked: Boolean, val rawError: String) : AzureSqlResult<Nothing>
}

object AzureSqlDatabaseClient {
    private const val TAG = "AzureSqlDatabaseClient"

    const val AZURE_SQL_URL =
        "jdbc:sqlserver://aurorah-sql-server.database.windows.net:1433;database=aurorah-db;user=aurorahadmin@aurorah-sql-server;password=Gdnproject4@;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

    private const val DRIVER_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    private const val DRIVER_JTDS = "net.sourceforge.jtds.jdbc.Driver"

    init {
        try {
            Class.forName(DRIVER_MSSQL)
            Log.d(TAG, "Loaded MSSQL JDBC Driver successfully")
        } catch (e: Exception) {
            Log.w(TAG, "MSSQL Driver load error, attempting JTDS driver: ${e.message}")
            try {
                Class.forName(DRIVER_JTDS)
                Log.d(TAG, "Loaded JTDS Driver successfully")
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to load JDBC driver: ${e2.message}")
            }
        }
    }

    private fun getConnection(): Connection {
        DriverManager.setLoginTimeout(30)
        return DriverManager.getConnection(AZURE_SQL_URL)
    }

    private fun handleException(e: Exception, action: String): AzureSqlResult.Error {
        val rawMsg = e.message ?: "Unknown SQL error"
        Log.e(TAG, "Azure SQL Error during $action: $rawMsg", e)
        val isFirewall = rawMsg.contains("firewall", ignoreCase = true) ||
                rawMsg.contains("Client with IP address", ignoreCase = true) ||
                rawMsg.contains("cannot open server", ignoreCase = true) ||
                rawMsg.contains("40615", ignoreCase = true) ||
                rawMsg.contains("not allowed to access", ignoreCase = true)

        val formattedMessage = if (isFirewall) {
            "Azure SQL Firewall Blocked Mobile IP. To fix in Azure Portal:\n" +
                    "1. Open Azure Portal -> SQL Servers -> 'aurorah-sql-server'\n" +
                    "2. Go to 'Networking' / 'Firewall rules'\n" +
                    "3. Click 'Add client IP' or turn on 'Allow Azure services and resources to access this server'\n" +
                    "4. Save and retry."
        } else {
            "Azure SQL Error during $action: $rawMsg"
        }
        return AzureSqlResult.Error(
            message = formattedMessage,
            isFirewallBlocked = isFirewall,
            rawError = rawMsg
        )
    }

    suspend fun insertVolunteer(entity: VolunteerEntity): AzureSqlResult<Boolean> = withContext(Dispatchers.IO) {
        val sql = """
            INSERT INTO Volunteers (FullName, Email, PhoneNumber, Area, Skills, Availability, Address, Status, RegistrationDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, entity.fullName)
                    stmt.setString(2, entity.email)
                    stmt.setString(3, entity.phone)
                    stmt.setString(4, entity.area)
                    stmt.setString(5, entity.skills)
                    stmt.setString(6, entity.availability)
                    stmt.setString(7, entity.address)
                    stmt.setString(8, entity.status)
                    stmt.setString(9, entity.registrationDate)
                    val rows = stmt.executeUpdate()
                    Log.d(TAG, "insertVolunteer Azure SQL success: $rows row(s) inserted")
                    AzureSqlResult.Success(true, "Volunteer successfully saved to Azure Cloud Database")
                }
            }
        } catch (e: Exception) {
            handleException(e, "insertVolunteer")
        }
    }

    suspend fun insertBeneficiary(entity: BeneficiaryEntity): AzureSqlResult<Boolean> = withContext(Dispatchers.IO) {
        val sql = """
            INSERT INTO Beneficiaries (FullName, Email, PhoneNumber, Address, Area, FamilySize, AssistanceNeeded, Urgency, Status, RegistrationDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, entity.fullName)
                    stmt.setString(2, entity.email)
                    stmt.setString(3, entity.phone)
                    stmt.setString(4, entity.address)
                    stmt.setString(5, entity.area)
                    stmt.setInt(6, entity.familySize)
                    stmt.setString(7, entity.assistanceNeeded)
                    stmt.setString(8, entity.urgency)
                    stmt.setString(9, entity.status)
                    stmt.setString(10, entity.registrationDate)
                    val rows = stmt.executeUpdate()
                    Log.d(TAG, "insertBeneficiary Azure SQL success: $rows row(s) inserted")
                    AzureSqlResult.Success(true, "Assistance request successfully saved to Azure Cloud Database")
                }
            }
        } catch (e: Exception) {
            handleException(e, "insertBeneficiary")
        }
    }

    suspend fun insertDonation(entity: DonationEntity): AzureSqlResult<Boolean> = withContext(Dispatchers.IO) {
        val sql = """
            INSERT INTO Donations (DonorName, Email, PhoneNumber, Type, Amount, Cause, IsAnonymous, Message, PaymentMethod, DonationDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, entity.donorName)
                    stmt.setString(2, entity.email)
                    stmt.setString(3, entity.phone)
                    stmt.setString(4, entity.type)
                    stmt.setDouble(5, entity.amount)
                    stmt.setString(6, entity.cause)
                    stmt.setInt(7, if (entity.isAnonymous) 1 else 0)
                    stmt.setString(8, entity.message)
                    stmt.setString(9, entity.paymentMethod)
                    stmt.setString(10, entity.date)
                    val rows = stmt.executeUpdate()
                    Log.d(TAG, "insertDonation Azure SQL success: $rows row(s) inserted")
                    AzureSqlResult.Success(true, "Donation record successfully saved to Azure Cloud Database")
                }
            }
        } catch (e: Exception) {
            handleException(e, "insertDonation")
        }
    }

    suspend fun insertUser(admin: AdminEntity): AzureSqlResult<Boolean> = withContext(Dispatchers.IO) {
        val sql = """
            INSERT INTO Admin (Username, Password, Role, FullName, Email, LastLogin)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, admin.username)
                    stmt.setString(2, admin.passwordHash)
                    stmt.setString(3, admin.role)
                    stmt.setString(4, admin.fullName)
                    stmt.setString(5, admin.email)
                    stmt.setString(6, admin.lastLogin)
                    val rows = stmt.executeUpdate()
                    Log.d(TAG, "insertUser Azure SQL success: $rows row(s) inserted")
                    AzureSqlResult.Success(true, "User successfully registered in Azure Cloud Database")
                }
            }
        } catch (e: Exception) {
            handleException(e, "insertUser")
        }
    }

    suspend fun fetchAllVolunteers(): AzureSqlResult<List<VolunteerEntity>> = withContext(Dispatchers.IO) {
        val sql = "SELECT * FROM Volunteers"
        val list = mutableListOf<VolunteerEntity>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            list.add(
                                VolunteerEntity(
                                    id = runCatching { rs.getInt("Id") }.getOrDefault(0),
                                    fullName = runCatching { rs.getString("FullName") }.getOrDefault(""),
                                    email = runCatching { rs.getString("Email") }.getOrDefault(""),
                                    phone = runCatching { rs.getString("PhoneNumber") }.getOrDefault(""),
                                    area = runCatching { rs.getString("Area") }.getOrDefault(""),
                                    skills = runCatching { rs.getString("Skills") }.getOrDefault(""),
                                    availability = runCatching { rs.getString("Availability") }.getOrDefault(""),
                                    address = runCatching { rs.getString("Address") }.getOrDefault(""),
                                    status = runCatching { rs.getString("Status") }.getOrDefault("Active"),
                                    registrationDate = runCatching { rs.getString("RegistrationDate") }.getOrDefault("")
                                )
                            )
                        }
                    }
                }
            }
            AzureSqlResult.Success(list, "Fetched ${list.size} volunteers from Azure SQL")
        } catch (e: Exception) {
            handleException(e, "fetchAllVolunteers")
        }
    }

    suspend fun fetchAllBeneficiaries(): AzureSqlResult<List<BeneficiaryEntity>> = withContext(Dispatchers.IO) {
        val sql = "SELECT * FROM Beneficiaries"
        val list = mutableListOf<BeneficiaryEntity>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            list.add(
                                BeneficiaryEntity(
                                    id = runCatching { rs.getInt("Id") }.getOrDefault(0),
                                    fullName = runCatching { rs.getString("FullName") }.getOrDefault(""),
                                    email = runCatching { rs.getString("Email") }.getOrDefault(""),
                                    phone = runCatching { rs.getString("PhoneNumber") }.getOrDefault(""),
                                    address = runCatching { rs.getString("Address") }.getOrDefault(""),
                                    area = runCatching { rs.getString("Area") }.getOrDefault(""),
                                    familySize = runCatching { rs.getInt("FamilySize") }.getOrDefault(1),
                                    assistanceNeeded = runCatching { rs.getString("AssistanceNeeded") }.getOrDefault(""),
                                    urgency = runCatching { rs.getString("Urgency") }.getOrDefault("Normal"),
                                    registrationDate = runCatching { rs.getString("RegistrationDate") }.getOrDefault(""),
                                    status = runCatching { rs.getString("Status") }.getOrDefault("Pending")
                                )
                            )
                        }
                    }
                }
            }
            AzureSqlResult.Success(list, "Fetched ${list.size} beneficiaries from Azure SQL")
        } catch (e: Exception) {
            handleException(e, "fetchAllBeneficiaries")
        }
    }

    suspend fun fetchAllDonations(): AzureSqlResult<List<DonationEntity>> = withContext(Dispatchers.IO) {
        val sql = "SELECT * FROM Donations"
        val list = mutableListOf<DonationEntity>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            list.add(
                                DonationEntity(
                                    id = runCatching { rs.getInt("Id") }.getOrDefault(0),
                                    donorName = runCatching { rs.getString("DonorName") }.getOrDefault(""),
                                    email = runCatching { rs.getString("Email") }.getOrDefault(""),
                                    phone = runCatching { rs.getString("PhoneNumber") }.getOrDefault(""),
                                    type = runCatching { rs.getString("Type") }.getOrDefault(""),
                                    amount = runCatching { rs.getDouble("Amount") }.getOrDefault(0.0),
                                    cause = runCatching { rs.getString("Cause") }.getOrDefault(""),
                                    isAnonymous = runCatching { rs.getInt("IsAnonymous") == 1 || rs.getBoolean("IsAnonymous") }.getOrDefault(false),
                                    message = runCatching { rs.getString("Message") }.getOrDefault(""),
                                    paymentMethod = runCatching { rs.getString("PaymentMethod") }.getOrDefault(""),
                                    date = runCatching { rs.getString("DonationDate") }.getOrDefault("")
                                )
                            )
                        }
                    }
                }
            }
            AzureSqlResult.Success(list, "Fetched ${list.size} donations from Azure SQL")
        } catch (e: Exception) {
            handleException(e, "fetchAllDonations")
        }
    }
}
