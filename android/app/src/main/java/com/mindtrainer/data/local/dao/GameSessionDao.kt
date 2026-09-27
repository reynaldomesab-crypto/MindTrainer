package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.GameSessionEntity
import java.util.UUID

@Dao
interface GameSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: GameSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<GameSessionEntity>)

    @Query("SELECT * FROM game_sessions WHERE id = :id")
    suspend fun getById(id: UUID): GameSessionEntity?

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId ORDER BY started_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getByUser(userId: UUID, limit: Int, offset: Int): List<GameSessionEntity>

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId AND exercise_type_id = :exerciseTypeId ORDER BY started_at DESC LIMIT :limit")
    suspend fun getByUserAndExercise(userId: UUID, exerciseTypeId: UUID, limit: Int): List<GameSessionEntity>

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId AND date(completed_at) = date('now') AND exercise_type_id = :exerciseTypeId")
    suspend fun getTodaysSession(userId: UUID, exerciseTypeId: UUID): GameSessionEntity?

    @Update
    suspend fun update(session: GameSessionEntity)

    @Query("SELECT COUNT(*) FROM game_sessions WHERE user_id = :userId AND completed_at IS NOT NULL")
    suspend fun getTotalCompletedCount(userId: UUID): Int
}