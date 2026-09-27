package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.RefreshTokenEntity
import java.util.UUID

@Dao
interface RefreshTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: RefreshTokenEntity)

    @Query("SELECT * FROM refresh_tokens WHERE token_hash = :tokenHash")
    suspend fun getByTokenHash(tokenHash: String): RefreshTokenEntity?

    @Query("SELECT * FROM refresh_tokens WHERE user_id = :userId AND revoked_at IS NULL AND expires_at > datetime('now')")
    suspend fun getValidTokensForUser(userId: UUID): List<RefreshTokenEntity>

    @Update
    suspend fun update(token: RefreshTokenEntity)

    @Query("DELETE FROM refresh_tokens WHERE expires_at < datetime('now')")
    suspend fun deleteExpired()

    @Query("DELETE FROM refresh_tokens WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: UUID)
}