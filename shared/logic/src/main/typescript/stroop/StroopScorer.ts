/**
 * Stroop Challenge Scorer (TypeScript)
 * Calculates interference effects, switch costs, and composite indices.
 */

import type {
  StroopMode,
  StroopStimulus,
  StroopConfig,
  StroopCondition,
} from './StroopGenerator';

export interface StroopTrial {
  stimulus: StroopStimulus;
  response: string;
  rtMs: number;
  correct: boolean;
  isSwitchTrial?: boolean;
  condition: StroopCondition;
}

export interface StroopResult {
  mode: StroopMode;
  totalTrials: number;
  meanRTCongruent: number;
  meanRTIncongruent: number;
  meanRTNeutral: number;
  interferenceEffect: number;
  interferenceRatio: number;
  accuracyCongruent: number;
  accuracyIncongruent: number;
  accuracyNeutral: number;
  switchCost: number;
  throughput: number;
  inhibitionIndex: number;
  flexibilityIndex: number;
  consistency: number;
  compositeScore: number;
  currentLevel: number;
  nextLevel: number;
  summary: () => string;
}

export class StroopScorer {
  static scoreSession(
    mode: StroopMode,
    trials: StroopTrial[],
    config: StroopConfig
  ): StroopResult {
    // Filter valid trials (with responses)
    const validTrials = trials.filter(t => t.response && t.response.trim().length > 0);

    // Separate by condition
    const congruentTrials = validTrials.filter(t => t.condition === 'CONGRUENT');
    const incongruentTrials = validTrials.filter(t => t.condition === 'INCONGRUENT');
    const neutralTrials = validTrials.filter(t => t.condition === 'NEUTRAL');

    // Calculate mean RTs and accuracies
    const meanRTCongruent = this.meanRT(congruentTrials);
    const meanRTIncongruent = this.meanRT(incongruentTrials);
    const meanRTNeutral = this.meanRT(neutralTrials);

    const accCongruent = this.accuracy(congruentTrials);
    const accIncongruent = this.accuracy(incongruentTrials);
    const accNeutral = this.accuracy(neutralTrials);

    // Interference effect (classic Stroop effect)
    const interferenceEffect = meanRTIncongruent - meanRTCongruent;
    const interferenceRatio = meanRTCongruent > 0 ? meanRTIncongruent / meanRTCongruent : 1.0;

    // Mode-specific metrics
    const switchCost = mode === 'SWITCH' ? this.calculateSwitchCost(validTrials) : 0;
    const throughput = mode === 'SPEED' ? this.calculateThroughput(validTrials, config) : 0;

    // Inhibition Index: 1 - (interference / baseline)
    const baselineRT = Math.max(meanRTCongruent, meanRTNeutral, 1);
    const inhibitionIndex = Math.max(0, 1 - interferenceEffect / baselineRT);

    // Flexibility Index: 1 - (switch cost / mean RT)
    const flexibilityIndex = mode === 'SWITCH'
      ? Math.max(0, 1 - switchCost / Math.max(meanRTIncongruent, 1))
      : 1;

    // Consistency: inverse of coefficient of variation
    const allRTs = validTrials.map(t => t.rtMs);
    const consistency = allRTs.length > 1 ? this.calculateConsistency(allRTs) : 0.5;

    // Composite score
    const compositeScore = this.calculateCompositeScore(
      inhibitionIndex,
      flexibilityIndex,
      throughput,
      accIncongruent,
      consistency,
      mode
    );

    // Difficulty level calculation for adaptive progression
    const nextLevel = this.calculateNextLevel(compositeScore, config.difficultyLevel, accIncongruent);

    const result: StroopResult = {
      mode,
      totalTrials: validTrials.length,
      meanRTCongruent,
      meanRTIncongruent,
      meanRTNeutral,
      interferenceEffect,
      interferenceRatio,
      accuracyCongruent: accCongruent,
      accuracyIncongruent: accIncongruent,
      accuracyNeutral: accNeutral,
      switchCost,
      throughput,
      inhibitionIndex,
      flexibilityIndex,
      consistency,
      compositeScore,
      currentLevel: config.difficultyLevel,
      nextLevel,
      summary: () => result.summary(),
    };

    return result;
  }

  private static meanRT(trials: StroopTrial[]): number {
    if (trials.length === 0) return 0;
    return trials.reduce((sum, t) => sum + t.rtMs, 0) / trials.length;
  }

  private static accuracy(trials: StroopTrial[]): number {
    if (trials.length === 0) return 0;
    return trials.filter(t => t.correct).length / trials.length;
  }

  private static calculateSwitchCost(trials: StroopTrial[]): number {
    const switchTrials = trials.filter(t => t.isSwitchTrial);
    const repeatTrials = trials.filter(t => !t.isSwitchTrial);

    const meanSwitchRT = this.meanRT(switchTrials);
    const meanRepeatRT = this.meanRT(repeatTrials);

    return Math.max(0, meanSwitchRT - meanRepeatRT);
  }

  private static calculateThroughput(trials: StroopTrial[], config: StroopConfig): number {
    const correctCount = trials.filter(t => t.correct).length;
    const totalTimeSec = trials.reduce((sum, t) => sum + t.rtMs, 0) / 1000;
    return totalTimeSec > 0 ? correctCount / totalTimeSec : 0;
  }

  private static calculateConsistency(values: number[]): number {
    if (values.length < 2) return 0.5;
    const mean = values.reduce((a, b) => a + b, 0) / values.length;
    const stdDev = Math.sqrt(values.map(v => (v - mean) ** 2).reduce((a, b) => a + b, 0) / values.length);
    return mean > 0 ? (1 - (stdDev / mean)).clamp(0, 1) : 0.5;
  }

  private static calculateCompositeScore(
    inhibitionIndex: number,
    flexibilityIndex: number,
    throughput: number,
    accuracy: number,
    consistency: number,
    mode: StroopMode
  ): number {
    const weights: Record<StroopMode, [number, number, number]> = {
      CLASSIC: [0.5, 0, 0],
      REVERSE: [0.5, 0, 0],
      SWITCH: [0.3, 0.3, 0],
      SPATIAL: [0.4, 0, 0],
      SPEED: [0.2, 0, 0.4],
      SEQUENCE: [0.4, 0.1, 0.1],
    };

    const [wInhib, wFlex, wThrough] = weights[mode];
    const throughputNormalized = Math.min(1, throughput / 3); // normalize to ~3/sec max

    const score = (
      inhibitionIndex * wInhib * 100 +
      flexibilityIndex * wFlex * 100 +
      throughputNormalized * wThrough * 100 +
      accuracy * 0.2 * 100 +
      consistency * 0.1 * 100
    );

    return Math.round(score).clamp(0, 100);
  }

  private static calculateNextLevel(
    compositeScore: number,
    currentLevel: number,
    accuracy: number
  ): number {
    if (compositeScore >= 85 && accuracy >= 0.9) return Math.min(1, currentLevel + 0.05);
    if (compositeScore >= 70 && accuracy >= 0.8) return Math.min(1, currentLevel + 0.03);
    if (compositeScore < 50 || accuracy < 0.6) return Math.max(0, currentLevel - 0.03);
    return currentLevel;
  }
}

// Helper: Number clamping
declare global {
  interface Number {
    clamp(min: number, max: number): number;
  }
}

Number.prototype.clamp = function(min: number, max: number): number {
  return Math.max(min, Math.min(max, this.valueOf()));
};