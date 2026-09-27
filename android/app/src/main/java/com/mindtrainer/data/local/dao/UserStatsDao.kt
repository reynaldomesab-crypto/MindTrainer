package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.UserStatsEntity
import java.util.UUID

@Dao
interface UserStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE user_id = :userId")
    suspend fun getByUserId(userId: UUID): UserStatsEntity?

    @Update
    suspend fun update(stats: UserStatsEntity)

    @Query("DELETE FROM user_stats WHERE user_id = :userId")
    suspend fun delete(userId: UUID)
}