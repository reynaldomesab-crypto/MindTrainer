/**
 * Non-Dominant Hand Metrics Calculator (TypeScript)
 * Evaluates motor control performance across three task types:
 * copywriting, shape tracing, and tap sequences.
 */

import { BlindfoldScorer, BlindfoldResult } from '../blindfold/BlindfoldScorer';

export interface Point {
  x: number;
  y: number;
}

export interface CopywritingResult {
  accuracy: number;
  rmsError: number;
  rhythmConsistency: number;
  hesitationIndex: number;
  fatigueIndex: number;
  score: number;
  errorBreakdown: BlindfoldResult;
}

export interface TracingResult {
  rmse: number;
  smoothness: number;
  timeRatio: number;
  cornerAccuracy: number;
  score: number;
  completionTimeMs: number;
}

export interface TappingResult {
  spatialAccuracy: number;
  sequenceAccuracy: number;
  rhythmConsistency: number;
  reactionTimeMs: number;
  avgIntervalMs: number;
  score: number;
}

export class NonDomMetrics {
  /**
   * Evaluates copywriting performance with non-dominant hand.
   * Uses similar keyboard geometry as blindfold but with different weighting.
   */
  static evaluateCopywriting(
    expected: string,
    actual: string,
    keyHoldDurations: number[], // ms per character
    interKeyIntervals: number[], // ms between keystrokes
    keyboardLayout: string = 'QWERTY'
  ): CopywritingResult {
    const blindfoldResult = BlindfoldScorer.score(expected, actual, keyboardLayout);

    // Rhythm consistency: coefficient of variation of inter-key intervals
    const rhythmConsistency = interKeyIntervals.length > 1
      ? this.calculateConsistency(interKeyIntervals)
      : 0.5;

    // Hesitation index: proportion of long holds (>500ms)
    const hesitationIndex = keyHoldDurations.length > 0
      ? keyHoldDurations.filter(d => d > 500).length / keyHoldDurations.length
      : 0;

    // Fatigue detection: increasing hold durations over time
    const fatigueIndex = this.calculateFatigue(keyHoldDurations);

    // Composite score
    const accuracyWeight = 0.5;
    const rhythmWeight = 0.2;
    const hesitationWeight = 0.15;
    const fatigueWeight = 0.15;

    const score = Math.round(
      (blindfoldResult.accuracy * accuracyWeight +
        rhythmConsistency * rhythmWeight +
        (1 - hesitationIndex) * hesitationWeight +
        (1 - Math.max(0, fatigueIndex)) * fatigueWeight) * 100
    ).clamp(0, 100);

    return {
      accuracy: blindfoldResult.accuracy,
      rmsError: blindfoldResult.rmsError,
      rhythmConsistency,
      hesitationIndex,
      fatigueIndex,
      score,
      errorBreakdown: blindfoldResult,
    };
  }

  /**
   * Evaluates shape tracing performance.
   * Measures deviation from ideal path, velocity smoothness, and corner handling.
   */
  static evaluateTracing(
    idealPath: Point[],
    actualPath: Point[],
    timestamps: number[], // ms since start
    targetTimeMs: number
  ): TracingResult {
    // Resample paths to same number of points for comparison
    const resampledIdeal = this.resamplePath(idealPath, 100);
    const resampledActual = this.resamplePath(actualPath, 100);

    // Path deviation (RMSE)
    let totalDeviation = 0;
    for (let i = 0; i < resampledIdeal.length; i++) {
      const ideal = resampledIdeal[i];
      const actual = resampledActual[i];
      totalDeviation += Math.hypot(ideal.x - actual.x, ideal.y - actual.y);
    }
    const rmse = totalDeviation / resampledIdeal.length;

    // Velocity smoothness (jerk minimization)
    const velocities = this.calculateVelocities(resampledActual, timestamps);
    const smoothness = this.calculateSmoothness(velocities);

    // Completion time vs target
    const actualTime = timestamps.length > 0 ? timestamps[timestamps.length - 1] : targetTimeMs;
    const timeRatio = Math.max(0.5, Math.min(2.0, actualTime / targetTimeMs));
    const timeScore = 1 / timeRatio; // faster is better, but not too fast

    // Corner cutting vs overshooting
    const cornerAccuracy = this.evaluateCorners(idealPath, actualPath);

    // Composite metrics
    const deviationScore = Math.max(0, 1 - rmse * 10); // normalize
    const score = Math.round(
      (deviationScore * 0.5 + smoothness * 0.2 + timeScore * 0.2 + cornerAccuracy * 0.1) * 100
    ).clamp(0, 100);

    return {
      rmse,
      smoothness,
      timeRatio,
      cornerAccuracy,
      score,
      completionTimeMs: actualTime,
    };
  }

