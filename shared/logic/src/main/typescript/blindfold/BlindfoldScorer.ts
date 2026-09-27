/**
 * Blindfold Writing Scorer (TypeScript)
 * Evaluates typing accuracy using keyboard geometry (key distances)
 * instead of simple character matching.
 */

export interface KeyPosition {
  x: number;
  y: number;
}

export interface BlindfoldResult {
  rmsError: number;
  accuracy: number;
  correctChars: number;
  totalChars: number;
  substitutions: number;
  insertions: number;
  deletions: number;
  keyDistances: number[];
  lengthFactor: number;
  calculateScore: (timeMs: number) => number;
}

interface AlignmentItem {
  expected: string;
  actual: string;
  isMatch: boolean;
}

// Keyboard layouts with normalized key positions (0-1)
const keyboardLayouts: Record<string, Record<string, KeyPosition>> = {
  QWERTY: buildLayout([
    "`1234567890-=",
    "qwertyuiop[]\\",
    "asdfghjkl;'",
    "zxcvbnm,./",
  ]),
  QWERTZ: buildLayout([
    "`1234567890-=",
    "qwertzuiopü+",
    "asdfghjklöä#",
    "yxcvbnm,.-",
  ]),
  AZERTY: buildLayout([
    "`1234567890-=",
    "azertyuiop^$",
    "qsdfghjklmù*",
    "wxcvbn,;:!",
  ]),
  QZERTY: buildLayout([
    "`1234567890-=",
    "qzertyuiop^$",
    "asdfghjklmù*",
    "wxcvbn,;:!",
  ]),
  JCUKEN: buildLayout([
    "ё1234567890-=",
    "йцукенгшщзхъ\\",
    "фывапролджэ",
    "ячсмитьбю.",
  ]),
  ARABIC_101: buildLayout([
    "ذ1234567890-=",
    "ضصثقفغعهخحج",
    "شسيبلاتنمكط",
    "ئءؤرلاىةوزظ",
  ]),
  PINYIN: buildLayout([
    "`1234567890-=",
    "qwertyuiop[]\\",
    "asdfghjkl;'",
    "zxcvbnm,./",
  ]),
  FLICK: {
    ...buildLayout([
      "`1234567890-=",
      "qwertyuiop[]\\",
      "asdfghjkl;'",
      "zxcvbnm,./",
    ]),
    '1': { x: 0.1, y: 0.1 }, '2': { x: 0.3, y: 0.1 }, '3': { x: 0.5, y: 0.1 },
    '4': { x: 0.7, y: 0.1 }, '5': { x: 0.9, y: 0.1 },
    '6': { x: 0.1, y: 0.3 }, '7': { x: 0.3, y: 0.3 }, '8': { x: 0.5, y: 0.3 },
    '9': { x: 0.7, y: 0.3 }, '0': { x: 0.9, y: 0.3 },
    '*': { x: 0.1, y: 0.5 }, '#': { x: 0.9, y: 0.5 },
  },
  GODAN: buildLayout([
    "`1234567890-=",
    "qwertyuiop[]\\",
    "asdfghjkl;'",
    "zxcvbnm,./",
  ]),
  HANGUL_2SET: buildLayout([
    "`1234567890-=",
    "qwertyuiop[]\\",
    "asdfghjkl;'",
    "zxcvbnm,./",
  ]),
  HANGUL_3SET: buildLayout([
    "`1234567890-=",
    "qwertyuiop[]\\",
    "asdfghjkl;'",
    "zxcvbnm,./",
  ]),
  DEVANAGARI: buildLayout([
    "`1234567890-=",
    "qwertyuiop[]\\",
    "asdfghjkl;'",
    "zxcvbnm,./",
  ]),
};

function buildLayout(rows: string[]): Record<string, KeyPosition> {
  const layout: Record<string, KeyPosition> = {};
  const keyWidth = 1.0 / 14.0;
  const keyHeight = 1.0 / 5.0;

  rows.forEach((row, rowIdx) => {
    const y = (rowIdx + 0.5) * keyHeight;
    [...row].forEach((ch, colIdx) => {
      const x = (colIdx + 0.5) * keyWidth;
      layout[ch] = { x, y };
      layout[ch.toUpperCase()] = { x, y };
    });
  });

  // Add space bar
  layout[' '] = { x: 0.5, y: 0.9 };

  return layout;
}

