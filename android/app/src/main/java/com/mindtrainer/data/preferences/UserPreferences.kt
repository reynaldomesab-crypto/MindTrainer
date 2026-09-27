package com.mindtrainer.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder
import androidx.datastore.preferences.rxjava2.preferencesDataStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(@Suppress("UNUSED_PARAMETER") context: Context) {

    private val dataStore = context.preferencesDataStore("user_preferences")

    // Keys
    private val KEY_LANGUAGE = preferencesKey<String>("language")
    private val KEY_REMINDERS_ENABLED = preferencesKey<Boolean>("reminders_enabled")
    private val KEY_REMINDER_TIME = preferencesKey<String>("reminder_time")
    private val KEY_REMINDER_TIMEZONE = preferencesKey<String>("reminder_timezone")
    private val KEY_REMINDER_DAYS = preferencesKey<String>("reminder_days")
    private val KEY_REMINDER_TONE = preferencesKey<String>("reminder_tone")
    private val KEY_REMINDER_MESSAGE = preferencesKey<String>("reminder_message")
    private val KEY_DIFFICULTY_SCHULTE = preferencesKey<String>("difficulty_schulte")
    private val KEY_DIFFICULTY_BLINDFOLD = preferencesKey<String>("difficulty_blindfold")
    private val KEY_DIFFICULTY_NON_DOMINANT = preferencesKey<String>("difficulty_non_dominant")
    private val KEY_DIFFICULTY_STROOP = preferencesKey<String>("difficulty_stroop")
    private val KEY_COLORBLIND_MODE = preferencesKey<Boolean>("colorblind_mode")
    private val KEY_REDUCED_MOTION = preferencesKey<Boolean>("reduced_motion")
    private val KEY_LARGE_TEXT = preferencesKey<Boolean>("large_text")
    private val KEY_ANALYTICS_OPT_IN = preferencesKey<Boolean>("analytics_opt_in")
    private val KEY_HANDEDNESS = preferencesKey<String>("handedness")
    private val KEY_ONBOARDING_COMPLETE = preferencesKey<Boolean>("onboarding_complete")
    private val KEY_AUTH_TOKEN = preferencesKey<String>("auth_token")
    private val KEY_REFRESH_TOKEN = preferencesKey<String>("refresh_token")
    private val KEY_USER_ID = preferencesKey<String>("user_id")

    // Language
    val language: Flowable<String> = dataStore.data
        .map { it[KEY_LANGUAGE] ?: "en" }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    // Reminders
    val remindersEnabled: Flowable<Boolean> = dataStore.data
        .map { it[KEY_REMINDERS_ENABLED] ?: false }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_REMINDERS_ENABLED] = enabled }
    }

    val reminderTime: Flowable<String> = dataStore.data
        .map { it[KEY_REMINDER_TIME] ?: "19:00" }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { it[KEY_REMINDER_TIME] = time }
    }

    val reminderTimezone: Flowable<String> = dataStore.data
        .map { it[KEY_REMINDER_TIMEZONE] ?: "UTC" }

    suspend fun setReminderTimezone(timezone: String) {
        dataStore.edit { it[KEY_REMINDER_TIMEZONE] = timezone }
    }

    val reminderDays: Flowable<String> = dataStore.data
        .map { it[KEY_REMINDER_DAYS] ?: "127" } // All days by default

    suspend fun setReminderDays(days: String) {
        dataStore.edit { it[KEY_REMINDER_DAYS] = days }
    }

    val reminderTone: Flowable<String> = dataStore.data
        .map { it[KEY_REMINDER_TONE] ?: "GENTLE" }

    suspend fun setReminderTone(tone: String) {
        dataStore.edit { it[KEY_REMINDER_TONE] = tone }
    }

    val reminderMessage: Flowable<String> = dataStore.data
        .map { it[KEY_REMINDER_MESSAGE] ?: "" }

    suspend fun setReminderMessage(message: String) {
        dataStore.edit { it[KEY_REMINDER_MESSAGE] = message }
    }

    // Difficulty Preferences
    val difficultySchulte: Flowable<String> = dataStore.data
        .map { it[KEY_DIFFICULTY_SCHULTE] ?: "MAINTAIN" }

    suspend fun setDifficultySchulte(preference: String) {
        dataStore.edit { it[KEY_DIFFICULTY_SCHULTE] = preference }
    }

    val difficultyBlindfold: Flowable<String> = dataStore.data
        .map { it[KEY_DIFFICULTY_BLINDFOLD] ?: "MAINTAIN" }

    suspend fun setDifficultyBlindfold(preference: String) {
        dataStore.edit { it[KEY_DIFFICULTY_BLINDFOLD] = preference }
    }

    val difficultyNonDominant: Flowable<String> = dataStore.data
        .map { it[KEY_DIFFICULTY_NON_DOMINANT] ?: "MAINTAIN" }

    suspend fun setDifficultyNonDominant(preference: String) {
        dataStore.edit { it[KEY_DIFFICULTY_NON_DOMINANT] = preference }
    }

    val difficultyStroop: Flowable<String> = dataStore.data
        .map { it[KEY_DIFFICULTY_STROOP] ?: "MAINTAIN" }

    suspend fun setDifficultyStroop(preference: String) {
        dataStore.edit { it[KEY_DIFFICULTY_STROOP] = preference }
    }

    // Accessibility
    val colorblindMode: Flowable<Boolean> = dataStore.data
        .map { it[KEY_COLORBLIND_MODE] ?: false }

    suspend fun setColorblindMode(enabled: Boolean) {
        dataStore.edit { it[KEY_COLORBLIND_MODE] = enabled }
    }

    val reducedMotion: Flowable<Boolean> = dataStore.data
        .map { it[KEY_REDUCED_MOTION] ?: false }

    suspend fun setReducedMotion(enabled: Boolean) {
        dataStore.edit { it[KEY_REDUCED_MOTION] = enabled }
    }

    val largeText: Flowable<Boolean> = dataStore.data
        .map { it[KEY_LARGE_TEXT] ?: false }

    suspend fun setLargeText(enabled: Boolean) {
        dataStore.edit { it[KEY_LARGE_TEXT] = enabled }
    }

    // Analytics
    val analyticsOptIn: Flowable<Boolean> = dataStore.data
        .map { it[KEY_ANALYTICS_OPT_IN] ?: false }

    suspend fun setAnalyticsOptIn(optIn: Boolean) {
        dataStore.edit { it[KEY_ANALYTICS_OPT_IN] = optIn }
    }

    // Handedness
    val handedness: Flowable<String> = dataStore.data
        .map { it[KEY_HANDEDNESS] ?: "RIGHT" }

    suspend fun setHandedness(handedness: String) {
        dataStore.edit { it[KEY_HANDEDNESS] = handedness }
    }

    // Onboarding
    val onboardingComplete: Flowable<Boolean> = dataStore.data
        .map { it[KEY_ONBOARDING_COMPLETE] ?: false }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = complete }
    }

    // Auth tokens
    val authToken: Flowable<String?> = dataStore.data
        .map { it[KEY_AUTH_TOKEN] }

    suspend fun setAuthToken(token: String?) {
        dataStore.edit {
            if (token != null) it[KEY_AUTH_TOKEN] = token else it.remove(KEY_AUTH_TOKEN)
        }
    }

    val refreshToken: Flowable<String?> = dataStore.data
        .map { it[KEY_REFRESH_TOKEN] }

    suspend fun setRefreshToken(token: String?) {
        dataStore.edit {
            if (token != null) it[KEY_REFRESH_TOKEN] = token else it.remove(KEY_REFRESH_TOKEN)
        }
    }

    val userId: Flowable<String?> = dataStore.data
        .map { it[KEY_USER_ID] }

    suspend fun setUserId(userId: String?) {
        dataStore.edit {
            if (userId != null) it[KEY_USER_ID] = userId else it.remove(KEY_USER_ID)
        }
    }

    // Clear all (logout)
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}