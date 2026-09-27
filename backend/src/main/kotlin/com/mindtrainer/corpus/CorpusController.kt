package com.mindtrainer.corpus

import com.mindtrainer.common.security.CurrentUser
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.usecase.GetDailyExerciseUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/corpus")
@Tag(name = "Corpus", description = "Exercise content corpus")
class CorpusController(
    private val getDailyUseCase: GetDailyExerciseUseCase
) {

    @GetMapping("/schulte/package")
    @Operation(summary = "Download Schulte tables package")
    fun getSchultePackage(
        @RequestParam lang: String,
        @RequestParam(defaultValue = "500") count: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<CorpusPackageResponse> {
        val params = mapOf("lang" to lang, "count" to count.toString(), "size" to size.toString())
        val result = getDailyUseCase("schulte_corpus", params)
        
        return when (result) {
            is Result.Success -> {
                val pkg = result.data as CorpusPackage
                ResponseEntity.ok(CorpusPackageResponse(
                    language = pkg.language,
                    exerciseType = pkg.exerciseType,
                    items = pkg.items,
                    count = pkg.count,
                    generatedAt = pkg.generatedAt.toString()
                ))
            }
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/blindfold/texts")
    @Operation(summary = "Get blindfold texts")
    fun getBlindfoldTexts(
        @RequestParam lang: String,
        @RequestParam(required = false) tier: Int?,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<CorpusPackageResponse> {
        val params = mapOf(
            "lang" to lang,
            "tier" to tier?.toString() ?: "",
            "limit" to limit.toString()
        )
        val result = getDailyUseCase("blindfold_corpus", params)
        
        return when (result) {
            is Result.Success -> {
                val pkg = result.data as CorpusPackage
                ResponseEntity.ok(CorpusPackageResponse(
                    language = pkg.language,
                    exerciseType = pkg.exerciseType,
                    items = pkg.items,
                    count = pkg.count,
                    generatedAt = pkg.generatedAt.toString()
                ))
            }
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/nondom/texts")
    @Operation(summary = "Get non-dominant hand texts")
    fun getNonDomTexts(
        @RequestParam lang: String,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<CorpusPackageResponse> {
        val params = mapOf("lang" to lang, "limit" to limit.toString())
        val result = getDailyUseCase("nondom_corpus_texts", params)
        
        return when (result) {
            is Result.Success -> {
                val pkg = result.data as CorpusPackage
                ResponseEntity.ok(CorpusPackageResponse(
                    language = pkg.language,
                    exerciseType = pkg.exerciseType,
                    items = pkg.items,
                    count = pkg.count,
                    generatedAt = pkg.generatedAt.toString()
                ))
            }
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/nondom/paths")
    @Operation(summary = "Get tracing paths")
    fun getNonDomPaths(): ResponseEntity<CorpusPackageResponse> {
        val result = getDailyUseCase("nondom_corpus_paths", emptyMap())
        
        return when (result) {
            is Result.Success -> {
                val pkg = result.data as CorpusPackage
                ResponseEntity.ok(CorpusPackageResponse(
                    language = pkg.language,
                    exerciseType = pkg.exerciseType,
                    items = pkg.items,
                    count = pkg.count,
                    generatedAt = pkg.generatedAt.toString()
                ))
            }
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/nondom/sequences")
    @Operation(summary = "Get tap sequences")
    fun getNonDomSequences(): ResponseEntity<CorpusPackageResponse> {
        val result = getDailyUseCase("nondom_corpus_sequences", emptyMap())
        
        return when (result) {
            is Result.Success -> {
                val pkg = result.data as CorpusPackage
                ResponseEntity.ok(CorpusPackageResponse(
                    language = pkg.language,
                    exerciseType = pkg.exerciseType,
                    items = pkg.items,
                    count = pkg.count,
                    generatedAt = pkg.generatedAt.toString()
                ))
            }
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }
}