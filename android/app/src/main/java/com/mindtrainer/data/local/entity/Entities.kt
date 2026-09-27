package com.mindtrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [Index("email", unique = true), Index("username", unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: UUID,
    val email: String,
    val passwordHash: String?,
    val username: String,
    val avatarUrl: String?,
    val provider: String,
    val providerId: String?,
    val language: String,
    val emailVerified: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: UUID,
    val totalSessions: Int,
    val totalTimeMs: Long,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastPlayedAt: Instant?,
    val updatedAt: Instant
)

@Entity(
    tableName = "refresh_tokens",
    indices = [Index("userId"), Index("expiresAt"), Index("tokenHash", unique = true)]
)
data class RefreshTokenEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val tokenHash: String,
    val expiresAt: Instant,
    val revokedAt: Instant?,
    val createdAt: Instant,
    val deviceInfo: String?,
    val ipAddress: String?
)

@Entity(tableName = "exercise_types", indices = [Index("key", unique = true)])
data class ExerciseTypeEntity(
    @PrimaryKey val id: UUID,
    val key: String,
    val name: String,
    val description: String?,
    val maxDailySessions: Int,
    val createdAt: Instant
)

@Entity(
    tableName = "games",
    indices = [Index("domainId"), Index("key", unique = true), Index("isActive")]
)
data class GameEntity(
    @PrimaryKey val id: UUID,
    val domainId: UUID,
    val key: String,
    val name: String,
    val description: String?,
    val difficultyConfigJson: String,
    val scoringFormula: String,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Entity(
    tableName = "game_levels",
    indices = [Index("gameId"), Index(value = ["gameId", "levelNum"], unique = true)]
)
data class GameLevelEntity(
    @PrimaryKey val id: UUID,
    val gameId: UUID,
    val levelNum: Int,
    val configJson: String,
    val unlockRequirementJson: String?,
    val xpReward: Int,
    val createdAt: Instant
)

@Entity(
    tableName = "game_sessions",
    indices = [
        Index("userId"),
        Index("userId", "startedAt"),
        Index("exerciseTypeId"),
        Index("completedAt")
    ]
)
data class GameSessionEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val exerciseTypeId: UUID,
    val gameId: UUID?,
    val levelId: UUID?,
    val startedAt: Instant,
    val completedAt: Instant?,
    val score: Int?,
    val accuracy: Double?,
    val reactionTimeMs: Long?,
    val metadataJson: String,
    val difficultyLevel: Double?,
    val createdAt: Instant
)

@Entity(
    tableName = "session_events",
    indices = [Index("sessionId", "timestampMs")]
)
data class SessionEventEntity(
    @PrimaryKey val id: UUID,
    val sessionId: UUID,
    val timestampMs: Long,
    val eventType: String,
    val eventDataJson: String,
    val createdAt: Instant
)

@Entity(
    tableName = "user_daily_exercises",
    indices = [Index("userId", "exerciseDate"), Index(value = ["userId", "exerciseDate", "exerciseTypeId"], unique = true)]
)
data class UserDailyExerciseEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val exerciseDate: LocalDate,
    val exerciseTypeId: UUID,
    val completed: Boolean,
    val completedAt: Instant?,
    val sessionId: UUID?,
    val createdAt: Instant
)

@Entity(tableName = "leaderboards", indices = [Index(value = ["gameId", "period", "scope"], unique = true)])
data class LeaderboardEntity(
    @PrimaryKey val id: UUID,
    val gameId: UUID,
    val period: String,
    val scope: String,
    val createdAt: Instant
)

@Entity(
    tableName = "leaderboard_entries",
    indices = [Index("leaderboardId", "rank"), Index("userId"), Index(value = ["leaderboardId", "userId"], unique = true)]
)
data class LeaderboardEntryEntity(
    @PrimaryKey val id: UUID,
    val leaderboardId: UUID,
    val userId: UUID,
    val rank: Int,
    val score: Int,
    val achievedAt: Instant
)

@Entity(
    tableName = "friendships",
    indices = [Index("userId", "status"), Index("friendId", "status"), Index(value = ["userId", "friendId"], unique = true)]
)
data class FriendshipEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val friendId: UUID,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Entity(
    tableName = "challenges",
    indices = [Index("challengerId", "status"), Index("challengedId", "status"), Index("expiresAt")]
)
data class ChallengeEntity(
    @PrimaryKey val id: UUID,
    val challengerId: UUID,
    val challengedId: UUID,
    val gameId: UUID,
    val levelId: UUID?,
    val challengerScore: Int?,
    val challengedScore: Int?,
    val status: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val completedAt: Instant?
)

@Entity(tableName = "achievements", indices = [Index("key", unique = true), Index("isActive")])
data class AchievementEntity(
    @PrimaryKey val id: UUID,
    val key: String,
    val name: String,
    val description: String?,
    val criteriaJson: String,
    val rewardXp: Int,
    val icon: String?,
    val isActive: Boolean,
    val createdAt: Instant
)

@Entity(
    tableName = "user_achievements",
    indices = [Index("userId"), Index(value = ["userId", "achievementId"], unique = true)]
)
data class UserAchievementEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val achievementId: UUID,
    val unlockedAt: Instant
)

@Entity(
    tableName = "user_difficulty",
    indices = [Index("userId"), Index(value = ["userId", "exerciseTypeId"], unique = true)]
)
data class UserDifficultyEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val exerciseTypeId: UUID,
    val currentLevel: Double,
    val preference: String,
    val lastUpdated: Instant
)

@Entity(
    tableName = "daily_progress",
    indices = [Index("userId", "progressDate"), Index(value = ["userId", "progressDate", "exerciseTypeId"], unique = true)]
)
data class DailyProgressEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val progressDate: LocalDate,
    val exerciseTypeId: UUID,
    val level: Double?,
    val score: Int?,
    val metricsJson: String,
    val createdAt: Instant
)

@Entity(
    tableName = "user_insights",
    indices = [Index("userId", "generatedAt"), Index("userId", "readAt")]
)
data class UserInsightEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val type: String,
    val title: String,
    val description: String?,
    val payloadJson: String,
    val generatedAt: Instant,
    val readAt: Instant?,
    val isDismissed: Boolean
)

@Entity(
    tableName = "corpus_versions",
    indices = [Index(value = ["exerciseTypeId", "language", "version"], unique = true)]
)
data class CorpusVersionEntity(
    @PrimaryKey val id: UUID,
    val exerciseTypeId: UUID,
    val language: String,
    val version: Int,
    val contentHash: String?,
    val itemCount: Int,
    val createdAt: Instant
)