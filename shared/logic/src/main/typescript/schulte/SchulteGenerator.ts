/**
 * Procedural Schulte Table Generator (TypeScript)
 * Generates deterministic tables from seeds for consistent daily challenges
 * and downloadable packages.
 */

export interface SchulteTable {
  seed: number;
  size: number;
  numbers: number[];
  timeLimitMs: number;
  grid: number[][];
  totalNumbers: number;
}

/**
 * Seeded random number generator for deterministic generation
 */
class SeededRandom {
  private seed: number;

  constructor(seed: number) {
    this.seed = seed;
  }

  nextInt(max: number): number {
    // Linear congruential generator (same as Java's Random)
    this.seed = (this.seed * 25214903917 + 11) & 0xffffffffffff;
    return (this.seed >>> 16) % max;
  }

  nextDouble(): number {
    this.seed = (this.seed * 25214903917 + 11) & 0xffffffffffff;
    return (this.seed >>> 16) / 0x100000000;
  }
}

export class SchulteGenerator {
  /**
   * Generates a Schulte table from a seed.
   * @param seed Deterministic seed for reproducible generation
   * @param size Grid size (4, 5, 6, or 7)
   * @return SchulteTable object with shuffled numbers
   */
  static generate(seed: number, size: number = 5): SchulteTable {
    if (size < 4 || size > 7) {
      throw new Error(`Size must be between 4 and 7, got ${size}`);
    }

    const random = new SeededRandom(seed);
    const total = size * size;
    const numbers = Array.from({ length: total }, (_, i) => i + 1);

    // Fisher-Yates shuffle for uniform distribution
    for (let i = total - 1; i > 0; i--) {
      const j = random.nextInt(i + 1);
      [numbers[i], numbers[j]] = [numbers[j], numbers[i]];
    }

    // Create 2D grid
    const grid: number[][] = [];
    for (let row = 0; row < size; row++) {
      grid[row] = numbers.slice(row * size, (row + 1) * size);
    }

    const timeLimitMs = [60000, 30000, 30000, 20000][size - 4] ?? 30000;

    return {
      seed,
      size,
      numbers,
      timeLimitMs,
      grid,
      totalNumbers: total,
    };
  }

  /**
   * Validates a tapped sequence against the correct order.
   * @param numbers The generated table numbers (flat array)
   * @param tappedSequence The sequence of numbers tapped by user
   * @return Object with accuracy and correct count
   */
  static validateSequence(numbers: number[], tappedSequence: number[]): { accuracy: number; correct: number } {
    let correct = 0;
    let expectedIndex = 0;

    for (const tapped of tappedSequence) {
      if (expectedIndex < numbers.length && tapped === numbers[expectedIndex]) {
        correct++;
        expectedIndex++;
      }
    }

    const accuracy = numbers.length > 0 ? correct / numbers.length : 0;
    return { accuracy, correct };
  }

  /**
   * Calculates score based on time and accuracy.
   * Higher score = better performance.
   */
  static calculateScore(
    completionTimeMs: number,
    accuracy: number,
    size: number,
    timeLimitMs: number
  ): number {
    const maxTime = timeLimitMs;
    const timeRatio = Math.max(0, Math.min(1, 1 - completionTimeMs / maxTime));
    
    const sizeMultiplier: Record<number, number> = {
      4: 0.8,
      5: 1.0,
      6: 1.2,
      7: 1.5,
    };

    const baseScore = Math.round(
      1000 * accuracy * (0.5 + 0.5 * timeRatio) * (sizeMultiplier[size] ?? 1.0)
    );

    return Math.max(0, baseScore);
  }

  /**
   * Generates multiple tables for a downloadable package.
   */
  static generatePackage(count: number, size: number = 5, baseSeed: number = Date.now()): SchulteTable[] {
    return Array.from({ length: count }, (_, index) => {
      const seed = baseSeed + index;
      return this.generate(seed, size);
    });
  }

  /**
   * Gets the daily table seed based on date (same for all users globally).
   */
  static getDailySeed(date: Date = new Date()): number {
    // Deterministic seed from date for global daily challenge
    const dayCount = Math.floor(date.getTime() / (1000 * 60 * 60 * 24));
    return 1_000_000 + dayCount;
  }
}