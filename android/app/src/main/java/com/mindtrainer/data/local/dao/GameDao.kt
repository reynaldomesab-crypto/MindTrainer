package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.GameEntity
import java.util.UUID

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameEntity>)

    @Query("SELECT * FROM games WHERE is_active = 1")
    suspend fun getActive(): List<GameEntity>

    @Query("SELECT * FROM games WHERE domain_id = :domainId")
    suspend fun getByDomain(domainId: UUID): List<GameEntity>

    @Query("SELECT * FROM games WHERE key = :key")
    suspend fun getByKey(key: String): GameEntity?

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: UUID): GameEntity?
}