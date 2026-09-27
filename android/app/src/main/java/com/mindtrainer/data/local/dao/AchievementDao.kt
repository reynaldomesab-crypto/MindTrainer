package com.mindtrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindtrainer.data.local.entity.{AchievementEntity, UserAchievementEntity}
import java.util.UUID

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievements WHERE is_active = 1")
    suspend fun getActive(): List<AchievementEntity>

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getById(id: UUID): AchievementEntity?
}

@Dao
interface UserAchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userAchievement: UserAchievementEntity)

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId")
    suspend fun getByUser(userId: UUID): List<UserAchievementEntity>

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId AND achievement_id = :achievementId")
    suspend fun getByUserAndAchievement(userId: UUID, achievementId: UUID): UserAchievementEntity?

    @Query("""
        SELECT a.* FROM achievements a
        JOIN user_achievements ua ON a.id = ua.achievement_id
        WHERE ua.user_id = :userId
    """)
    suspend fun getUnlockedAchievements(userId: UUID): List<AchievementEntity>
}