  /**
   * Evaluates tap sequence performance.
   * Measures spatial accuracy, timing, and sequence order.
   */
  static evaluateTapping(
    targetGrid: Point[], // target positions (normalized 0-1)
    actualTaps: Point[],
    tapTimestamps: number[], // ms since start
    targetSequence: number[], // indices of targets in order
    actualSequence: number[], // user's tap order
    gridSize: number
  ): TappingResult {
    // Spatial accuracy: distance from target centers
    let totalDistance = 0;
    let correctTargets = 0;

    for (let i = 0; i < actualTaps.length; i++) {
      if (i < targetGrid.length) {
        const target = targetGrid[targetSequence[i]];
        const actual = actualTaps[i];
        const distance = Math.hypot(target.x - actual.x, target.y - actual.y);
        totalDistance += distance;

        // Consider correct if within 15% of grid cell size
        const cellSize = 1 / gridSize;
        if (distance < cellSize * 0.15) correctTargets++;
      }
    }

    const spatialAccuracy = actualTaps.length > 0
      ? 1 - (totalDistance / actualTaps.length) / (1 / gridSize)
      : 0;

    // Sequence accuracy
    const sequenceAccuracy = targetSequence.length > 0
      ? targetSequence.filter((_, i) => i < actualSequence.length && actualSequence[i] === targetSequence[i]).length / targetSequence.length
      : 0;

    // Timing consistency
    const intervals = tapTimestamps.length > 1
      ? tapTimestamps.slice(1).map((t, i) => t - tapTimestamps[i])
      : [];

    const rhythmConsistency = intervals.length > 1
      ? this.calculateConsistency(intervals)
      : 0.5;

    // Reaction time (first tap)
    const reactionTime = tapTimestamps.length > 0 ? tapTimestamps[0] : 0;

    // Composite score
    const score = Math.round(
      (spatialAccuracy * 0.4 + sequenceAccuracy * 0.4 + rhythmConsistency * 0.2) * 100
    ).clamp(0, 100);

    return {
      spatialAccuracy: Math.max(0, Math.min(1, spatialAccuracy)),
      sequenceAccuracy,
      rhythmConsistency,
      reactionTimeMs: reactionTime,
      avgIntervalMs: intervals.length > 0 ? intervals.reduce((a, b) => a + b, 0) / intervals.length : 0,
      score,
    };
  }

  // ============ Helper Functions ============

  private static resamplePath(path: Point[], targetCount: number): Point[] {
    if (path.length <= 1) {
      return Array(targetCount).fill(path[0] ?? { x: 0, y: 0 });
    }

    // Calculate cumulative distances
    const distances: number[] = [0];
    for (let i = 1; i < path.length; i++) {
      const d = Math.hypot(path[i].x - path[i - 1].x, path[i].y - path[i - 1].y);
      distances.push(distances[distances.length - 1] + d);
    }

    const totalLength = distances[distances.length - 1];
    if (totalLength === 0) return Array(targetCount).fill(path[0]);

    return Array.from({ length: targetCount }, (_, i) => {
      const targetDist = (i / (targetCount - 1)) * totalLength;
      
      // Find segment
      let seg = 0;
      while (seg < distances.length - 1 && distances[seg + 1] < targetDist) seg++;
      
      if (seg >= path.length - 1) return path[path.length - 1];
      
      const segStart = distances[seg];
      const segEnd = distances[seg + 1];
      const t = segEnd > segStart ? (targetDist - segStart) / (segEnd - segStart) : 0;
      
      return {
        x: path[seg].x + (path[seg + 1].x - path[seg].x) * t,
        y: path[seg].y + (path[seg + 1].y - path[seg].y) * t,
      };
    });
  }

