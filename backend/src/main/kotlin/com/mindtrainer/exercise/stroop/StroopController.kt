package com.mindtrainer.exercise.stroop

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
@RequestMapping("/api/v1/exercises/stroop")
@Tag(name = "Exercises - Stroop", description = "Stroop Challenge exercise endpoints")
class StroopController(
    private val getDailyUseCase: GetDailyExerciseUseCase,
    private val submitUseCase: SubmitExerciseUseCase
) {

    @GetMapping("/daily")
    @Operation(summary = "Get today's Stroop challenge")
    fun getDaily(
        @AuthenticationPrincipal user: CurrentUser,
        @RequestParam(defaultValue = "CLASSIC") mode: String,
        @RequestParam(defaultValue = "en") lang: String
    ): ResponseEntity<StroopDailyResponse> {
        val params = mapOf("mode" to mode, "lang" to lang)
        val result = getDailyUseCase("stroop", params)
        
        return when (result) {
            is Result.Success -> {
                val daily = result.data as StroopDaily
                ResponseEntity.ok(StroopDailyResponse(
                    mode = daily.mode,
                    config = StroopSessionConfig(
                        colorCount = daily.config.colorCount,
                        incongruentRatio = daily.config.incongruentRatio,
                        stimulusDurationMs = daily.config.stimulusDurationMs,
                        switchFrequency = daily.config.switchFrequency,
                        sequenceLength = daily.config.sequenceLength
                    ),
                    stimuli = daily.stimuli.map { s ->
                        StroopStimulus(
                            word = s.word,
                            inkColor = s.inkColor,
                            position = s.position,
                            audioColor = s.audioColor
                        )
                    },
                    expiresAt = daily.expiresAt.toString()
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Daily exercise not available")
        }
    }

    @GetMapping("/modes")
    @Operation(summary = "Get available Stroop modes")
    fun getModes(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(listOf("CLASSIC", "REVERSE", "SWITCH", "SPATIAL", "SPEED", "SEQUENCE"))
    }

    @GetMapping("/wordsets")
    @Operation(summary = "Get word sets for language")
    fun getWordSets(
        @RequestParam lang: String
    ): ResponseEntity<StroopWordSetsResponse> {
        // This would typically come from corpus service
        return ResponseEntity.ok(StroopWordSetsResponse(
            language = lang,
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE"),
            neutralWords = listOf("DOG", "HOUSE", "TABLE", "BOOK", "CAR", "TREE"),
            emotionalWords = listOf("ANXIETY", "FEAR", "JOY", "CALM", "ANGER")
        ))
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit Stroop session result")
    fun submit(
        @AuthenticationPrincipal user: CurrentUser,
        @Valid @RequestBody request: StroopSessionRequest
    ): ResponseEntity<StroopSessionResponse> {
        val trials = request.trials.map { t ->
            StroopTrialRequest(
                stimulus = StroopStimulus(
                    word = t.stimulus.word,
                    inkColor = t.stimulus.inkColor,
                    position = t.stimulus.position,
                    audioColor = t.stimulus.audioColor
                ),
                response = t.response,
                rtMs = t.rtMs,
                correct = t.correct,
                switchTrial = t.switchTrial
            )
        }
        
        val domainRequest = StroopSubmitRequest(
            mode = request.mode,
            trials = trials
        )
        
        val result = submitUseCase("stroop", domainRequest)
        
        return when (result) {
            is Result.Success -> {
                val domainResult = result.data as StroopResult
                ResponseEntity.ok(StroopSessionResponse(
                    sessionId = domainResult.sessionId.toString(),
                    interferenceEffect = domainResult.interferenceEffect,
                    interferenceRatio = domainResult.interferenceRatio,
                    switchCost = domainResult.switchCost,
                    throughput = domainResult.throughput,
                    inhibitionIndex = domainResult.inhibitionIndex,
                    flexibilityIndex = domainResult.flexibilityIndex,
                    level = domainResult.level,
                    nextLevel = domainResult.nextLevel
                ))
            }
            is Result.Error -> throw ResourceNotFoundException(result.message ?: "Failed to submit session")
        }
    }
}