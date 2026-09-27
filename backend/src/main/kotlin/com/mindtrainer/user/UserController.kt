package com.mindtrainer.user

import com.mindtrainer.common.security.CurrentUser
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.usecase.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "User", description = "User profile and settings")
class UserController(
    private val getProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: GetUserProfileUseCase, // Would be separate in real impl
    private val getPreferencesUseCase: GetRemindersUseCase, // Would be separate in real impl
    private val updatePreferencesUseCase: UpdatePreferencesUseCase,
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateRemindersUseCase: UpdateRemindersUseCase,
    private val getDifficultyPrefsUseCase: GetDifficultyPreferencesUseCase,
    private val updateDifficultyPrefsUseCase: UpdateDifficultyPreferencesUseCase,
    private val authUseCase: AuthUseCase
) {

    @GetMapping
    @Operation(summary = "Get current user profile")
    fun getProfile(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<UserProfileResponse> {
        val result = getProfileUseCase()
        return when (result) {
            is Result.Success -> ResponseEntity.ok(mapToResponse(result.data as UserProfile))
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }

    @PatchMapping
    @Operation(summary = "Update user profile")
    fun updateProfile(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> {
        // Implementation would call update use case
        return ResponseEntity.ok(UserProfileResponse(
            id = user.id,
            email = user.email,
            username = request.username ?: user.username,
            avatarUrl = request.avatarUrl,
            language = request.language ?: "en",
            createdAt = java.time.Instant.now().toString(),
            stats = UserStatsResponse(0, 0, 0, 0, null)
        ))
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get user preferences")
    fun getPreferences(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<UserPreferencesResponse> {
        // Implementation would call get preferences use case
        return ResponseEntity.ok(UserPreferencesResponse(
            language = "en",
            reminders = ReminderSettingsResponse(false, "19:00", "UTC", listOf(), "GENTLE", ""),
            difficulty = DifficultyPreferencesResponse("MAINTAIN", "MAINTAIN", "MAINTAIN", "MAINTAIN"),
            colorblindMode = false,
            reducedMotion = false,
            largeText = false,
            analyticsOptIn = false
        ))
    }

    @PatchMapping("/preferences")
    @Operation(summary = "Update user preferences")
    fun updatePreferences(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: UserPreferencesRequest
    ): ResponseEntity<UserPreferencesResponse> {
        val result = updatePreferencesUseCase(UpdatePreferencesRequest(
            language = request.language,
            reminders = request.reminders?.let { mapReminder(it) },
            difficulty = request.difficulty?.let { mapDifficulty(it) },
            colorblindMode = request.colorblindMode,
            reducedMotion = request.reducedMotion,
            largeText = request.largeText,
            analyticsOptIn = request.analyticsOptIn
        ))
        return when (result) {
            is Result.Success -> ResponseEntity.ok(mapToResponse(result.data as UserPreferences))
            is Result.Error -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/reminders")
    @Operation(summary = "Get reminder settings")
    fun getReminders(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<ReminderSettingsResponse> {
        val result = getRemindersUseCase()
        return when (result) {
            is Result.Success -> ResponseEntity.ok(mapReminderResponse(result.data as ReminderSettings))
            is Result.Error -> ResponseEntity.ok(ReminderSettingsResponse(false, "19:00", "UTC", listOf(), "GENTLE", ""))
        }
    }

    @PatchMapping("/reminders")
    @Operation(summary = "Update reminder settings")
    fun updateReminders(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: ReminderSettingsRequest
    ): ResponseEntity<ReminderSettingsResponse> {
        val result = updateRemindersUseCase(ReminderSettings(
            enabled = request.enabled ?: false,
            time = request.time ?: "19:00",
            timezone = request.timezone ?: "UTC",
            daysOfWeek = request.daysOfWeek ?: listOf(),
            tone = request.tone ?: "GENTLE",
            message = request.message ?: ""
        ))
        return when (result) {
            is Result.Success -> ResponseEntity.ok(mapReminderResponse(result.data as ReminderSettings))
            is Result.Error -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/difficulty-preference")
    @Operation(summary = "Get difficulty preferences")
    fun getDifficultyPreferences(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<DifficultyPreferencesResponse> {
        val result = getDifficultyPrefsUseCase()
        return when (result) {
            is Result.Success -> ResponseEntity.ok(mapDifficultyResponse(result.data as DifficultyPreferences))
            is Result.Error -> ResponseEntity.ok(DifficultyPreferencesResponse("MAINTAIN", "MAINTAIN", "MAINTAIN", "MAINTAIN"))
        }
    }

    @PatchMapping("/difficulty-preference")
    @Operation(summary = "Update difficulty preferences")
    fun updateDifficultyPreferences(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: DifficultyPreferencesRequest
    ): ResponseEntity<DifficultyPreferencesResponse> {
        val result = updateDifficultyPrefsUseCase(DifficultyPreferences(
            schulte = DifficultyPreference.valueOf(request.schulte ?: "MAINTAIN"),
            blindfold = DifficultyPreference.valueOf(request.blindfold ?: "MAINTAIN"),
            nonDominant = DifficultyPreference.valueOf(request.nonDominant ?: "MAINTAIN"),
            stroop = DifficultyPreference.valueOf(request.stroop ?: "MAINTAIN")
        ))
        return when (result) {
            is Result.Success -> ResponseEntity.ok(mapDifficultyResponse(result.data as DifficultyPreferences))
            is Result.Error -> ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "Logout")
    fun logout(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<Unit> {
        authUseCase(AuthUseCase.AuthAction.Logout)
        return ResponseEntity.noContent().build()
    }

    // Mapping helpers
    private fun mapToResponse(profile: UserProfile): UserProfileResponse {
        return UserProfileResponse(
            id = profile.id.toString(),
            email = profile.email,
            username = profile.username,
            avatarUrl = profile.avatarUrl,
            language = profile.language,
            createdAt = profile.createdAt.toString(),
            stats = UserStatsResponse(
                profile.stats.totalSessions,
                profile.stats.totalTimeMs,
                profile.stats.currentStreak,
                profile.stats.longestStreak,
                profile.stats.lastPlayedAt?.toString()
            )
        )
    }

    private fun mapReminder(s: ReminderSettingsRequest): ReminderSettings {
        return ReminderSettings(
            enabled = s.enabled ?: false,
            time = s.time ?: "19:00",
            timezone = s.timezone ?: "UTC",
            daysOfWeek = s.daysOfWeek ?: listOf(),
            tone = s.tone ?: "GENTLE",
            message = s.message ?: ""
        )
    }

    private fun mapReminderResponse(s: ReminderSettings): ReminderSettingsResponse {
        return ReminderSettingsResponse(s.enabled, s.time, s.timezone, s.daysOfWeek, s.tone, s.message)
    }

    private fun mapReminderResponse(dto: ReminderSettingsResponse): ReminderSettings {
        return ReminderSettings(
            enabled = dto.enabled,
            time = dto.time,
            timezone = dto.timezone,
            daysOfWeek = dto.daysOfWeek,
            tone = dto.tone,
            message = dto.message
        )
    }

    private fun mapDifficulty(dto: DifficultyPreferencesRequest): DifficultyPreferences {
        return DifficultyPreferences(
            schulte = DifficultyPreference.valueOf(dto.schulte ?: "MAINTAIN"),
            blindfold = DifficultyPreference.valueOf(dto.blindfold ?: "MAINTAIN"),
            nonDominant = DifficultyPreference.valueOf(dto.nonDominant ?: "MAINTAIN"),
            stroop = DifficultyPreference.valueOf(dto.stroop ?: "MAINTAIN")
        )
    }

    private fun mapDifficultyResponse(dto: DifficultyPreferences): DifficultyPreferencesResponse {
        return DifficultyPreferencesResponse(
            dto.schulte.name, dto.blindfold.name, dto.nonDominant.name, dto.stroop.name
        )
    }
}