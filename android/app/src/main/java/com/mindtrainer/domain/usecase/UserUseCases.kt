package com.mindtrainer.domain.usecase

import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.repository.UserRepository
import com.mindtrainer.domain.model.Result
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(action: AuthAction): Result<AuthResult> {
        return when (action) {
            is AuthAction.Register -> repository.register(action.request)
            is AuthAction.Login -> repository.login(action.request)
            is AuthAction.OAuthLogin -> repository.oauthLogin(action.provider, action.token)
            is AuthAction.RefreshToken -> repository.refreshToken(action.refreshToken)
            is AuthAction.Logout -> repository.logout().map { AuthResult("", "", UserProfile(UUID.randomUUID(), "", "", null, "", Instant.now(), UserStats(0, 0, 0, 0, null))) }
        }
    }

    sealed interface AuthAction {
        data class Register(val request: RegisterRequest) : AuthAction
        data class Login(val request: LoginRequest) : AuthAction
        data class OAuthLogin(val provider: String, val token: String) : AuthAction
        data class RefreshToken(val refreshToken: String) : AuthAction
        object Logout : AuthAction
    }
}

class UpdatePreferencesUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(request: UpdatePreferencesRequest): Result<UserPreferences> {
        return repository.updatePreferences(request)
    }
}

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Result<UserProfile> {
        return repository.getProfile()
    }
}

class GetRemindersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Result<ReminderSettings> {
        return repository.getReminders()
    }
}

class UpdateRemindersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(request: ReminderSettings): Result<ReminderSettings> {
        return repository.updateReminders(request)
    }
}

class GetDifficultyPreferencesUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Result<DifficultyPreferences> {
        return repository.getDifficultyPreferences()
    }
}

class UpdateDifficultyPreferencesUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(request: DifficultyPreferences): Result<DifficultyPreferences> {
        return repository.updateDifficultyPreferences(request)
    }
}