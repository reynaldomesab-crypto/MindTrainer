import { SchulteGenerator, SchulteTable } from '../schulte/SchulteGenerator';

describe('SchulteGenerator', () => {
  describe('generate', () => {
    it('creates valid 5x5 table', () => {
      const table: SchulteTable = SchulteGenerator.generate(12345, 5);
      
      expect(table.totalNumbers).toBe(25);
      expect(table.size).toBe(5);
      expect(table.timeLimitMs).toBe(30000);
      expect(table.numbers.length).toBe(25);
      expect(table.grid.length).toBe(5);
      expect(table.grid[0].length).toBe(5);
    });

    it('creates valid 4x4 table', () => {
      const table = SchulteGenerator.generate(12345, 4);
      
      expect(table.totalNumbers).toBe(16);
      expect(table.size).toBe(4);
      expect(table.timeLimitMs).toBe(60000);
    });

    it('creates valid 7x7 table', () => {
      const table = SchulteGenerator.generate(12345, 7);
      
      expect(table.totalNumbers).toBe(49);
      expect(table.size).toBe(7);
      expect(table.timeLimitMs).toBe(20000);
    });

    it('produces all numbers 1 to N exactly once', () => {
      const table = SchulteGenerator.generate(42, 5);
      const sorted = [...table.numbers].sort((a, b) => a - b);
      
      expect(sorted).toEqual(Array.from({ length: 25 }, (_, i) => i + 1));
    });

    it('same seed produces same table', () => {
      const table1 = SchulteGenerator.generate(999, 5);
      const table2 = SchulteGenerator.generate(999, 5);
      
      expect(table1.numbers).toEqual(table2.numbers);
      expect(table1.grid.flat()).toEqual(table2.grid.flat());
    });

    it('different seeds produce different tables', () => {
      const table1 = SchulteGenerator.generate(111, 5);
      const table2 = SchulteGenerator.generate(222, 5);
      
      expect(table1.numbers).not.toEqual(table2.numbers);
    });

    it('throws for invalid size', () => {
      expect(() => SchulteGenerator.generate(1, 3)).toThrow('Size must be between 4 and 7');
      expect(() => SchulteGenerator.generate(1, 8)).toThrow('Size must be between 4 and 7');
    });
  });

  describe('validateSequence', () => {
    it('detects perfect sequence', () => {
      const table = SchulteGenerator.generate(100, 4);
      const perfectSequence = [...table.numbers];
      
      const result = SchulteGenerator.validateSequence(table.numbers, perfectSequence);
      
      expect(result.accuracy).toBe(1.0);
      expect(result.correct).toBe(16);
    });

    it('detects errors', () => {
      const table = SchulteGenerator.generate(100, 4);
      const wrongSequence = [...table.numbers].reverse();
      
      const result = SchulteGenerator.validateSequence(table.numbers, wrongSequence);
      
      expect(result.accuracy).toBeLessThan(1.0);
      expect(result.correct).toBeLessThan(16);
    });

    it('handles partial correct sequence', () => {
      const table = SchulteGenerator.generate(100, 4);
      const partialSequence = table.numbers.slice(0, 8);
      
      const result = SchulteGenerator.validateSequence(table.numbers, partialSequence);
      
      expect(result.accuracy).toBe(0.5);
      expect(result.correct).toBe(8);
    });
  });

  describe('calculateScore', () => {
    it('rewards speed and accuracy', () => {
      const scoreFastAccurate = SchulteGenerator.calculateScore(10000, 1.0, 5, 30000);
      const scoreSlowAccurate = SchulteGenerator.calculateScore(25000, 1.0, 5, 30000);
      const scoreFastInaccurate = SchulteGenerator.calculateScore(10000, 0.5, 5, 30000);
      
      expect(scoreFastAccurate).toBeGreaterThan(scoreSlowAccurate);
      expect(scoreFastAccurate).toBeGreaterThan(scoreFastInaccurate);
      expect(scoreSlowAccurate).toBeGreaterThan(scoreFastInaccurate);
    });

    it('scales with grid size', () => {
      const score4x4 = SchulteGenerator.calculateScore(15000, 1.0, 4, 60000);
      const score5x5 = SchulteGenerator.calculateScore(15000, 1.0, 5, 30000);
      const score7x7 = SchulteGenerator.calculateScore(15000, 1.0, 7, 20000);
      
      expect(score7x7).toBeGreaterThan(score5x5);
      expect(score5x5).toBeGreaterThan(score4x4);
    });

    it('clamps score to non-negative', () => {
      const score = SchulteGenerator.calculateScore(60000, 0.0, 5, 30000);
      expect(score).toBeGreaterThanOrEqual(0);
    });
  });

  describe('generatePackage', () => {
    it('creates correct number of tables', () => {
      const pkg1 = SchulteGenerator.generatePackage(10, 5, 5000);
      const pkg2 = SchulteGenerator.generatePackage(100, 5, 5000);
      
      expect(pkg1.length).toBe(10);
      expect(pkg2.length).toBe(100);
    });

    it('each table in package is unique', () => {
      const pkg = SchulteGenerator.generatePackage(10, 5, 5000);
      const seeds = pkg.map(t => t.seed);
      const uniqueSeeds = new Set(seeds);
      
      expect(uniqueSeeds.size).toBe(10);
    });
  });

  describe('getDailySeed', () => {
    it('is deterministic', () => {
      const date = new Date('2024-01-15');
      const seed1 = SchulteGenerator.getDailySeed(date);
      const seed2 = SchulteGenerator.getDailySeed(date);
      
      expect(seed1).toBe(seed2);
    });
  });
});