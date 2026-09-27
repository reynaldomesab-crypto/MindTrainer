package com.mindtrainer.exercise.blindfold

import com.mindtrainer.common.exception.ResourceNotFoundException
import com.mindtrainer.common.security.CurrentUser
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.usecase.GetDailyExerciseUseCase
import com.mindtrainer.domain.usecase.SubmitExerciseUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/exercises/blindfold")
@Tag(name = "Exercises - Blindfold", description = "Blindfold Writing exercise endpoints")
class BlindfoldController(
    private val getDailyUseCase: GetDailyExerciseUseCase,
    private val submitUseCase: SubmitExerciseUseCase
) {

    @GetMapping("/daily")
    @Operation(summary = "Get today's blindfold text")
    fun getDaily(
        @AuthenticationPrincipal user: CurrentUser,
        @RequestParam(defaultValue = "en") lang: String
    ): ResponseEntity<BlindfoldDailyResponse> {
        val params = mapOf("lang" to lang)
        val result = getDailyUseCase("blindfold", params)
        
        return when (result) {
            is Result.Success -> {
                val daily = result.data as BlindfoldDaily
                ResponseEntity.ok(BlindfoldDailyResponse(
                    text = BlindfoldText(
                        id = daily.text.id,
                        language = daily.text.language,
                        text = daily.text.text,
                        charCount = daily.text.charCount,
                        tier = daily.text.tier,
                        topic = daily.text.topic,
                        rareCharDensity = daily.text.rareCharDensity
                    ),
                    expiresAt = daily.expiresAt.toString()
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Daily exercise not available")
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit blindfold session result")
    fun submit(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: BlindfoldSessionRequest
    ): ResponseEntity<BlindfoldSessionResponse> {
        val domainRequest = BlindfoldSubmitRequest(
            textId = request.textId,
            actualText = request.actualText,
            keyboardLayout = request.keyboardLayout,
            keyDistances = request.keyDistances,
            rmsError = request.rmsError,
            timeMs = request.timeMs
        )
        
        val result = submitUseCase("blindfold", domainRequest)
        
        return when (result) {
            is Result.Success -> {
                val domainResult = result.data as BlindfoldResult
                ResponseEntity.ok(BlindfoldSessionResponse(
                    sessionId = domainResult.sessionId.toString(),
                    score = domainResult.score,
                    rmsError = domainResult.rmsError,
                    accuracy = domainResult.accuracy,
                    lengthFactor = domainResult.lengthFactor,
                    level = domainResult.level,
                    nextLevel = domainResult.nextLevel
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Failed to submit session")
        }
    }
}