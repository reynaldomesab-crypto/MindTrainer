package com.mindtrainer.logic.schulte

import kotlin.random.Random

/**
 * Procedural Schulte Table Generator
 * Generates deterministic tables from seeds for consistent daily challenges
 * and downloadable packages.
 */
object SchulteGenerator {

    /**
     * Generates a Schulte table from a seed.
     * @param seed Deterministic seed for reproducible generation
     * @param size Grid size (4, 5, 6, or 7)
     * @return Shuffled array of numbers 1 to size*size
     */
    fun generate(seed: Long, size: Int = 5): IntArray {
        require(size in 4..7) { "Size must be between 4 and 7, got $size" }
        
        val random = Random(seed)
        val total = size * size
        val numbers = IntArray(total) { it + 1 }
        
        // Fisher-Yates shuffle for uniform distribution
        for (i in total - 1 downTo 1) {
            val j = random.nextInt(i + 1)
            val temp = numbers[i]
            numbers[i] = numbers[j]
            numbers[j] = temp
        }
        
        return numbers
    }

    /**
     * Generates a 2D grid representation for rendering.
     */
    fun generateGrid(seed: Long, size: Int = 5): Array<IntArray> {
        val flat = generate(seed, size)
        val grid = Array(size) { IntArray(size) }
        for (i in flat.indices) {
            grid[i / size][i % size] = flat[i]
        }
        return grid
    }

    /**
     * Validates a tapped sequence against the correct order.
     * @param numbers The generated table numbers
     * @param tappedSequence The sequence of numbers tapped by user
     * @return Pair of (accuracy, correctCount)
     */
    fun validateSequence(numbers: IntArray, tappedSequence: List<Int>): Pair<Double, Int> {
        var correct = 0
        var expectedIndex = 0
        
        for (tapped in tappedSequence) {
            if (expectedIndex < numbers.size && tapped == numbers[expectedIndex]) {
                correct++
                expectedIndex++
            }
        }
        
        val accuracy = if (numbers.isEmpty()) 0.0 else correct.toDouble() / numbers.size
        return accuracy to correct
    }

    /**
     * Calculates score based on time and accuracy.
     * Higher score = better performance.
     */
    fun calculateScore(
        completionTimeMs: Long,
        accuracy: Double,
        size: Int,
        timeLimitMs: Long
    ): Int {
        val maxTime = timeLimitMs.toDouble()
        val timeRatio = 1.0 - (completionTimeMs.toDouble() / maxTime).coerceIn(0.0, 1.0)
        val sizeMultiplier = when (size) {
            4 -> 0.8
            5 -> 1.0
            6 -> 1.2
            7 -> 1.5
            else -> 1.0
        }
        
        val baseScore = (1000 * accuracy * (0.5 + 0.5 * timeRatio) * sizeMultiplier).toInt()
        return baseScore.coerceAtLeast(0)
    }

    /**
     * Generates multiple tables for a downloadable package.
     */
    fun generatePackage(count: Int, size: Int = 5, baseSeed: Long = System.currentTimeMillis()): List<SchulteTable> {
        return (0 until count).map { index ->
            val seed = baseSeed + index
            SchulteTable(
                seed = seed,
                size = size,
                numbers = generate(seed, size),
                timeLimitMs = when (size) {
                    4 -> 60_000
                    5 -> 30_000
                    6 -> 30_000
                    7 -> 20_000
                    else -> 30_000
                }
            )
        }
    }

    /**
     * Gets the daily table seed based on date (same for all users globally).
     */
    fun getDailySeed(date: java.time.LocalDate = java.time.LocalDate.now()): Long {
        // Deterministic seed from date for global daily challenge
        val dayCount = date.toEpochDay()
        return 1_000_000 + dayCount
    }
}

/**
 * Data class representing a Schulte table configuration.
 */
data class SchulteTable(
    val seed: Long,
    val size: Int,
    val numbers: IntArray,
    val timeLimitMs: Long
) {
    val grid: Array<IntArray>
        get() = SchulteGenerator.generateGrid(seed, size)
    
    val totalNumbers: Int
        get() = size * size
}