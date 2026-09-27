package com.mindtrainer.exercise.nondominant

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
@RequestMapping("/api/v1/exercises/nondom")
@Tag(name = "Exercises - NonDominant", description = "Non-Dominant Hand exercise endpoints")
class NonDominantController(
    private val getDailyUseCase: GetDailyExerciseUseCase,
    private val submitUseCase: SubmitExerciseUseCase
) {

    @GetMapping("/daily")
    @Operation(summary = "Get today's non-dominant hand tasks")
    fun getDaily(
        @AuthenticationPrincipal user: CurrentUser,
        @RequestParam(defaultValue = "en") lang: String
    ): ResponseEntity<NonDomDailyResponse> {
        val params = mapOf("lang" to lang)
        val result = getDailyUseCase("nondominant", params)
        
        return when (result) {
            is Result.Success -> {
                val daily = result.data as NonDomDaily
                ResponseEntity.ok(NonDomDailyResponse(
                    copywriting = BlindfoldText(
                        id = daily.copywriting.id,
                        language = daily.copywriting.language,
                        text = daily.copywriting.text,
                        charCount = daily.copywriting.charCount,
                        tier = daily.copywriting.tier,
                        topic = daily.copywriting.topic,
                        rareCharDensity = daily.copywriting.rareCharDensity
                    ),
                    tracing = ShapePath(
                        id = daily.tracing.id,
                        type = daily.tracing.type,
                        points = daily.tracing.points.map { PointDto(it.x, it.y) },
                        targetTimeMs = daily.tracing.targetTimeMs,
                        difficulty = daily.tracing.difficulty
                    ),
                    tapping = TapSequence(
                        id = daily.tapping.id,
                        gridSize = daily.tapping.gridSize,
                        targets = daily.tapping.targets,
                        count = daily.tapping.count,
                        timeLimitMs = daily.tapping.timeLimitMs
                    ),
                    expiresAt = daily.expiresAt.toString()
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Daily exercise not available")
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit non-dominant hand session result")
    fun submit(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: NonDomSessionRequest
    ): ResponseEntity<NonDomSessionResponse> {
        val domainRequest = NonDomSubmitRequest(
            taskType = request.taskType,
            contentId = request.contentId,
            handUsed = request.handUsed,
            metrics = request.metrics
        )
        
        val result = submitUseCase("nondominant", domainRequest)
        
        return when (result) {
            is Result.Success -> {
                val domainResult = result.data as NonDomResult
                ResponseEntity.ok(NonDomSessionResponse(
                    sessionId = domainResult.sessionId.toString(),
                    score = domainResult.score,
                    taskScore = domainResult.taskScore,
                    level = domainResult.level,
                    nextLevel = domainResult.nextLevel
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Failed to submit session")
        }
    }
}