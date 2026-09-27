package com.mindtrainer.logic.blindfold

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.log
import kotlin.math.sqrt

/**
 * Blindfold Writing Scorer
 * Evaluates typing accuracy using keyboard geometry (key distances)
 * instead of simple character matching.
 */
object BlindfoldScorer {

    /**
     * Keyboard layout key positions (normalized 0-1 coordinates).
     * Supports multiple layouts: QWERTY, QWERTZ, AZERTY, etc.
     */
    private val keyboardLayouts: Map<String, Map<Char, KeyPosition>> = mapOf(
        "QWERTY" to buildQwertyLayout(),
        "QWERTZ" to buildQwertzLayout(),
        "AZERTY" to buildAzertyLayout(),
        "QZERTY" to buildQzertyLayout(),
        "JCUKEN" to buildJcukenLayout(),
        "ARABIC_101" to buildArabicLayout(),
        "PINYIN" to buildPinyinLayout(),
        "FLICK" to buildFlickLayout(),
        "GODAN" to buildGodanLayout(),
        "HANGUL_2SET" to buildHangul2SetLayout(),
        "HANGUL_3SET" to buildHangul3SetLayout(),
        "DEVANAGARI" to buildDevanagariLayout()
    )

    /**
     * Scores a blindfold typing attempt.
     * @param expected The target text
     * @param actual What the user typed
     * @param keyboardLayout Keyboard layout identifier
     * @return Scoring result with RMS error, accuracy, and detailed metrics
     */
    fun score(
        expected: String,
        actual: String,
        keyboardLayout: String = "QWERTY"
    ): BlindfoldResult {
        val layout = keyboardLayouts[keyboardLayout.uppercase()] ?: keyboardLayouts["QWERTY"]!!
        
        // Align sequences using Levenshtein-like approach
        val alignment = alignSequences(expected, actual)
        
        var totalKeyDistance = 0.0
        var correctChars = 0
        var substitutions = 0
        var insertions = 0
        var deletions = 0
        
        val keyDistances = mutableListOf<Double>()
        
        for ((expChar, actChar, isMatch) in alignment) {
            val expPos = layout[expChar.uppercaseChar()] ?: KeyPosition(0.5, 0.5)
            val actPos = layout[actChar.uppercaseChar()] ?: KeyPosition(0.5, 0.5)
            
            val distance = hypot(expPos.x - actPos.x, expPos.y - actPos.y)
            keyDistances.add(distance)
            totalKeyDistance += distance
            
            if (isMatch) {
                correctChars++
            } else when {
                expChar == '\0' -> insertions++
                actChar == '\0' -> deletions++
                else -> substitutions++
            }
        }
        
        val rmsError = if (alignment.isNotEmpty()) {
            sqrt(totalKeyDistance / alignment.size)
        } else 0.0
        
        val accuracy = if (expected.isNotEmpty()) correctChars.toDouble() / expected.length else 1.0
        
        // Length factor: longer texts are harder (logarithmic scaling)
        val lengthFactor = 1.0 / (1.0 + log(expected.length / 30.0).coerceAtLeast(0.0))
        
        return BlindfoldResult(
            rmsError = rmsError,
            accuracy = accuracy,
            correctChars = correctChars,
            totalChars = expected.length,
            substitutions = substitutions,
            insertions = insertions,
            deletions = deletions,
            keyDistances = keyDistances,
            lengthFactor = lengthFactor
        )
    }

    /**
     * Aligns expected and actual sequences for comparison.
     * Uses a simplified dynamic programming approach.
     */
    private fun alignSequences(expected: String, actual: String): List<Triple<Char, Char, Boolean>> {
        val m = expected.length
        val n = actual.length
        
        // DP table for edit distance
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        
        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (expected[i - 1] == actual[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution/match
                )
            }
        }
        
        // Backtrack to get alignment
        val alignment = mutableListOf<Triple<Char, Char, Boolean>>()
        var i = m
        var j = n
        
        while (i > 0 || j > 0) {
            when {
                i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (if (expected[i - 1] == actual[j - 1]) 0 else 1) -> {
                    alignment.add(0, expected[i - 1] to actual[j - 1] to (expected[i - 1] == actual[j - 1]))
                    i--
                    j--
                }
                i > 0 && dp[i][j] == dp[i - 1][j] + 1 -> {
                    alignment.add(0, expected[i - 1] to '\0' to false)
                    i--
                }
                j > 0 && dp[i][j] == dp[i][j - 1] + 1 -> {
                    alignment.add(0, '\0' to actual[j - 1] to false)
                    j--
                }
                else -> {
                    // fallback
                    if (i > 0 && j > 0) {
                        alignment.add(0, expected[i - 1] to actual[j - 1] to false)
                        i--
                        j--
                    } else if (i > 0) {
                        alignment.add(0, expected[i - 1] to '\0' to false)
                        i--
                    } else {
                        alignment.add(0, '\0' to actual[j - 1] to false)
                        j--
                    }
                }
            }
        }
        
