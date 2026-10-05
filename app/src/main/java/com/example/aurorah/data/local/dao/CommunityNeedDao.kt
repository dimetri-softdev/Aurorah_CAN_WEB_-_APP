package com.example.aurorah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aurorah.data.local.entity.CommunityNeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityNeedDao {
    @Query("SELECT * FROM community_needs ORDER BY id DESC")
    fun getAllNeeds(): Flow<List<CommunityNeedEntity>>

    @Query("SELECT * FROM community_needs WHERE id = :id")
    fun getNeedById(id: Int): Flow<CommunityNeedEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(need: CommunityNeedEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(needs: List<CommunityNeedEntity>)

    @Update
    suspend fun update(need: CommunityNeedEntity)

    @Query("SELECT COUNT(*) FROM community_needs")
    suspend fun getCount(): Int
}
