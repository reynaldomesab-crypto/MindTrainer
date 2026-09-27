package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.GameLevelEntity
import java.util.UUID

@Dao
interface GameLevelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(levels: List<GameLevelEntity>)

    @Query("SELECT * FROM game_levels WHERE game_id = :gameId ORDER BY level_num")
    suspend fun getByGame(gameId: UUID): List<GameLevelEntity>

    @Query("SELECT * FROM game_levels WHERE game_id = :gameId AND level_num = :levelNum")
    suspend fun getByGameAndLevel(gameId: UUID, levelNum: Int): GameLevelEntity?
}