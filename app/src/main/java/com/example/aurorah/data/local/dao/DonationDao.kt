package com.example.aurorah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aurorah.data.local.entity.DonationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY id DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE id = :id")
    fun getDonationById(id: Int): Flow<DonationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donation: DonationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donations: List<DonationEntity>)

    @Query("SELECT SUM(amount) FROM donations")
    fun getTotalDonationsAmount(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM donations")
    suspend fun getCount(): Int

    @Query("UPDATE donations SET donorName = 'Anonymous Donor', email = 'anonymized@privacy.local', phone = '0000000000', isAnonymous = 1 WHERE email = :email OR donorName = :name")
    suspend fun anonymizeDonationByEmailOrName(email: String, name: String)

    @Query("DELETE FROM donations WHERE email = :email OR donorName = :name")
    suspend fun deleteDonationByEmailOrName(email: String, name: String)
}
