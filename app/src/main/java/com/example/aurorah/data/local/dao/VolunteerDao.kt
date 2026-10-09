package com.example.aurorah.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aurorah.data.local.entity.VolunteerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VolunteerDao {
    @Query("SELECT * FROM volunteers ORDER BY id DESC")
    fun getAllVolunteers(): Flow<List<VolunteerEntity>>

    @Query("SELECT * FROM volunteers WHERE id = :id")
    fun getVolunteerById(id: Int): Flow<VolunteerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(volunteer: VolunteerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(volunteers: List<VolunteerEntity>)

    @Update
    suspend fun update(volunteer: VolunteerEntity)

    @Delete
    suspend fun delete(volunteer: VolunteerEntity)

    @Query("SELECT COUNT(*) FROM volunteers")
    suspend fun getCount(): Int

    @Query("UPDATE volunteers SET fullName = 'Anonymized User', email = 'anonymized@privacy.local', phone = '0000000000', address = 'Anonymized', status = 'Anonymized' WHERE email = :email OR fullName = :name")
    suspend fun anonymizeVolunteerByEmailOrName(email: String, name: String)

    @Query("DELETE FROM volunteers WHERE email = :email OR fullName = :name")
    suspend fun deleteVolunteerByEmailOrName(email: String, name: String)
}
