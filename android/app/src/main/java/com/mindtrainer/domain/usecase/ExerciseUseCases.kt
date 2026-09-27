package com.mindtrainer.domain.usecase

import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.repository.ExerciseRepository
import com.mindtrainer.domain.model.Result
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDailyExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    operator fun invoke(exerciseType: String, params: Map<String, String> = emptyMap()): Result<Any> {
        return when (exerciseType) {
            "schulte" -> repository.getSchulteDaily(params["size"]?.toInt() ?: 5)
            "blindfold" -> repository.getBlindfoldDaily(params["lang"] ?: "en")
            "nondominant" -> repository.getNonDomDaily(params["lang"] ?: "en")
            "stroop" -> repository.getStroopDaily(
                params["mode"] ?: "CLASSIC",
                params["lang"] ?: "en"
            )
            else -> Result.Error(IllegalArgumentException("Unknown exercise type: $exerciseType"), "Unknown exercise type")
        }
    }
}

class SubmitExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    operator fun invoke(exerciseType: String, request: Any): Result<Any> {
        return when (exerciseType) {
            "schulte" -> {
                val req = request as SchulteSubmitRequest
                repository.submitSchulte(req)
            }
            "blindfold" -> {
                val req = request as BlindfoldSubmitRequest
                repository.submitBlindfold(req)
            }
            "nondominant" -> {
                val req = request as NonDomSubmitRequest
                repository.submitNonDom(req)
            }
            "stroop" -> {
                val req = request as StroopSubmitRequest
                repository.submitStroop(req)
            }
            else -> Result.Error(IllegalArgumentException("Unknown exercise type: $exerciseType"), "Unknown exercise type")
        }
    }
}

class GetProgressUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    operator fun invoke(): Result<ProgressDashboard> {
        return repository.getDashboard()
    }
}

class GetExerciseProgressUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    operator fun invoke(exerciseType: String, days: Int = 30): Result<ExerciseProgress> {
        return repository.getExerciseProgress(exerciseType, days)
    }
}

class GetInsightsUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    operator fun invoke(): Result<List<Insight>> {
        return repository.getInsights()
    }
}