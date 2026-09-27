package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.DailyProgressEntity
import java.time.LocalDate
import java.util.UUID

@Dao
interface DailyProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: DailyProgressEntity)

    @Query("SELECT * FROM daily_progress WHERE user_id = :userId ORDER BY progress_date DESC LIMIT :limit")
    suspend fun getRecent(userId: UUID, limit: Int): List<DailyProgressEntity>

    @Query("SELECT * FROM daily_progress WHERE user_id = :userId AND exercise_type_id = :exerciseTypeId ORDER BY progress_date DESC LIMIT :limit")
    suspend fun getByExercise(userId: UUID, exerciseTypeId: UUID, limit: Int): List<DailyProgressEntity>

    @Query("SELECT * FROM daily_progress WHERE user_id = :userId AND progress_date BETWEEN :startDate AND :endDate")
    suspend fun getByDateRange(userId: UUID, startDate: LocalDate, endDate: LocalDate): List<DailyProgressEntity>
}