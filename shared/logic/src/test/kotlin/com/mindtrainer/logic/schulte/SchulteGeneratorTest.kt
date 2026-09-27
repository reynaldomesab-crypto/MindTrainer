package com.mindtrainer.logic.schulte

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SchulteGeneratorTest {

    @Test
    fun `generate creates valid 5x5 table`() {
        val table = SchulteGenerator.generate(12345L, 5)
        
        assertEquals(25, table.totalNumbers)
        assertEquals(5, table.size)
        assertEquals(30000, table.timeLimitMs)
        assertEquals(25, table.numbers.size)
        assertEquals(5, table.grid.size)
        assertEquals(5, table.grid[0].size)
    }

    @Test
    fun `generate creates valid 4x4 table`() {
        val table = SchulteGenerator.generate(12345L, 4)
        
        assertEquals(16, table.totalNumbers)
        assertEquals(4, table.size)
        assertEquals(60000, table.timeLimitMs)
    }

    @Test
    fun `generate creates valid 7x7 table`() {
        val table = SchulteGenerator.generate(12345L, 7)
        
        assertEquals(49, table.totalNumbers)
        assertEquals(7, table.size)
        assertEquals(20000, table.timeLimitMs)
    }

    @Test
    fun `generate produces all numbers 1 to N exactly once`() {
        val table = SchulteGenerator.generate(42L, 5)
        val sorted = table.numbers.sorted()
        
        assertEquals((1..25).toList(), sorted)
    }

    @Test
    fun `same seed produces same table`() {
        val table1 = SchulteGenerator.generate(999L, 5)
        val table2 = SchulteGenerator.generate(999L, 5)
        
        assertArrayEquals(table1.numbers, table2.numbers)
        assertArrayEquals(table1.grid.flattenToArray(), table2.grid.flattenToArray())
    }

    @Test
    fun `different seeds produce different tables`() {
        val table1 = SchulteGenerator.generate(111L, 5)
        val table2 = SchulteGenerator.generate(222L, 5)
        
        assertNotEquals(table1.numbers.contentToString(), table2.numbers.contentToString())
    }

    @Test
    fun `validateSequence detects perfect sequence`() {
        val table = SchulteGenerator.generate(100L, 4)
        val perfectSequence = table.numbers.toList()
        
        val result = SchulteGenerator.validateSequence(table.numbers, perfectSequence)
        
        assertEquals(1.0, result.accuracy)
        assertEquals(16, result.correct)
    }

    @Test
    fun `validateSequence detects errors`() {
        val table = SchulteGenerator.generate(100L, 4)
        val wrongSequence = table.numbers.reversed().toList()
        
        val result = SchulteGenerator.validateSequence(table.numbers, wrongSequence)
        
        assertEquals(0.0625, result.accuracy) // Only 1/16 correct by chance
        assertEquals(1, result.correct)
    }

    @Test
    fun `calculateScore rewards speed and accuracy`() {
        val scoreFastAccurate = SchulteGenerator.calculateScore(10000, 1.0, 5, 30000)
        val scoreSlowAccurate = SchulteGenerator.calculateScore(25000, 1.0, 5, 30000)
        val scoreFastInaccurate = SchulteGenerator.calculateScore(10000, 0.5, 5, 30000)
        
        assertTrue(scoreFastAccurate > scoreSlowAccurate)
        assertTrue(scoreFastAccurate > scoreFastInaccurate)
        assertTrue(scoreSlowAccurate > scoreFastInaccurate)
    }

    @Test
    fun `calculateScore scales with grid size`() {
        val score4x4 = SchulteGenerator.calculateScore(15000, 1.0, 4, 60000)
        val score5x5 = SchulteGenerator.calculateScore(15000, 1.0, 5, 30000)
        val score7x7 = SchulteGenerator.calculateScore(15000, 1.0, 7, 20000)
        
        assertTrue(score7x7 > score5x5)
        assertTrue(score5x5 > score4x4)
    }

    @Test
    fun `generatePackage creates correct number of tables`() {
        val package1 = SchulteGenerator.generatePackage(10, 5, 5000L)
        val package2 = SchulteGenerator.generatePackage(100, 5, 5000L)
        
        assertEquals(10, package1.size)
        assertEquals(100, package2.size)
    }

    @Test
    fun `getDailySeed is deterministic`() {
        val date = java.time.LocalDate.of(2024, 1, 15)
        val seed1 = SchulteGenerator.getDailySeed(date)
        val seed2 = SchulteGenerator.getDailySeed(date)
        
        assertEquals(seed1, seed2)
    }

    @Test
    fun `generate throws for invalid size`() {
        assertThrows<IllegalArgumentException> { SchulteGenerator.generate(1L, 3) }
        assertThrows<IllegalArgumentException> { SchulteGenerator.generate(1L, 8) }
    }
}