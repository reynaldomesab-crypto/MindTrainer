package com.mindtrainer.data.repository

import com.mindtrainer.data.api.MindTrainerApi
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.data.local.AppDatabase
import com.mindtrainer.data.local.dao.UserDao
import com.mindtrainer.data.local.dao.UserStatsDao
import com.mindtrainer.data.local.dao.RefreshTokenDao
import com.mindtrainer.data.local.entity.UserEntity
import com.mindtrainer.data.local.entity.UserStatsEntity
import com.mindtrainer.data.local.entity.RefreshTokenEntity
import com.mindtrainer.data.preferences.UserPreferences
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.model.Result
import com.mindtrainer.domain.repository.UserRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: MindTrainerApi,
    private val database: AppDatabase,
    private val preferences: UserPreferences
) : UserRepository {

    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val profile = mapProfile(body)
                // Cache locally
                cacheProfile(profile)
                Result.Success(profile)
            } else {
                // Try local cache
                val cached = database.userDao().getById(UUID.fromString(preferences.userId.first() ?: ""))
                if (cached != null) {
                    Result.Success(mapCachedProfile(cached))
                } else {
                    Result.Error(Exception("API Error: ${response.code()}"), "Failed to get profile")
                }
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfile> {
        return try {
            val dto = UserProfileUpdateRequest(
                username = request.username,
                avatarUrl = request.avatarUrl,
                language = request.language
            )
            val response = api.updateProfile(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val profile = mapProfile(body)
                cacheProfile(profile)
                Result.Success(profile)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to update profile")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getPreferences(): Result<UserPreferences> {
        return try {
            val response = api.getPreferences()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapPreferences(body))
            } else {
                // Return local preferences
                Result.Success(getLocalPreferences())
            }
        } catch (e: Exception) {
            Result.Success(getLocalPreferences())
        }
    }

    override suspend fun updatePreferences(request: UpdatePreferencesRequest): Result<UserPreferences> {
        return try {
            val dto = UserPreferencesRequest(
                language = request.language,
                reminders = request.reminders?.let { mapReminderSettings(it) },
                difficulty = request.difficulty?.let { mapDifficultyPreferences(it) },
                colorblindMode = request.colorblindMode,
                reducedMotion = request.reducedMotion,
                largeText = request.largeText,
                analyticsOptIn = request.analyticsOptIn
            )
            val response = api.updatePreferences(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val prefs = mapPreferences(body)
                // Update local
                updateLocalPreferences(prefs)
                Result.Success(prefs)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to update preferences")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getReminders(): Result<ReminderSettings> {
        return try {
            val response = api.getReminders()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapReminderSettingsFromResponse(body))
            } else {
                Result.Success(getLocalReminders())
            }
        } catch (e: Exception) {
            Result.Success(getLocalReminders())
        }
    }

    override suspend fun updateReminders(request: ReminderSettings): Result<ReminderSettings> {
        return try {
            val dto = ReminderSettingsRequest(
                enabled = request.enabled,
                time = request.time,
                timezone = request.timezone,
                daysOfWeek = request.daysOfWeek,
                tone = request.tone,
                message = request.message
            )
            val response = api.updateReminders(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val settings = mapReminderSettingsFromResponse(body)
                updateLocalReminders(settings)
                Result.Success(settings)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to update reminders")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getDifficultyPreferences(): Result<DifficultyPreferences> {
        return try {
            val response = api.getDifficultyPreferences()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapDifficultyPreferencesFromResponse(body))
            } else {
                Result.Success(getLocalDifficultyPreferences())
            }
        } catch (e: Exception) {
            Result.Success(getLocalDifficultyPreferences())
        }
    }

    override suspend fun updateDifficultyPreferences(request: DifficultyPreferences): Result<DifficultyPreferences> {
        return try {
            val dto = DifficultyPreferencesRequest(
                schulte = request.schulte.name,
                blindfold = request.blindfold.name,
                nonDominant = request.nonDominant.name,
                stroop = request.stroop.name
            )
            val response = api.updateDifficultyPreferences(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val prefs = mapDifficultyPreferencesFromResponse(body)
                updateLocalDifficultyPreferences(prefs)
                Result.Success(prefs)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to update difficulty preferences")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResult> {
        return try {
            val dto = RegisterRequest(
                email = request.email,
                password = request.password,
                username = request.username,
                language = request.language
            )
            val response = api.register(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val authResult = mapAuthResult(body)
                // Store tokens locally
                preferences.setAuthToken(body.accessToken)
                preferences.setRefreshToken(body.refreshToken)
                preferences.setUserId(body.user.id)
                Result.Success(authResult)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Registration failed")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun login(request: LoginRequest): Result<AuthResult> {
        return try {
            val dto = LoginRequest(
                email = request.email,
                password = request.password
            )
            val response = api.login(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val authResult = mapAuthResult(body)
                preferences.setAuthToken(body.accessToken)
                preferences.setRefreshToken(body.refreshToken)
                preferences.setUserId(body.user.id)
                Result.Success(authResult)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Login failed")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun oauthLogin(provider: String, token: String): Result<AuthResult> {
        return try {
            val dto = OAuthRequest(provider = provider, token = token)
            val response = api.oauthLogin(provider, dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val authResult = mapAuthResult(body)
                preferences.setAuthToken(body.accessToken)
                preferences.setRefreshToken(body.refreshToken)
                preferences.setUserId(body.user.id)
                Result.Success(authResult)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "OAuth login failed")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthResult> {
        return try {
            val dto = RefreshRequest(refreshToken = refreshToken)
            val response = api.refreshToken(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val authResult = mapAuthResult(body)
                preferences.setAuthToken(body.accessToken)
                preferences.setRefreshToken(body.refreshToken)
                Result.Success(authResult)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Token refresh failed")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            preferences.clearAll()
            // Clear local database
            database.userDao().delete(UUID.fromString(preferences.userId.first() ?: ""))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    // Local cache helpers
    private suspend fun cacheProfile(profile: UserProfile) {
        val entity = UserEntity(
            id = profile.id,
            email = profile.email,
            passwordHash = null,
            username = profile.username,
            avatarUrl = profile.avatarUrl,
            provider = "local",
            providerId = null,
            language = profile.language,
            emailVerified = true,
            createdAt = profile.createdAt,
            updatedAt = Instant.now(),
            deletedAt = null
        )
        database.userDao().insert(entity)

        val statsEntity = UserStatsEntity(
            userId = profile.id,
            totalSessions = profile.stats.totalSessions,
            totalTimeMs = profile.stats.totalTimeMs,
            currentStreak = profile.stats.currentStreak,
            longestStreak = profile.stats.longestStreak,
            lastPlayedAt = profile.stats.lastPlayedAt,
            updatedAt = Instant.now()
        )
        database.userStatsDao().insert(statsEntity)
    }

    private fun mapCachedProfile(entity: UserEntity): UserProfile {
        val statsEntity = database.userStatsDao().getByUserId(entity.id)
        return UserProfile(
            id = entity.id,
            email = entity.email,
            username = entity.username,
            avatarUrl = entity.avatarUrl,
            language = entity.language,
            createdAt = entity.createdAt,
            stats = UserStats(
                totalSessions = statsEntity?.totalSessions ?: 0,
                totalTimeMs = statsEntity?.totalTimeMs ?: 0,
                currentStreak = statsEntity?.currentStreak ?: 0,
                longestStreak = statsEntity?.longestStreak ?: 0,
                lastPlayedAt = statsEntity?.lastPlayedAt
            )
        )
    }

    private fun getLocalPreferences(): UserPreferences {
        return UserPreferences(
            language = preferences.language.first() ?: "en",
            reminders = getLocalReminders(),
            difficulty = getLocalDifficultyPreferences(),
            colorblindMode = preferences.colorblindMode.first() ?: false,
            reducedMotion = preferences.reducedMotion.first() ?: false,
            largeText = preferences.largeText.first() ?: false,
            analyticsOptIn = preferences.analyticsOptIn.first() ?: false
        )
    }

    private fun getLocalReminders(): ReminderSettings {
        return ReminderSettings(
            enabled = preferences.remindersEnabled.first() ?: false,
            time = preferences.reminderTime.first() ?: "19:00",
            timezone = preferences.reminderTimezone.first() ?: "UTC",
            daysOfWeek = preferences.reminderDays.first()?.split(",") ?: listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
            tone = preferences.reminderTone.first() ?: "GENTLE",
            message = preferences.reminderMessage.first() ?: ""
        )
    }

    private fun getLocalDifficultyPreferences(): DifficultyPreferences {
        return DifficultyPreferences(
            schulte = DifficultyPreference.valueOf(preferences.difficultySchulte.first() ?: "MAINTAIN"),
            blindfold = DifficultyPreference.valueOf(preferences.difficultyBlindfold.first() ?: "MAINTAIN"),
            nonDominant = DifficultyPreference.valueOf(preferences.difficultyNonDominant.first() ?: "MAINTAIN"),
            stroop = DifficultyPreference.valueOf(preferences.difficultyStroop.first() ?: "MAINTAIN")
        )
    }

    private fun updateLocalPreferences(prefs: UserPreferences) {
        prefs.language?.let { preferences.setLanguage(it) }
        prefs.reminders?.let { updateLocalReminders(it) }
        prefs.difficulty?.let { updateLocalDifficultyPreferences(it) }
        prefs.colorblindMode?.let { preferences.setColorblindMode(it) }
        prefs.reducedMotion?.let { preferences.setReducedMotion(it) }
        prefs.largeText?.let { preferences.setLargeText(it) }
        prefs.analyticsOptIn?.let { preferences.setAnalyticsOptIn(it) }
    }

    private fun updateLocalReminders(settings: ReminderSettings) {
        preferences.setRemindersEnabled(settings.enabled)
        preferences.setReminderTime(settings.time)
        preferences.setReminderTimezone(settings.timezone)
        preferences.setReminderDays(settings.daysOfWeek.joinToString(","))
        preferences.setReminderTone(settings.tone)
        preferences.setReminderMessage(settings.message)
    }

    private fun updateLocalDifficultyPreferences(prefs: DifficultyPreferences) {
        preferences.setDifficultySchulte(prefs.schulte.name)
        preferences.setDifficultyBlindfold(prefs.blindfold.name)
        preferences.setDifficultyNonDominant(prefs.nonDominant.name)
        preferences.setDifficultyStroop(prefs.stroop.name)
    }

    // Mappers
    private fun mapProfile(dto: UserProfileResponse): UserProfile {
        return UserProfile(
            id = UUID.fromString(dto.id),
            email = dto.email,
            username = dto.username,
            avatarUrl = dto.avatarUrl,
            language = dto.language,
            createdAt = Instant.parse(dto.createdAt),
            stats = UserStats(
                totalSessions = dto.stats.totalSessions,
                totalTimeMs = dto.stats.totalTimeMs,
                currentStreak = dto.stats.currentStreak,
                longestStreak = dto.stats.longestStreak,
                lastPlayedAt = dto.stats.lastPlayedAt?.let { Instant.parse(it) }
            )
        )
    }

    private fun mapAuthResult(dto: AuthResponse): AuthResult {
        return AuthResult(
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken,
            user = mapProfile(dto.user)
        )
    }

    private fun mapPreferences(dto: UserPreferencesResponse): UserPreferences {
        return UserPreferences(
            language = dto.language,
            reminders = mapReminderSettingsFromResponse(dto.reminders),
            difficulty = mapDifficultyPreferencesFromResponse(dto.difficulty),
            colorblindMode = dto.colorblindMode,
            reducedMotion = dto.reducedMotion,
            largeText = dto.largeText,
            analyticsOptIn = dto.analyticsOptIn
        )
    }

    private fun mapReminderSettingsFromResponse(dto: ReminderSettingsResponse): ReminderSettings {
        return ReminderSettings(
            enabled = dto.enabled,
            time = dto.time,
            timezone = dto.timezone,
            daysOfWeek = dto.daysOfWeek,
            tone = dto.tone,
            message = dto.message
        )
    }

    private fun mapReminderSettings(dto: ReminderSettings): ReminderSettingsRequest {
        return ReminderSettingsRequest(
            enabled = dto.enabled,
            time = dto.time,
            timezone = dto.timezone,
            daysOfWeek = dto.daysOfWeek,
            tone = dto.tone,
            message = dto.message
        )
    }

    private fun mapDifficultyPreferencesFromResponse(dto: DifficultyPreferencesResponse): DifficultyPreferences {
        return DifficultyPreferences(
            schulte = DifficultyPreference.valueOf(dto.schulte),
            blindfold = DifficultyPreference.valueOf(dto.blindfold),
            nonDominant = DifficultyPreference.valueOf(dto.nonDominant),
            stroop = DifficultyPreference.valueOf(dto.stroop)
        )
    }

    private fun mapDifficultyPreferences(dto: DifficultyPreferences): DifficultyPreferencesRequest {
        return DifficultyPreferencesRequest(
            schulte = dto.schulte.name,
            blindfold = dto.blindfold.name,
            nonDominant = dto.nonDominant.name,
            stroop = dto.stroop.name
        )
    }
}