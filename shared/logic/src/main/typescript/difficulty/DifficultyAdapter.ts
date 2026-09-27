/**
 * Adaptive Difficulty Engine (TypeScript)
 * Adjusts exercise difficulty based on user performance with plateau detection.
 */

export type Preference = 'CHALLENGE' | 'MAINTAIN' | 'RELAX';

export interface DifficultyState {
  exerciseType: string;
  currentLevel: number;
  recentScores: number[];
  recentAccuracies: number[];
  recentRTs: number[];
  sessionsSinceChange: number;
  plateauDetected: boolean;
  totalSessions: number;
  preference: Preference;
}

export interface ExerciseParams {
  [key: string]: any;
}

export class DifficultyAdapter {
  private static readonly WINDOW_SIZE = 20;

  static updateDifficulty(
    state: DifficultyState,
    score: number,
    accuracy: number,
    rtMs: number,
    preference: Preference = 'MAINTAIN'
  ): { newLevel: number; changed: boolean } {
    state.preference = preference;
    state.totalSessions++;
    state.recentScores.push(score);
    state.recentAccuracies.push(accuracy);
    state.recentRTs.push(rtMs);

    // Keep rolling window
    if (state.recentScores.length > this.WINDOW_SIZE) {
      state.recentScores.shift();
      state.recentAccuracies.shift();
      state.recentRTs.shift();
    }

    // Calculate performance trend
    const trend = this.calculateTrend(state);
    const isImproving = trend > 0.02;
    const isDeclining = trend < -0.02;

    // Plateau detection: no improvement for 14+ sessions
    if (isImproving) {
      state.sessionsSinceChange = 0;
      state.plateauDetected = false;
    } else {
      state.sessionsSinceChange++;
      if (state.sessionsSinceChange >= 14 && 
          state.recentScores.reduce((a, b) => a + b, 0) / state.recentScores.length > 70) {
        state.plateauDetected = true;
      }
    }

    // Determine level adjustment
    let adjustment = 0;
    if (isImproving) {
      adjustment = this.calculateIncrease(preference, state.currentLevel, accuracy);
    } else if (isDeclining) {
      adjustment = this.calculateDecrease(preference, state.currentLevel, accuracy);
    } else if (state.plateauDetected) {
      adjustment = this.calculatePlateauAdjustment(preference, state.currentLevel);
    }

    const newLevel = Math.max(0, Math.min(1, state.currentLevel + adjustment));
    const changed = Math.abs(newLevel - state.currentLevel) > 0.001;
    state.currentLevel = newLevel;

    if (changed) state.sessionsSinceChange = 0;

    return { newLevel, changed };
  }

  private static calculateTrend(state: DifficultyState): number {
    const scores = state.recentScores;
    if (scores.length < 3) return 0;

    // Linear regression slope over recent sessions
    const n = scores.length;
    const sumX = (n - 1) * n / 2;
    const sumY = scores.reduce((a, b) => a + b, 0);
    let sumXY = 0;
    let sumX2 = 0;

    for (let i = 0; i < n; i++) {
      sumXY += i * scores[i];
      sumX2 += i * i;
    }

    const slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    return slope / 100; // Normalize to 0-1 scale
  }

  private static calculateIncrease(preference: Preference, currentLevel: number, accuracy: number): number {
    const baseStep = { CHALLENGE: 0.06, MAINTAIN: 0.04, RELAX: 0.02 }[preference];
    const levelFactor = 1 - currentLevel * 0.5;
    const accuracyFactor = accuracy > 0.95 ? 1.5 : accuracy > 0.9 ? 1.2 : 1.0;
    return baseStep * levelFactor * accuracyFactor;
  }

  private static calculateDecrease(preference: Preference, currentLevel: number, accuracy: number): number {
    const baseStep = { CHALLENGE: 0.04, MAINTAIN: 0.03, RELAX: 0.02 }[preference];
    const accuracyFactor = accuracy < 0.5 ? 1.5 : accuracy < 0.7 ? 1.2 : 1.0;
    return -baseStep * accuracyFactor;
  }

  private static calculatePlateauAdjustment(preference: Preference, currentLevel: number): number {
    return { CHALLENGE: 0.02, MAINTAIN: 0.01, RELAX: 0 }[preference];
  }

