package com.mindtrainer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mindtrainer.data.local.converters.Converters
import com.mindtrainer.data.local.dao.*
import com.mindtrainer.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        UserStatsEntity::class,
        RefreshTokenEntity::class,
        ExerciseTypeEntity::class,
        GameEntity::class,
        GameLevelEntity::class,
        GameSessionEntity::class,
        SessionEventEntity::class,
        UserDailyExerciseEntity::class,
        LeaderboardEntity::class,
        LeaderboardEntryEntity::class,
        FriendshipEntity::class,
        ChallengeEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        UserDifficultyEntity::class,
        DailyProgressEntity::class,
        UserInsightEntity::class,
        CorpusVersionEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun refreshTokenDao(): RefreshTokenDao
    abstract fun exerciseTypeDao(): ExerciseTypeDao
    abstract fun gameDao(): GameDao
    abstract fun gameLevelDao(): GameLevelDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun sessionEventDao(): SessionEventDao
    abstract fun userDailyExerciseDao(): UserDailyExerciseDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun leaderboardEntryDao(): LeaderboardEntryDao
    abstract fun friendshipDao(): FriendshipDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userAchievementDao(): UserAchievementDao
    abstract fun userDifficultyDao(): UserDifficultyDao
    abstract fun dailyProgressDao(): DailyProgressDao
    abstract fun userInsightDao(): UserInsightDao
    abstract fun corpusVersionDao(): CorpusVersionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindtrainer.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}