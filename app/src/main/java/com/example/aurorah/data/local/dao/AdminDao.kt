package com.example.aurorah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aurorah.data.local.entity.AdminEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {
    @Query("SELECT * FROM admins ORDER BY id ASC")
    fun getAllAdmins(): Flow<List<AdminEntity>>

    @Query("SELECT * FROM admins WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): AdminEntity?

    @Query("SELECT * FROM admins WHERE email = :email OR username = :username LIMIT 1")
    suspend fun getAdminByEmailOrUsername(email: String, username: String): AdminEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(admin: AdminEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(admins: List<AdminEntity>)

    @Query("UPDATE admins SET lastLogin = :lastLogin WHERE id = :id")
    suspend fun updateLastLogin(id: Int, lastLogin: String)

    @Query("SELECT COUNT(*) FROM admins")
    suspend fun getCount(): Int

    @Query("UPDATE admins SET fullName = 'Anonymized Admin', email = 'anonymized@privacy.local' WHERE email = :email OR username = :username")
    suspend fun anonymizeAdminByEmailOrUsername(email: String, username: String)
}
