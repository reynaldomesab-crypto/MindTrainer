package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.UserInsightEntity
import java.util.UUID

@Dao
interface UserInsightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(insight: UserInsightEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(insights: List<UserInsightEntity>)

    @Query("SELECT * FROM user_insights WHERE user_id = :userId ORDER BY generated_at DESC LIMIT :limit")
    suspend fun getByUser(userId: UUID, limit: Int): List<UserInsightEntity>

    @Query("SELECT * FROM user_insights WHERE user_id = :userId AND read_at IS NULL AND is_dismissed = 0")
    suspend fun getUnread(userId: UUID): List<UserInsightEntity>

    @Update
    suspend fun update(insight: UserInsightEntity)

    @Query("DELETE FROM user_insights WHERE generated_at < datetime('now', '-180 days')")
    suspend fun deleteOld()
}