export class BlindfoldScorer {
  /**
   * Scores a blindfold typing attempt.
   * @param expected The target text
   * @param actual What the user typed
   * @param keyboardLayout Keyboard layout identifier
   * @return Scoring result with RMS error, accuracy, and detailed metrics
   */
  static score(
    expected: string,
    actual: string,
    keyboardLayout: string = 'QWERTY'
  ): BlindfoldResult {
    const layout = keyboardLayouts[keyboardLayout.toUpperCase()] ?? keyboardLayouts.QWERTY;

    // Align sequences using Levenshtein-like approach
    const alignment = this.alignSequences(expected, actual);

    let totalKeyDistance = 0;
    let correctChars = 0;
    let substitutions = 0;
    let insertions = 0;
    let deletions = 0;

    const keyDistances: number[] = [];

    for (const item of alignment) {
      const expPos = layout[item.expected.toUpperCase()] ?? { x: 0.5, y: 0.5 };
      const actPos = layout[item.actual.toUpperCase()] ?? { x: 0.5, y: 0.5 };

      const distance = Math.hypot(expPos.x - actPos.x, expPos.y - actPos.y);
      keyDistances.push(distance);
      totalKeyDistance += distance;

      if (item.isMatch) {
        correctChars++;
      } else if (item.expected === '\0') {
        insertions++;
      } else if (item.actual === '\0') {
        deletions++;
      } else {
        substitutions++;
      }
    }

    const rmsError = alignment.length > 0
      ? Math.sqrt(totalKeyDistance / alignment.length)
      : 0;

    const accuracy = expected.length > 0 ? correctChars / expected.length : 1;

    // Length factor: longer texts are harder (logarithmic scaling)
    const lengthFactor = 1 / (1 + Math.max(0, Math.log(expected.length / 30)));

    const result: BlindfoldResult = {
      rmsError,
      accuracy,
      correctChars,
      totalChars: expected.length,
      substitutions,
      insertions,
      deletions,
      keyDistances,
      lengthFactor,
      calculateScore: (timeMs: number) => this.calculateScore(result, timeMs),
    };

    return result;
  }

  /**
   * Aligns expected and actual sequences for comparison.
   * Uses dynamic programming for Levenshtein distance with backtracking.
   */
  private static alignSequences(expected: string, actual: string): AlignmentItem[] {
    const m = expected.length;
    const n = actual.length;

    // DP table for edit distance
    const dp: number[][] = Array(m + 1).fill(null).map(() => Array(n + 1).fill(0));
    for (let i = 0; i <= m; i++) dp[i][0] = i;
    for (let j = 0; j <= n; j++) dp[0][j] = j;

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        const cost = expected[i - 1] === actual[j - 1] ? 0 : 1;
        dp[i][j] = Math.min(
          dp[i - 1][j] + 1,      // deletion
          dp[i][j - 1] + 1,      // insertion
          dp[i - 1][j - 1] + cost // substitution/match
        );
      }
    }

    // Backtrack to get alignment
    const alignment: AlignmentItem[] = [];
    let i = m;
    let j = n;

    while (i > 0 || j > 0) {
      if (i > 0 && j > 0 && dp[i][j] === dp[i - 1][j - 1] + (expected[i - 1] === actual[j - 1] ? 0 : 1)) {
        alignment.unshift({
          expected: expected[i - 1],
          actual: actual[j - 1],
          isMatch: expected[i - 1] === actual[j - 1],
        });
        i--;
        j--;
      } else if (i > 0 && dp[i][j] === dp[i - 1][j] + 1) {
        alignment.unshift({
          expected: expected[i - 1],
          actual: '\0',
          isMatch: false,
        });
        i--;
      } else if (j > 0 && dp[i][j] === dp[i][j - 1] + 1) {
        alignment.unshift({
          expected: '\0',
          actual: actual[j - 1],
          isMatch: false,
        });
        j--;
      } else {
        // fallback
        if (i > 0 && j > 0) {
          alignment.unshift({
            expected: expected[i - 1],
            actual: actual[j - 1],
            isMatch: false,
          });
          i--;
          j--;
        } else if (i > 0) {
          alignment.unshift({
            expected: expected[i - 1],
            actual: '\0',
            isMatch: false,
          });
          i--;
        } else {
          alignment.unshift({
            expected: '\0',
            actual: actual[j - 1],
            isMatch: false,
          });
          j--;
        }
      }
    }

    return alignment;
  }

  /**
   * Calculates final score (0-100, higher is better).
   */
  private static calculateScore(result: BlindfoldResult, timeMs: number): number {
    // Base score from accuracy and RMS error
    let accuracyScore = result.accuracy * 100;
    const errorPenalty = Math.min(50, result.rmsError * 20); // max 50 point penalty
    const lengthBonus = (1 - result.lengthFactor) * 20; // bonus for longer texts

    let baseScore = Math.max(0, Math.min(100, accuracyScore - errorPenalty + lengthBonus));

    // Time factor (optional - faster is slightly better)
    if (timeMs > 0) {
      const wpm = (result.totalChars / 5) / (timeMs / 60000);
      if (wpm > 40) baseScore *= 1.05;
      else if (wpm > 25) baseScore *= 1.0;
      else baseScore *= 0.95;
    }

    return Math.round(Math.max(0, Math.min(100, baseScore)));
  }
}