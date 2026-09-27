package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.UserDifficultyEntity
import java.util.UUID

@Dao
interface UserDifficultyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(difficulty: UserDifficultyEntity)

    @Query("SELECT * FROM user_difficulty WHERE user_id = :userId")
    suspend fun getByUser(userId: UUID): List<UserDifficultyEntity>

    @Query("SELECT * FROM user_difficulty WHERE user_id = :userId AND exercise_type_id = :exerciseTypeId")
    suspend fun getByUserAndExercise(userId: UUID, exerciseTypeId: UUID): UserDifficultyEntity?

    @Update
    suspend fun update(difficulty: UserDifficultyEntity)
}