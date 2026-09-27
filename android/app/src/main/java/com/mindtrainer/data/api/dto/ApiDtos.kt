package com.mindtrainer.data.api.dto

import com.squareup.moshi.Json

// Auth DTOs
data class RegisterRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val username: String,
    @Json(name = "language") val language: String = "en"
)

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class OAuthRequest(
    @Json(name = "provider") val provider: String,
    @Json(name = "token") val token: String
)

data class RefreshRequest(
    @Json(name = "refreshToken") val refreshToken: String
)

data class AuthResponse(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "tokenType") val tokenType: String,
    @Json(name = "expiresIn") val expiresIn: Int,
    @Json(name = "user") val user: UserProfileResponse
)

data class UserProfileResponse(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatarUrl") val avatarUrl: String?,
    @Json(name = "language") val language: String,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "stats") val stats: UserStatsResponse
)

data class UserStatsResponse(
    @Json(name = "totalSessions") val totalSessions: Int,
    @Json(name = "totalTimeMs") val totalTimeMs: Long,
    @Json(name = "currentStreak") val currentStreak: Int,
    @Json(name = "longestStreak") val longestStreak: Int,
    @Json(name = "lastPlayedAt") val lastPlayedAt: String?
)

data class UpdateProfileRequest(
    @Json(name = "username") val username: String?,
    @Json(name = "avatarUrl") val avatarUrl: String?,
    @Json(name = "language") val language: String?
)

// Preferences DTOs
data class UserPreferencesResponse(
    @Json(name = "language") val language: String,
    @Json(name = "reminders") val reminders: ReminderSettingsResponse,
    @Json(name = "difficulty") val difficulty: DifficultyPreferencesResponse,
    @Json(name = "colorblindMode") val colorblindMode: Boolean,
    @Json(name = "reducedMotion") val reducedMotion: Boolean,
    @Json(name = "largeText") val largeText: Boolean,
    @Json(name = "analyticsOptIn") val analyticsOptIn: Boolean
)

data class UserPreferencesRequest(
    @Json(name = "language") val language: String?,
    @Json(name = "reminders") val reminders: ReminderSettingsRequest?,
    @Json(name = "difficulty") val difficulty: DifficultyPreferencesRequest?,
    @Json(name = "colorblindMode") val colorblindMode: Boolean?,
    @Json(name = "reducedMotion") val reducedMotion: Boolean?,
    @Json(name = "largeText") val largeText: Boolean?,
    @Json(name = "analyticsOptIn") val analyticsOptIn: Boolean?
)

data class ReminderSettingsResponse(
    @Json(name = "enabled") val enabled: Boolean,
    @Json(name = "time") val time: String,
    @Json(name = "timezone") val timezone: String,
    @Json(name = "daysOfWeek") val daysOfWeek: List<String>,
    @Json(name = "tone") val tone: String,
    @Json(name = "message") val message: String
)

data class ReminderSettingsRequest(
    @Json(name = "enabled") val enabled: Boolean?,
    @Json(name = "time") val time: String?,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "daysOfWeek") val daysOfWeek: List<String>?,
    @Json(name = "tone") val tone: String?,
    @Json(name = "message") val message: String?
)

data class DifficultyPreferencesResponse(
    @Json(name = "schulte") val schulte: String,
    @Json(name = "blindfold") val blindfold: String,
    @Json(name = "nonDominant") val nonDominant: String,
    @Json(name = "stroop") val stroop: String
)

data class DifficultyPreferencesRequest(
    @Json(name = "schulte") val schulte: String?,
    @Json(name = "blindfold") val blindfold: String?,
    @Json(name = "nonDominant") val nonDominant: String?,
    @Json(name = "stroop") val stroop: String?
)

// Exercise DTOs - Schulte
data class SchulteTableConfig(
    @Json(name = "seed") val seed: Long,
    @Json(name = "size") val size: Int,
    @Json(name = "timeLimitMs") val timeLimitMs: Int,
    @Json(name = "showNumbers") val showNumbers: Boolean
)

data class SchulteDailyResponse(
    @Json(name = "config") val config: SchulteTableConfig,
    @Json(name = "numbers") val numbers: List<Int>,
    @Json(name = "expiresAt") val expiresAt: String
)