  private static calculateVelocities(path: Point[], timestamps: number[]): number[] {
    if (path.length !== timestamps.length || path.length < 2) return [];

    return path.slice(1).map((pt, i) => {
      const dt = (timestamps[i + 1] - timestamps[i]) / 1000; // seconds
      if (dt <= 0) return 0;
      const dist = Math.hypot(pt.x - path[i].x, pt.y - path[i].y);
      return dist / dt; // units per second
    });
  }

  private static calculateSmoothness(velocities: number[]): number {
    if (velocities.length < 3) return 0.5;

    // Calculate jerk (rate of change of acceleration)
    let totalJerk = 0;
    for (let i = 2; i < velocities.length; i++) {
      const accel1 = velocities[i - 1] - velocities[i - 2];
      const accel2 = velocities[i] - velocities[i - 1];
      const jerk = Math.abs(accel2 - accel1);
      totalJerk += jerk;
    }

    const avgJerk = totalJerk / (velocities.length - 2);
    // Normalize: lower jerk = smoother
    return (1 / (1 + avgJerk * 10)).clamp(0, 1);
  }

  private static evaluateCorners(idealPath: Point[], actualPath: Point[]): number {
    if (idealPath.length < 3) return 1;

    let correctCorners = 0;
    let totalCorners = 0;

    for (let i = 1; i < idealPath.length - 1; i++) {
      const v1 = { x: idealPath[i].x - idealPath[i - 1].x, y: idealPath[i].y - idealPath[i - 1].y };
      const v2 = { x: idealPath[i + 1].x - idealPath[i].x, y: idealPath[i + 1].y - idealPath[i].y };

      const angle = Math.atan2(v1.x * v2.y - v1.y * v2.x, v1.x * v2.x + v1.y * v2.y);

      // Sharp turn threshold
      if (Math.abs(angle) > 1.0) { // ~57 degrees
        totalCorners++;
        
        // Check if actual path has similar turn nearby
        const actualIdx = Math.round(i * actualPath.length / idealPath.length).clamp(1, actualPath.length - 2);
        const av1 = { x: actualPath[actualIdx].x - actualPath[actualIdx - 1].x, y: actualPath[actualIdx].y - actualPath[actualIdx - 1].y };
        const av2 = { x: actualPath[actualIdx + 1].x - actualPath[actualIdx].x, y: actualPath[actualIdx + 1].y - actualPath[actualIdx].y };
        const aAngle = Math.atan2(av1.x * av2.y - av1.y * av2.x, av1.x * av2.x + av1.y * av2.y);

        if (Math.abs(aAngle) > 0.5 && (angle > 0) === (aAngle > 0)) {
          correctCorners++;
        }
      }
    }

    return totalCorners > 0 ? correctCorners / totalCorners : 1;
  }

  private static calculateConsistency(values: number[]): number {
    if (values.length < 2) return 0.5;
    const mean = values.reduce((a, b) => a + b, 0) / values.length;
    const stdDev = Math.sqrt(values.map(v => (v - mean) ** 2).reduce((a, b) => a + b, 0) / values.length);
    return mean > 0 ? (1 - (stdDev / mean)).clamp(0, 1) : 0.5;
  }

  private static calculateFatigue(holdDurations: number[]): number {
    if (holdDurations.length <= 10) return 0;
    
    const half = Math.floor(holdDurations.length / 2);
    const firstHalf = holdDurations.slice(0, half).reduce((a, b) => a + b, 0) / half;
    const secondHalf = holdDurations.slice(half).reduce((a, b) => a + b, 0) / (holdDurations.length - half);
    
    return firstHalf > 0 ? ((secondHalf - firstHalf) / firstHalf).clamp(-1, 1) : 0;
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