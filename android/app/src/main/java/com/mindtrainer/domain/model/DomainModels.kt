package com.mindtrainer.domain.model

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// Result wrapper
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Exception, val message: String?) : Result<Nothing>
}

// Auth
data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val user: UserProfile
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val language: String = "en"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateProfileRequest(
    val username: String?,
    val avatarUrl: String?,
    val language: String?
)

// User Profile
data class UserProfile(
    val id: UUID,
    val email: String,
    val username: String,
    val avatarUrl: String?,
    val language: String,
    val createdAt: Instant,
    val stats: UserStats
)

data class UserStats(
    val totalSessions: Int,
    val totalTimeMs: Long,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastPlayedAt: Instant?
)

// Preferences
data class UserPreferences(
    val language: String,
    val reminders: ReminderSettings,
    val difficulty: DifficultyPreferences,
    val colorblindMode: Boolean,
    val reducedMotion: Boolean,
    val largeText: Boolean,
    val analyticsOptIn: Boolean
)

data class ReminderSettings(
    val enabled: Boolean,
    val time: String, // HH:mm format
    val timezone: String,
    val daysOfWeek: List<String>,
    val tone: String,
    val message: String
)

data class DifficultyPreferences(
    val schulte: DifficultyPreference,
    val blindfold: DifficultyPreference,
    val nonDominant: DifficultyPreference,
    val stroop: DifficultyPreference
)

enum class DifficultyPreference {
    CHALLENGE, MAINTAIN, RELAX
}

data class UpdatePreferencesRequest(
    val language: String?,
    val reminders: ReminderSettings?,
    val difficulty: DifficultyPreferences?,
    val colorblindMode: Boolean?,
    val reducedMotion: Boolean?,
    val largeText: Boolean?,
    val analyticsOptIn: Boolean?
)

// Exercise Models - Schulte
data class SchulteTableConfig(
    val seed: Long,
    val size: Int,
    val timeLimitMs: Int,
    val showNumbers: Boolean
)

data class SchulteDaily(
    val config: SchulteTableConfig,
    val numbers: List<Int>,
    val expiresAt: Instant
)

data class SchultePackage(
    val tables: List<SchulteTableConfig>,
    val generatedAt: Instant
)

data class SchulteSubmitRequest(
    val config: SchulteTableConfig,
    val completionTimeMs: Long,
    val tappedSequence: List<Int>,
    val accuracy: Double
)

data class SchulteResult(
    val sessionId: UUID,
    val score: Int,
    val accuracy: Double,
    val level: Double,
    val nextLevel: Double
)

// Exercise Models - Blindfold
data class BlindfoldText(
    val id: String,
    val language: String,
    val text: String,
    val charCount: Int,
    val tier: Int,
    val topic: String,
    val rareCharDensity: Double
)

data class BlindfoldDaily(
    val text: BlindfoldText,
    val expiresAt: Instant
)

data class BlindfoldSubmitRequest(
    val textId: String,
    val actualText: String,
    val keyboardLayout: String,
    val keyDistances: List<Double>,
    val rmsError: Double,
    val timeMs: Long
)

data class BlindfoldResult(
    val sessionId: UUID,
    val score: Int,
    val rmsError: Double,
    val accuracy: Double,
    val lengthFactor: Double,
    val level: Double,
    val nextLevel: Double
)

// Exercise Models - Non-Dominant
data class Point(
    val x: Double,
    val y: Double
)

data class ShapePath(
    val id: String,
    val type: String,
    val points: List<Point>,
    val targetTimeMs: Long,
    val difficulty: Double
)

data class TapSequence(
    val id: String,
    val gridSize: Int,
    val targets: List<Int>,
    val count: Int,
    val timeLimitMs: Long
)

data class NonDomDaily(
    val copywriting: BlindfoldText,
    val tracing: ShapePath,
    val tapping: TapSequence,
    val expiresAt: Instant
)

data class NonDomSubmitRequest(
    val taskType: String, // COPYWRITING, TRACING, TAPPING
    val contentId: String,
    val handUsed: String, // LEFT, RIGHT
    val metrics: Map<String, Double>
)

data class NonDomResult(
    val sessionId: UUID,
    val score: Int,
    val taskScore: Int,
    val level: Double,
    val nextLevel: Double
)

// Exercise Models - Stroop
data class StroopStimulus(
    val word: String,
    val inkColor: String,
    val position: String?,
    val audioColor: String?
)

data class StroopSessionConfig(
    val colorCount: Int,
    val incongruentRatio: Double,
    val stimulusDurationMs: Int,
    val switchFrequency: Double,
    val sequenceLength: Int
)

data class StroopDaily(
    val mode: String,
    val config: StroopSessionConfig,
    val stimuli: List<StroopStimulus>,
    val expiresAt: Instant
)

data class StroopWordSets(
    val language: String,
    val colorWords: List<String>,
    val neutralWords: List<String>,
    val emotionalWords: List<String>
)

data class StroopTrialRequest(
    val stimulus: StroopStimulus,
    val response: String,
    val rtMs: Long,
    val correct: Boolean,
    val switchTrial: Boolean
)

data class StroopSubmitRequest(
    val mode: String,
    val trials: List<StroopTrialRequest>
)

data class StroopResult(
    val sessionId: UUID,
    val interferenceEffect: Double,
    val interferenceRatio: Double,
    val switchCost: Double,
    val throughput: Double,
    val inhibitionIndex: Double,
    val flexibilityIndex: Double,
    val level: Double,
    val nextLevel: Double
)

// Corpus
data class CorpusPackage(
    val language: String,
    val exerciseType: String,
    val items: List<Map<String, Any>>,
    val count: Int,
    val generatedAt: Instant
)

// Progress Models
data class DailyExerciseStatus(
    val exerciseType: String,
    val completed: Boolean,
    val completedAt: Instant?,
    val sessionId: UUID?
)

data class ExerciseProgress(
    val exerciseType: String,
    val currentLevel: Double,
    val sessionsCompleted: Int,
    val lastSession: Instant?,
    val trend: TrendDirection,
    val personalBests: Map<String, Double>,
    val history: List<SessionHistoryItem>
)

enum class TrendDirection {
    IMPROVING, STABLE, DECLINING
}

data class SessionHistoryItem(
    val date: LocalDate,
    val score: Int,
    val level: Double,
    val metrics: Map<String, Double>
)

data class GlobalMetrics(
    val cognitiveAge: Int,
    val consistencyScore: Double,
    val focusIndex: Double,
    val motorSymmetry: Double,
    val weeklyActiveDays: Int
)

data class StreakData(
    val current: Int,
    val longest: Int,
    val lastActiveDate: LocalDate,
    val streakFreezeUsed: Boolean
)

data class Insight(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val generatedAt: Instant,
    val readAt: Instant?
)

data class ProgressDashboard(
    val todayStatus: List<DailyExerciseStatus>,
    val exerciseProgress: Map<String, ExerciseProgress>,
    val globalMetrics: GlobalMetrics,
    val streaks: StreakData,
    val insights: List<Insight>
)