data class SchultePackageResponse(
    @Json(name = "tables") val tables: List<SchulteTableConfig>,
    @Json(name = "generatedAt") val generatedAt: String
)

data class SchulteSessionRequest(
    @Json(name = "config") val config: SchulteTableConfig,
    @Json(name = "completionTimeMs") val completionTimeMs: Long,
    @Json(name = "tappedSequence") val tappedSequence: List<Int>,
    @Json(name = "accuracy") val accuracy: Double
)

data class SchulteSessionResponse(
    @Json(name = "sessionId") val sessionId: String,
    @Json(name = "score") val score: Int,
    @Json(name = "accuracy") val accuracy: Double,
    @Json(name = "level") val level: Double,
    @Json(name = "nextLevel") val nextLevel: Double
)

// Exercise DTOs - Blindfold
data class BlindfoldText(
    @Json(name = "id") val id: String,
    @Json(name = "language") val language: String,
    @Json(name = "text") val text: String,
    @Json(name = "charCount") val charCount: Int,
    @Json(name = "tier") val tier: Int,
    @Json(name = "topic") val topic: String,
    @Json(name = "rareCharDensity") val rareCharDensity: Double
)

data class BlindfoldDailyResponse(
    @Json(name = "text") val text: BlindfoldText,
    @Json(name = "expiresAt") val expiresAt: String
)

data class BlindfoldSessionRequest(
    @Json(name = "textId") val textId: String,
    @Json(name = "actualText") val actualText: String,
    @Json(name = "keyboardLayout") val keyboardLayout: String,
    @Json(name = "keyDistances") val keyDistances: List<Double>,
    @Json(name = "rmsError") val rmsError: Double,
    @Json(name = "timeMs") val timeMs: Long
)

data class BlindfoldSessionResponse(
    @Json(name = "sessionId") val sessionId: String,
    @Json(name = "score") val score: Int,
    @Json(name = "rmsError") val rmsError: Double,
    @Json(name = "accuracy") val accuracy: Double,
    @Json(name = "lengthFactor") val lengthFactor: Double,
    @Json(name = "level") val level: Double,
    @Json(name = "nextLevel") val nextLevel: Double
)

// Exercise DTOs - Non-Dominant
data class ShapePath(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "points") val points: List<PointDto>,
    @Json(name = "targetTimeMs") val targetTimeMs: Long,
    @Json(name = "difficulty") val difficulty: Double
)

data class PointDto(
    @Json(name = "x") val x: Double,
    @Json(name = "y") val y: Double
)

data class TapSequence(
    @Json(name = "id") val id: String,
    @Json(name = "gridSize") val gridSize: Int,
    @Json(name = "targets") val targets: List<Int>,
    @Json(name = "count") val count: Int,
    @Json(name = "timeLimitMs") val timeLimitMs: Long
)

data class NonDomDailyResponse(
    @Json(name = "copywriting") val copywriting: BlindfoldText,
    @Json(name = "tracing") val tracing: ShapePath,
    @Json(name = "tapping") val tapping: TapSequence,
    @Json(name = "expiresAt") val expiresAt: String
)

data class NonDomSessionRequest(
    @Json(name = "taskType") val taskType: String,
    @Json(name = "contentId") val contentId: String,
    @Json(name = "handUsed") val handUsed: String,
    @Json(name = "metrics") val metrics: Map<String, Double>
)

data class NonDomSessionResponse(
    @Json(name = "sessionId") val sessionId: String,
    @Json(name = "score") val score: Int,
    @Json(name = "taskScore") val taskScore: Int,
    @Json(name = "level") val level: Double,
    @Json(name = "nextLevel") val nextLevel: Double
)

// Exercise DTOs - Stroop
data class StroopStimulus(
    @Json(name = "word") val word: String,
    @Json(name = "inkColor") val inkColor: String,
    @Json(name = "position") val position: String?,
    @Json(name = "audioColor") val audioColor: String?
)

data class StroopSessionConfig(
    @Json(name = "colorCount") val colorCount: Int,
    @Json(name = "incongruentRatio") val incongruentRatio: Double,
    @Json(name = "stimulusDurationMs") val stimulusDurationMs: Int,
    @Json(name = "switchFrequency") val switchFrequency: Double,
    @Json(name = "sequenceLength") val sequenceLength: Int
)