  static getExerciseParams(exerciseType: string, level: number): ExerciseParams {
    switch (exerciseType) {
      case 'SCHULTE':
        return this.getSchulteParams(level);
      case 'BLINDFOLD':
        return this.getBlindfoldParams(level);
      case 'NON_DOMINANT':
        return this.getNonDominantParams(level);
      case 'STROOP':
        return this.getStroopParams(level);
      default:
        return {};
    }
  }

  private static getSchulteParams(level: number): ExerciseParams {
    let size: number;
    let timeLimit: number;

    if (level < 0.2) { size = 4; timeLimit = 60000; }
    else if (level < 0.4) { size = 5; timeLimit = 45000; }
    else if (level < 0.6) { size = 5; timeLimit = 30000; }
    else if (level < 0.8) { size = 6; timeLimit = 30000; }
    else { size = 7; timeLimit = 20000; }

    return { size, timeLimitMs: timeLimit, showNumbers: true };
  }

  private static getBlindfoldParams(level: number): ExerciseParams {
    let tier: number;
    let charRange: [number, number];
    let maxRmsError: number;

    if (level < 0.33) { tier = 1; charRange = [200, 250]; maxRmsError = 1.5; }
    else if (level < 0.66) { tier = 2; charRange = [250, 300]; maxRmsError = 1.2; }
    else { tier = 3; charRange = [300, 350]; maxRmsError = 1.0; }

    return { tier, charRange, maxRmsError };
  }

  private static getNonDominantParams(level: number): ExerciseParams {
    let tasks: string[];
    let copyTier: number;
    let traceComplexity: number;
    let tapGridSize: number;

    if (level < 0.25) { tasks = ['COPYWRITING']; }
    else if (level < 0.5) { tasks = ['COPYWRITING', 'TRACING']; }
    else { tasks = ['COPYWRITING', 'TRACING', 'TAPPING']; }

    if (level < 0.33) { copyTier = 1; }
    else if (level < 0.66) { copyTier = 2; }
    else { copyTier = 3; }

    if (level < 0.3) { traceComplexity = 1; }
    else if (level < 0.6) { traceComplexity = 2; }
    else if (level < 0.8) { traceComplexity = 3; }
    else { traceComplexity = 4; }

    if (level < 0.4) { tapGridSize = 3; }
    else if (level < 0.7) { tapGridSize = 4; }
    else { tapGridSize = 5; }

    return { tasks, copyTier, traceComplexity, tapGridSize };
  }

  private static getStroopParams(level: number): ExerciseParams {
    let colorCount: number;
    let incongruentRatio: number;
    let stimulusDuration: number;
    let switchFrequency: number;
    let mode: string;

    if (level < 0.3) { colorCount = 4; incongruentRatio = 0.2; stimulusDuration = 1000; mode = 'CLASSIC'; }
    else if (level < 0.6) { colorCount = 5; incongruentRatio = 0.4; stimulusDuration = 800; mode = 'SWITCH'; }
    else if (level < 0.7) { colorCount = 5; incongruentRatio = 0.5; stimulusDuration = 600; mode = 'SWITCH'; }
    else if (level < 0.8) { colorCount = 6; incongruentRatio = 0.6; stimulusDuration = 600; mode = 'SPATIAL'; }
    else { colorCount = 6; incongruentRatio = 0.8; stimulusDuration = 400; mode = 'SPEED'; }

    if (level < 0.4) { switchFrequency = 0.2; }
    else if (level < 0.7) { switchFrequency = 0.3; }
    else { switchFrequency = 0.4; }

    return { colorCount, incongruentRatio, stimulusDurationMs: stimulusDuration, switchFrequency, mode };
  }

  static createInitialState(exerciseType: string, preference: 'CHALLENGE' | 'MAINTAIN' | 'RELAX' = 'MAINTAIN'): DifficultyState {
    const initialLevel = { CHALLENGE: 0.3, MAINTAIN: 0.1, RELAX: 0 }[preference];
    return {
      exerciseType,
      currentLevel: initialLevel,
      recentScores: [],
      recentAccuracies: [],
      recentRTs: [],
      sessionsSinceChange: 0,
      plateauDetected: false,
      totalSessions: 0,
      preference,
    };
  }

  static serialize(state: DifficultyState): string {
    return JSON.stringify(state);
  }

  static deserialize(json: string): DifficultyState {
    return JSON.parse(json);
  }
}