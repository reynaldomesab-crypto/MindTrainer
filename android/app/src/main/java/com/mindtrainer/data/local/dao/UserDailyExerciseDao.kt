package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.UserDailyExerciseEntity
import java.time.LocalDate
import java.util.UUID

@Dao
interface UserDailyExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(daily: UserDailyExerciseEntity)

    @Query("SELECT * FROM user_daily_exercises WHERE user_id = :userId AND exercise_date = :date")
    suspend fun getByUserAndDate(userId: UUID, date: LocalDate): List<UserDailyExerciseEntity>

    @Query("SELECT * FROM user_daily_exercises WHERE user_id = :userId AND exercise_date = :date AND exercise_type_id = :exerciseTypeId")
    suspend fun getByUserDateAndType(userId: UUID, date: LocalDate, exerciseTypeId: UUID): UserDailyExerciseEntity?

    @Query("SELECT COUNT(*) FROM user_daily_exercises WHERE user_id = :userId AND exercise_date = :date AND completed = 1")
    suspend fun getCompletedCount(userId: UUID, date: LocalDate): Int

    @Update
    suspend fun update(daily: UserDailyExerciseEntity)

    @Query("DELETE FROM user_daily_exercises WHERE exercise_date < date('now', '-90 days')")
    suspend fun deleteOldEntries()
}