data class StroopDailyResponse(
    @Json(name = "mode") val mode: String,
    @Json(name = "config") val config: StroopSessionConfig,
    @Json(name = "stimuli") val stimuli: List<StroopStimulus>,
    @Json(name = "expiresAt") val expiresAt: String
)

data class StroopTrialRequest(
    @Json(name = "stimulus") val stimulus: StroopStimulus,
    @Json(name = "response") val response: String,
    @Json(name = "rtMs") val rtMs: Long,
    @Json(name = "correct") val correct: Boolean,
    @Json(name = "switchTrial") val switchTrial: Boolean
)

data class StroopSessionRequest(
    @Json(name = "mode") val mode: String,
    @Json(name = "trials") val trials: List<StroopTrialRequest>
)

data class StroopSessionResponse(
    @Json(name = "sessionId") val sessionId: String,
    @Json(name = "interferenceEffect") val interferenceEffect: Double,
    @Json(name = "interferenceRatio") val interferenceRatio: Double,
    @Json(name = "switchCost") val switchCost: Double,
    @Json(name = "throughput") val throughput: Double,
    @Json(name = "inhibitionIndex") val inhibitionIndex: Double,
    @Json(name = "flexibilityIndex") val flexibilityIndex: Double,
    @Json(name = "level") val level: Double,
    @Json(name = "nextLevel") val nextLevel: Double
)

data class StroopWordSetsResponse(
    @Json(name = "language") val language: String,
    @Json(name = "colorWords") val colorWords: List<String>,
    @Json(name = "neutralWords") val neutralWords: List<String>,
    @Json(name = "emotionalWords") val emotionalWords: List<String>
)

// Progress DTOs
data class DailyExerciseStatus(
    @Json(name = "exerciseType") val exerciseType: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "completedAt") val completedAt: String?,
    @Json(name = "sessionId") val sessionId: String?
)

data class ExerciseProgressResponse(
    @Json(name = "exerciseType") val exerciseType: String,
    @Json(name = "currentLevel") val currentLevel: Double,
    @Json(name = "sessionsCompleted") val sessionsCompleted: Int,
    @Json(name = "lastSession") val lastSession: String?,
    @Json(name = "trend") val trend: String,
    @Json(name = "personalBests") val personalBests: Map<String, Double>,
    @Json(name = "history") val history: List<SessionHistoryItem>
)

data class SessionHistoryItem(
    @Json(name = "date") val date: String,
    @Json(name = "score") val score: Int,
    @Json(name = "level") val level: Double,
    @Json(name = "metrics") val metrics: Map<String, Double>
)

data class GlobalMetricsResponse(
    @Json(name = "cognitiveAge") val cognitiveAge: Int,
    @Json(name = "consistencyScore") val consistencyScore: Double,
    @Json(name = "focusIndex") val focusIndex: Double,
    @Json(name = "motorSymmetry") val motorSymmetry: Double,
    @Json(name = "weeklyActiveDays") val weeklyActiveDays: Int
)

data class StreakDataResponse(
    @Json(name = "current") val current: Int,
    @Json(name = "longest") val longest: Int,
    @Json(name = "lastActiveDate") val lastActiveDate: String,
    @Json(name = "streakFreezeUsed") val streakFreezeUsed: Boolean
)

data class InsightResponse(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "generatedAt") val generatedAt: String,
    @Json(name = "readAt") val readAt: String?
)

data class ProgressDashboardResponse(
    @Json(name = "todayStatus") val todayStatus: List<DailyExerciseStatus>,
    @Json(name = "exerciseProgress") val exerciseProgress: Map<String, ExerciseProgressResponse>,
    @Json(name = "globalMetrics") val globalMetrics: GlobalMetricsResponse,
    @Json(name = "streaks") val streaks: StreakDataResponse,
    @Json(name = "insights") val insights: List<InsightResponse>
)

// Corpus DTOs
data class CorpusPackageResponse(
    @Json(name = "language") val language: String,
    @Json(name = "exerciseType") val exerciseType: String,
    @Json(name = "items") val items: List<Map<String, Any>>,
    @Json(name = "count") val count: Int,
    @Json(name = "generatedAt") val generatedAt: String
)