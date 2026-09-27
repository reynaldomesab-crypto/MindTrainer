package com.mindtrainer.exercise.schulte

import com.mindtrainer.common.exception.ResourceNotFoundException
import com.mindtrainer.common.security.CurrentUser
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.usecase.GetDailyExerciseUseCase
import com.mindtrainer.domain.usecase.SubmitExerciseUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/exercises/schulte")
@Tag(name = "Exercises - Schulte", description = "Schulte Tables exercise endpoints")
class SchulteController(
    private val getDailyUseCase: GetDailyExerciseUseCase,
    private val submitUseCase: SubmitExerciseUseCase
) {

    @GetMapping("/daily")
    @Operation(summary = "Get today's Schulte table")
    fun getDaily(
        @AuthenticationPrincipal user: CurrentUser,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<SchulteDailyResponse> {
        val params = mapOf("size" to size.toString())
        val result = getDailyUseCase("schulte", params)
        
        return when (result) {
            is Result.Success -> {
                val daily = result.data as SchulteDaily
                val config = SchulteTableConfig(
                    seed = daily.config.seed,
                    size = daily.config.size,
                    timeLimitMs = daily.config.timeLimitMs,
                    showNumbers = daily.config.showNumbers
                )
                ResponseEntity.ok(SchulteDailyResponse(
                    config = config,
                    numbers = daily.numbers,
                    expiresAt = daily.expiresAt.toString()
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Daily exercise not available")
        }
    }

    @GetMapping("/package")
    @Operation(summary = "Download package of Schulte tables")
    fun getPackage(
        @AuthenticationPrincipal user: CurrentUser,
        @RequestParam(defaultValue = "500") count: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<SchultePackageResponse> {
        val params = mapOf("count" to count.toString(), "size" to size.toString())
        val result = getDailyUseCase("schulte_package", params)
        
        return when (result) {
            is Result.Success -> {
                val pkg = result.data as SchultePackage
                val tables = pkg.tables.map { t ->
                    SchulteTableConfig(
                        seed = t.seed,
                        size = t.size,
                        timeLimitMs = t.config.timeLimitMs,
                        showNumbers = t.config.showNumbers
                    )
                }
                ResponseEntity.ok(SchultePackageResponse(
                    tables = tables,
                    generatedAt = pkg.generatedAt.toString()
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Package not available")
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit Schulte session result")
    fun submit(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: SchulteSessionRequest
    ): ResponseEntity<SchulteSessionResponse> {
        val domainRequest = SchulteSubmitRequest(
            config = SchulteTableConfig(
                seed = request.config.seed,
                size = request.config.size,
                timeLimitMs = request.config.timeLimitMs,
                showNumbers = request.config.showNumbers
            ),
            completionTimeMs = request.completionTimeMs,
            tappedSequence = request.tappedSequence,
            accuracy = request.accuracy
        )
        
        val result = submitUseCase("schulte", domainRequest)
        
        return when (result) {
            is Result.Success -> {
                val domainResult = result.data as SchulteResult
                ResponseEntity.ok(SchulteSessionResponse(
                    sessionId = domainResult.sessionId.toString(),
                    score = domainResult.score,
                    accuracy = domainResult.accuracy,
                    level = domainResult.level,
                    nextLevel = domainResult.nextLevel
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Failed to submit session")
        }
    }
}