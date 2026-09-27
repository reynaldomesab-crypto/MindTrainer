package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.ExerciseTypeEntity
import java.util.UUID

@Dao
interface ExerciseTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<ExerciseTypeEntity>)

    @Query("SELECT * FROM exercise_types")
    suspend fun getAll(): List<ExerciseTypeEntity>

    @Query("SELECT * FROM exercise_types WHERE key = :key")
    suspend fun getByKey(key: String): ExerciseTypeEntity?

    @Query("SELECT * FROM exercise_types WHERE id = :id")
    suspend fun getById(id: UUID): ExerciseTypeEntity?
}