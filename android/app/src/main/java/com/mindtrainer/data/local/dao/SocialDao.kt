package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindtrainer.data.local.entity.{FriendshipEntity, ChallengeEntity}
import java.util.UUID

@Dao
interface FriendshipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friendship: FriendshipEntity)

    @Query("SELECT * FROM friendships WHERE user_id = :userId AND status = :status")
    suspend fun getByUserAndStatus(userId: UUID, status: String): List<FriendshipEntity>

    @Query("SELECT * FROM friendships WHERE user_id = :userId AND friend_id = :friendId")
    suspend fun getBetweenUsers(userId: UUID, friendId: UUID): FriendshipEntity?

    @Update
    suspend fun update(friendship: FriendshipEntity)

    @Query("DELETE FROM friendships WHERE user_id = :userId AND friend_id = :friendId")
    suspend fun delete(userId: UUID, friendId: UUID)
}

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(challenge: ChallengeEntity)

    @Query("SELECT * FROM challenges WHERE challenged_id = :userId AND status = 'pending'")
    suspend fun getPendingForUser(userId: UUID): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE challenger_id = :userId OR challenged_id = :userId ORDER BY created_at DESC LIMIT :limit")
    suspend fun getByUser(userId: UUID, limit: Int): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getById(id: UUID): ChallengeEntity?

    @Update
    suspend fun update(challenge: ChallengeEntity)

    @Query("DELETE FROM challenges WHERE expires_at < datetime('now') AND status = 'pending'")
    suspend fun deleteExpiredPending()
}