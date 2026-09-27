package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.{LeaderboardEntity, LeaderboardEntryEntity}
import java.util.UUID

@Dao
interface LeaderboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(board: LeaderboardEntity)

    @Query("SELECT * FROM leaderboards WHERE game_id = :gameId AND period = :period AND scope = :scope")
    suspend fun getByGamePeriodScope(gameId: UUID, period: String, scope: String): LeaderboardEntity?

    @Query("SELECT * FROM leaderboards WHERE game_id = :gameId")
    suspend fun getByGame(gameId: UUID): List<LeaderboardEntity>
}

@Dao
interface LeaderboardEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<LeaderboardEntryEntity>)

    @Query("SELECT * FROM leaderboard_entries WHERE leaderboard_id = :leaderboardId ORDER BY rank LIMIT :limit")
    suspend fun getTopEntries(leaderboardId: UUID, limit: Int): List<LeaderboardEntryEntity>

    @Query("SELECT * FROM leaderboard_entries WHERE leaderboard_id = :leaderboardId AND user_id = :userId")
    suspend fun getUserEntry(leaderboardId: UUID, userId: UUID): LeaderboardEntryEntity?
}