        return alignment
    }

    // ============ Keyboard Layout Builders ============

    private fun buildQwertyLayout(): Map<Char, KeyPosition> {
        // Rows: top to bottom, normalized 0-1
        val rows = listOf(
            "`1234567890-=",
            "qwertyuiop[]\\",
            "asdfghjkl;'",
            "zxcvbnm,./"
        )
        return buildLayout(rows, keyWidth = 1.0 / 14.0, keyHeight = 1.0 / 5.0, rowOffset = 0.0)
    }

    private fun buildQwertzLayout(): Map<Char, KeyPosition> {
        val rows = listOf(
            "`1234567890-=",
            "qwertzuiopü+",
            "asdfghjklöä#",
            "yxcvbnm,.-"
        )
        return buildLayout(rows, keyWidth = 1.0 / 14.0, keyHeight = 1.0 / 5.0, rowOffset = 0.0)
    }

    private fun buildAzertyLayout(): Map<Char, KeyPosition> {
        val rows = listOf(
            "`1234567890-=",
            "azertyuiop^$",
            "qsdfghjklmù*",
            "wxcvbn,;:!"
        )
        return buildLayout(rows, keyWidth = 1.0 / 14.0, keyHeight = 1.0 / 5.0, rowOffset = 0.0)
    }

    private fun buildQzertyLayout(): Map<Char, KeyPosition> {
        val rows = listOf(
            "`1234567890-=",
            "qzertyuiop^$",
            "asdfghjklmù*",
            "wxcvbn,;:!"
        )
        return buildLayout(rows, keyWidth = 1.0 / 14.0, keyHeight = 1.0 / 5.0, rowOffset = 0.0)
    }

    private fun buildJcukenLayout(): Map<Char, KeyPosition> {
        // Russian JCUKEN layout (simplified)
        val rows = listOf(
            "ё1234567890-=",
            "йцукенгшщзхъ\\",
            "фывапролджэ",
            "ячсмитьбю."
        )
        return buildLayout(rows, keyWidth = 1.0 / 14.0, keyHeight = 1.0 / 5.0, rowOffset = 0.0)
    }

    private fun buildArabicLayout(): Map<Char, KeyPosition> {
        // Arabic 101 layout (simplified)
        val rows = listOf(
            "ذ1234567890-=",
            "ضصثقفغعهخحج",
            "شسيبلاتنمكط",
            "ئءؤرلاىةوزظ"
        )
        return buildLayout(rows, keyWidth = 1.0 / 14.0, keyHeight = 1.0 / 5.0, rowOffset = 0.0)
    }

    private fun buildPinyinLayout(): Map<Char, KeyPosition> {
        // Pinyin uses QWERTY base
        return buildQwertyLayout()
    }

    private fun buildFlickLayout(): Map<Char, KeyPosition> {
        // Japanese flick input - 12 key layout
        val keys = mapOf(
            '1' to KeyPosition(0.1, 0.1), '2' to KeyPosition(0.3, 0.1), '3' to KeyPosition(0.5, 0.1),
            '4' to KeyPosition(0.7, 0.1), '5' to KeyPosition(0.9, 0.1),
            '6' to KeyPosition(0.1, 0.3), '7' to KeyPosition(0.3, 0.3), '8' to KeyPosition(0.5, 0.3),
            '9' to KeyPosition(0.7, 0.3), '0' to KeyPosition(0.9, 0.3),
            '*' to KeyPosition(0.1, 0.5), '#' to KeyPosition(0.9, 0.5)
        )
        return keys + buildQwertyLayout()
    }

    private fun buildGodanLayout(): Map<Char, KeyPosition> {
        // Japanese Godan layout - similar to QWERTY
        return buildQwertyLayout()
    }

    private fun buildHangul2SetLayout(): Map<Char, KeyPosition> {
        // Korean 2-set layout
        return buildQwertyLayout()
    }

    private fun buildHangul3SetLayout(): Map<Char, KeyPosition> {
        // Korean 3-set layout
        return buildQwertyLayout()
    }

    private fun buildDevanagariLayout(): Map<Char, KeyPosition> {
        // Devanagari/InScript layout
        return buildQwertyLayout()
    }

    private fun buildLayout(
        rows: List<String>,
        keyWidth: Double,
        keyHeight: Double,
        rowOffset: Double
    ): Map<Char, KeyPosition> {
        val layout = mutableMapOf<Char, KeyPosition>()
        
        rows.forEachIndexed { rowIdx, row ->
            val y = rowOffset + (rowIdx + 0.5) * keyHeight
            row.forEachIndexed { colIdx, ch ->
                val x = (colIdx + 0.5) * keyWidth
                layout[ch] = KeyPosition(x, y)
                layout[ch.uppercaseChar()] = KeyPosition(x, y)
            }
        }
        
        // Add space bar
        layout[' '] = KeyPosition(0.5, 0.9)
        
        return layout
    }
}

/**
 * Key position on keyboard (normalized 0-1 coordinates).
 */
data class KeyPosition(val x: Double, val y: Double)

/**
 * Result of blindfold scoring.
 */
data class BlindfoldResult(
    val rmsError: Double,
    val accuracy: Double,
    val correctChars: Int,
    val totalChars: Int,
    val substitutions: Int,
    val insertions: Int,
    val deletions: Int,
    val keyDistances: List<Double>,
    val lengthFactor: Double
) {
    /**
     * Calculates final score (0-100, higher is better).
     */
    fun calculateScore(timeMs: Long): Int {
        // Base score from accuracy and RMS error
        val accuracyScore = accuracy * 100
        val errorPenalty = (rmsError * 20).coerceAtMost(50) // max 50 point penalty
        val lengthBonus = (1.0 - lengthFactor) * 20 // bonus for longer texts
        
        val baseScore = (accuracyScore - errorPenalty + lengthBonus).coerceIn(0.0, 100.0)
        
        // Time factor (optional - faster is slightly better)
        val timeFactor = if (timeMs > 0) {
            val wpm = (totalChars / 5.0) / (timeMs / 60000.0)
            when {
                wpm > 40 -> 1.05
                wpm > 25 -> 1.0
                else -> 0.95
            }
        } else 1.0
        
        return (baseScore * timeFactor).toInt().coerceIn(0, 100)
    }
}