package com.example.aurorah.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BeneficiaryDao {
    @Query("SELECT * FROM beneficiaries ORDER BY id DESC")
    fun getAllBeneficiaries(): Flow<List<BeneficiaryEntity>>

    @Query("SELECT * FROM beneficiaries WHERE id = :id")
    fun getBeneficiaryById(id: Int): Flow<BeneficiaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(beneficiary: BeneficiaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beneficiaries: List<BeneficiaryEntity>)

    @Update
    suspend fun update(beneficiary: BeneficiaryEntity)

    @Delete
    suspend fun delete(beneficiary: BeneficiaryEntity)

    @Query("SELECT COUNT(*) FROM beneficiaries")
    suspend fun getCount(): Int
}
