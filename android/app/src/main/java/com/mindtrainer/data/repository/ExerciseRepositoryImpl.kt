package com.mindtrainer.data.repository

import com.mindtrainer.data.api.MindTrainerApi
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.data.local.AppDatabase
import com.mindtrainer.data.local.dao.*
import com.mindtrainer.data.local.entity.*
import com.mindtrainer.data.preferences.UserPreferences
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.model.Result
import com.mindtrainer.domain.repository.ExerciseRepository
import java.util.UUID
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val api: MindTrainerApi,
    private val database: AppDatabase,
    private val preferences: UserPreferences
) : ExerciseRepository {

    private val userId: UUID? = UUID.fromString(preferences.userId.first() ?: "")

    override suspend fun getSchulteDaily(size: Int): Result<SchulteDaily> {
        return try {
            val response = api.getSchulteDaily(size)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapSchulteDaily(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get daily Schulte table")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getSchultePackage(count: Int, size: Int): Result<SchultePackage> {
        return try {
            val response = api.getSchultePackage(count, size)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapSchultePackage(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get Schulte package")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun submitSchulte(request: SchulteSubmitRequest): Result<SchulteResult> {
        return try {
            val dto = SchulteSessionRequest(
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
            val response = api.submitSchulte(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapSchulteResult(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to submit Schulte session")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getBlindfoldDaily(language: String): Result<BlindfoldDaily> {
        return try {
            val response = api.getBlindfoldDaily(language)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapBlindfoldDaily(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get daily blindfold text")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun submitBlindfold(request: BlindfoldSubmitRequest): Result<BlindfoldResult> {
        return try {
            val dto = BlindfoldSessionRequest(
                textId = request.textId,
                actualText = request.actualText,
                keyboardLayout = request.keyboardLayout,
                keyDistances = request.keyDistances,
                rmsError = request.rmsError,
                timeMs = request.timeMs
            )
            val response = api.submitBlindfold(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapBlindfoldResult(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to submit blindfold session")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getNonDomDaily(language: String): Result<NonDomDaily> {
        return try {
            val response = api.getNonDomDaily(language)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapNonDomDaily(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get daily non-dominant tasks")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun submitNonDom(request: NonDomSubmitRequest): Result<NonDomResult> {
        return try {
            val dto = NonDomSessionRequest(
                taskType = request.taskType,
                contentId = request.contentId,
                handUsed = request.handUsed,
                metrics = request.metrics
            )
            val response = api.submitNonDom(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapNonDomResult(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to submit non-dominant session")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getStroopDaily(mode: String, language: String): Result<StroopDaily> {
        return try {
            val response = api.getStroopDaily(mode, language)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapStroopDaily(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get daily Stroop challenge")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getStroopModes(): Result<List<String>> {
        return try {
            val response = api.getStroopModes()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get Stroop modes")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getStroopWordSets(language: String): Result<StroopWordSets> {
        return try {
            val response = api.getStroopWordSets(language)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapStroopWordSets(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get Stroop word sets")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun submitStroop(request: StroopSubmitRequest): Result<StroopResult> {
        return try {
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
            val dto = StroopSessionRequest(
                mode = request.mode,
                trials = trials
            )
            val response = api.submitStroop(dto)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapStroopResult(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to submit Stroop session")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun downloadCorpus(exerciseType: String, language: String, params: Map<String, String>): Result<CorpusPackage> {
        return try {
            val response = when (exerciseType) {
                "schulte" -> api.getSchulteCorpus(
                    language,
                    params["count"]?.toInt() ?: 500,
                    params["size"]?.toInt() ?: 5
                )
                "blindfold" -> api.getBlindfoldCorpus(
                    language,
                    params["tier"]?.toInt(),
                    params["limit"]?.toInt() ?: 50
                )
                "nondominant" -> when (params["task"]) {
                    "texts" -> api.getNonDomCorpus(language, params["limit"]?.toInt() ?: 50)
                    "paths" -> api.getNonDomPaths()
                    "sequences" -> api.getNonDomSequences()
                    else -> throw IllegalArgumentException("Unknown non-dominant task")
                }
                else -> throw IllegalArgumentException("Unknown exercise type: $exerciseType")
            }
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapCorpusPackage(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to download corpus")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    // Mappers
    private fun mapSchulteDaily(dto: SchulteDailyResponse): SchulteDaily {
        return SchulteDaily(
            config = SchulteTableConfig(
                seed = dto.config.seed,
                size = dto.config.size,
                timeLimitMs = dto.config.timeLimitMs,
                showNumbers = dto.config.showNumbers
            ),
            numbers = dto.numbers,
            expiresAt = Instant.parse(dto.expiresAt)
        )
    }

    private fun mapSchultePackage(dto: SchultePackageResponse): SchultePackage {
        return SchultePackage(
            tables = dto.tables.map { t ->
                SchulteTableConfig(
                    seed = t.seed,
                    size = t.size,
                    timeLimitMs = t.timeLimitMs,
                    showNumbers = t.showNumbers
                )
            },
            generatedAt = Instant.parse(dto.generatedAt)
        )
    }

    private fun mapSchulteResult(dto: SchulteSessionResponse): SchulteResult {
        return SchulteResult(
            sessionId = UUID.fromString(dto.sessionId),
            score = dto.score,
            accuracy = dto.accuracy,
            level = dto.level,
            nextLevel = dto.nextLevel
        )
    }

    private fun mapBlindfoldDaily(dto: BlindfoldDailyResponse): BlindfoldDaily {
        return BlindfoldDaily(
            text = BlindfoldText(
                id = dto.text.id,
                language = dto.text.language,
                text = dto.text.text,
                charCount = dto.text.charCount,
                tier = dto.text.tier,
                topic = dto.text.topic,
                rareCharDensity = dto.text.rareCharDensity
            ),
            expiresAt = Instant.parse(dto.expiresAt)
        )
    }

    private fun mapBlindfoldResult(dto: BlindfoldSessionResponse): BlindfoldResult {
        return BlindfoldResult(
            sessionId = UUID.fromString(dto.sessionId),
            score = dto.score,
            rmsError = dto.rmsError,
            accuracy = dto.accuracy,
            lengthFactor = dto.lengthFactor,
            level = dto.level,
            nextLevel = dto.nextLevel
        )
    }

    private fun mapNonDomDaily(dto: NonDomDailyResponse): NonDomDaily {
        return NonDomDaily(
            copywriting = BlindfoldText(
                id = dto.copywriting.id,
                language = dto.copywriting.language,
                text = dto.copywriting.text,
                charCount = dto.copywriting.charCount,
                tier = dto.copywriting.tier,
                topic = dto.copywriting.topic,
                rareCharDensity = dto.copywriting.rareCharDensity
            ),
            tracing = ShapePath(
                id = dto.tracing.id,
                type = dto.tracing.type,
                points = dto.tracing.points.map { Point(it.x, it.y) },
                targetTimeMs = dto.tracing.targetTimeMs,
                difficulty = dto.tracing.difficulty
            ),
            tapping = TapSequence(
                id = dto.tapping.id,
                gridSize = dto.tapping.gridSize,
                targets = dto.tapping.targets,
                count = dto.tapping.count,
                timeLimitMs = dto.tapping.timeLimitMs
            ),
            expiresAt = Instant.parse(dto.expiresAt)
        )
    }

    private fun mapNonDomResult(dto: NonDomSessionResponse): NonDomResult {
        return NonDomResult(
            sessionId = UUID.fromString(dto.sessionId),
            score = dto.score,
            taskScore = dto.taskScore,
            level = dto.level,
            nextLevel = dto.nextLevel
        )
    }

    private fun mapStroopDaily(dto: StroopDailyResponse): StroopDaily {
        return StroopDaily(
            mode = dto.mode,
            config = StroopSessionConfig(
                colorCount = dto.config.colorCount,
                incongruentRatio = dto.config.incongruentRatio,
                stimulusDurationMs = dto.config.stimulusDurationMs,
                switchFrequency = dto.config.switchFrequency,
                sequenceLength = dto.config.sequenceLength
            ),
            stimuli = dto.stimuli.map { s ->
                StroopStimulus(
                    word = s.word,
                    inkColor = s.inkColor,
                    position = s.position,
                    audioColor = s.audioColor
                )
            },
            expiresAt = Instant.parse(dto.expiresAt)
        )
    }

    private fun mapStroopWordSets(dto: StroopWordSetsResponse): StroopWordSets {
        return StroopWordSets(
            language = dto.language,
            colorWords = dto.colorWords,
            neutralWords = dto.neutralWords,
            emotionalWords = dto.emotionalWords
        )
    }

    private fun mapStroopResult(dto: StroopSessionResponse): StroopResult {
        return StroopResult(
            sessionId = UUID.fromString(dto.sessionId),
            interferenceEffect = dto.interferenceEffect,
            interferenceRatio = dto.interferenceRatio,
            switchCost = dto.switchCost,
            throughput = dto.throughput,
            inhibitionIndex = dto.inhibitionIndex,
            flexibilityIndex = dto.flexibilityIndex,
            level = dto.level,
            nextLevel = dto.nextLevel
        )
    }

    private fun mapCorpusPackage(dto: CorpusPackageResponse): CorpusPackage {
        return CorpusPackage(
            language = dto.language,
            exerciseType = dto.exerciseType,
            items = dto.items,
            count = dto.count,
            generatedAt = Instant.parse(dto.generatedAt)
        )
    }
}