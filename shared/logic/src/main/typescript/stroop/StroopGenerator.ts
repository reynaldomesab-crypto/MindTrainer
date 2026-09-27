/**
 * Stroop Challenge Generator (TypeScript)
 * Creates stimuli for multiple Stroop modes with balanced conditions.
 */

export type StroopMode = 'CLASSIC' | 'REVERSE' | 'SWITCH' | 'SPATIAL' | 'SPEED' | 'SEQUENCE';
export type StroopCondition = 'CONGRUENT' | 'INCONGRUENT' | 'NEUTRAL';
export type TaskType = 'COLOR' | 'WORD';

export interface StroopStimulus {
  word: string;
  inkColor: string;
  condition: 'CONGRUENT' | 'INCONGRUENT' | 'NEUTRAL';
  position: 'LEFT' | 'RIGHT' | 'CENTER';
  taskType?: 'COLOR' | 'WORD';
  isSwitchTrial?: boolean;
  isTarget?: boolean;
  stimulusDurationMs: number;
}

export interface StroopConfig {
  mode: StroopMode;
  colorCount: 4 | 5 | 6;
  incongruentRatio: number;
  stimulusDurationMs: number;
  switchFrequency: number;
  sequenceLength: number;
  trialCount: number;
  difficultyLevel: number;
}

export interface StroopWordSets {
  language: string;
  colorWords: string[];
  neutralWords: string[];
  emotionalWords: string[];
  allWords: string[];
}

export class StroopGenerator {
  /**
   * Generates a session of Stroop stimuli for a given mode and difficulty.
   */
  static generateSession(
    mode: StroopMode,
    config: StroopConfig,
    wordSets: StroopWordSets,
    seed: number = Date.now()
  ): StroopStimulus[] {
    const random = new SeededRandom(seed);
    const stimuli: StroopStimulus[] = [];

    const colorWords = wordSets.colorWords;
    const neutralWords = wordSets.neutralWords;
    const allWords = colorWords.concat(neutralWords);
    const colors = ['RED', 'BLUE', 'GREEN', 'YELLOW', 'PURPLE', 'ORANGE']
      .slice(0, config.colorCount);

    const trialCount = config.trialCount;
    const incongruentCount = Math.round(trialCount * config.incongruentRatio);
    const congruentCount = trialCount - incongruentCount;

    switch (mode) {
      case 'CLASSIC':
        stimuli.push(...this.generateIncongruentStimuli(incongruentCount, colors, colorWords, random, config));
        stimuli.push(...this.generateCongruentStimuli(congruentCount, colors, colorWords, random, config));
        break;
      case 'REVERSE':
        stimuli.push(...this.generateIncongruentStimuli(incongruentCount, colors, colorWords, random, config));
        stimuli.push(...this.generateCongruentStimuli(congruentCount, colors, colorWords, random, config));
        break;
      case 'SWITCH':
        stimuli.push(...this.generateSwitchStimuli(trialCount, colors, colorWords, config.neutralWords, random, config));
        break;
      case 'SPATIAL':
        stimuli.push(...this.generateSpatialStimuli(trialCount, colors, allWords, random, config));
        break;
      case 'SPEED':
        stimuli.push(...this.generateSpeedStimuli(trialCount, colors, colorWords, random, config));
        break;
      case 'SEQUENCE':
        stimuli.push(...this.generateSequenceStimuli(trialCount, colors, colorWords, config.sequenceLength, random, config));
        break;
    }

    // Shuffle for mixed presentation (except SEQUENCE which has order)
    if (mode !== 'SEQUENCE') {
      random.nextInt(1); // consume one for shuffle seed
      this.shuffle(stimuli, random);
    }

    return stimuli;
  }

  private static generateIncongruentStimuli(
    count: number,
    colors: string[],
    colorWords: string[],
    random: SeededRandom,
    config: StroopConfig
  ): StroopStimulus[] {
    return Array.from({ length: count }, () => {
      const word = this.randomChoice(colorWords, random);
      const inkColor = this.randomChoice(colors.filter(c => c !== word), random);
      const position = config.mode === 'SPATIAL' ? this.randomChoice(['LEFT', 'RIGHT'], random) : 'CENTER';
      return {
        word,
        inkColor,
        condition: 'INCONGRUENT',
        position,
        stimulusDurationMs: config.stimulusDurationMs,
      };
    });
  }

  private static generateCongruentStimuli(
    count: number,
    colors: string[],
    colorWords: string[],
    random: SeededRandom,
    config: StroopConfig
  ): StroopStimulus[] {
    return Array.from({ length: count }, () => {
      const word = this.randomChoice(colorWords, random);
      const position = config.mode === 'SPATIAL' ? this.randomChoice(['LEFT', 'RIGHT'], random) : 'CENTER';
      return {
        word,
        inkColor: word, // congruent: word == ink color
        condition: 'CONGRUENT',
        position,
        stimulusDurationMs: config.stimulusDurationMs,
      };
    });
  }

  private static generateSwitchStimuli(
    count: number,
    colors: string[],
    colorWords: string[],
    neutralWords: string[],
    random: SeededRandom,
    config: StroopConfig
  ): StroopStimulus[] {
    const stimuli: StroopStimulus[] = [];
    let lastTask: 'COLOR' | 'WORD' | null = null;
    let switchCount = 0;
    const allWords = colorWords.concat(neutralWords);

    for (let i = 0; i < count; i++) {
      const shouldSwitch = lastTask !== null && random.nextDouble() < config.switchFrequency;
      const task = shouldSwitch
        ? (lastTask === 'COLOR' ? 'WORD' : 'COLOR')
        : (lastTask ?? (random.nextDouble() < 0.5 ? 'COLOR' : 'WORD'));
      
      if (shouldSwitch) switchCount++;
      lastTask = task;

      const isCongruent = random.nextDouble() > config.incongruentRatio;
      const word = isCongruent 
        ? this.randomChoice(colorWords, random)
        : this.randomChoice(colorWords.concat(neutralWords), random);
      const inkColor = isCongruent ? word : this.randomChoice(colors.filter(c => c !== word), random);

      stimuli.push({
        word,
        inkColor,
        condition: isCongruent ? 'CONGRUENT' : 'INCONGRUENT',
        taskType: task,
        isSwitchTrial: shouldSwitch,
        stimulusDurationMs: config.stimulusDurationMs,
      });
    }
    return stimuli;
  }

  private static generateSpatialStimuli(
    count: number,
    colors: string[],
    allWords: string[],
    random: SeededRandom,
    config: StroopConfig
  ): StroopStimulus[] {
    return Array.from({ length: count }, () => {
      const position = this.randomChoice(['LEFT', 'RIGHT'], random) as 'LEFT' | 'RIGHT';
      const word = this.randomChoice(allWords, random);
      const inkColor = this.randomChoice(colors, random);
      const isCongruent = colors.includes(word) && word === inkColor;

      return {
        word,
        inkColor,
        condition: isCongruent ? 'CONGRUENT' : 'INCONGRUENT',
        position,
        stimulusDurationMs: config.stimulusDurationMs,
      };
    });
  }

  private static generateSpeedStimuli(
    count: number,
    colors: string[],
    colorWords: string[],
    random: SeededRandom,
    config: StroopConfig
  ): StroopStimulus[] {
    return Array.from({ length: count }, () => {
      const isCongruent = random.nextDouble() < 0.2; // 80% incongruent
      const word = this.randomChoice(colorWords, random);
      const inkColor = isCongruent ? word : this.randomChoice(colors.filter(c => c !== word), random);

      return {
        word,
        inkColor,
        condition: isCongruent ? 'CONGRUENT' : 'INCONGRUENT',
        position: 'CENTER',
        stimulusDurationMs: config.stimulusDurationMs,
      };
    });
  }

  private static generateSequenceStimuli(
    count: number,
    colors: string[],
    colorWords: string[],
    sequenceLength: number,
    random: SeededRandom,
    config: StroopConfig
  ): StroopStimulus[] {
    // Generate a target sequence to remember
    const targetSequence = Array.from({ length: sequenceLength }, () => this.randomChoice(colors, random));

    return Array.from({ length: count }, (_, i) => {
      const isTarget = i % 3 === 0; // Every 3rd stimulus is from target sequence
      const word = isTarget ? targetSequence[i % sequenceLength] : this.randomChoice(colorWords, random);
      const inkColor = this.randomChoice(colors, random);
      const isCongruent = word === inkColor;

      return {
        word,
        inkColor,
        condition: isCongruent ? 'CONGRUENT' : 'INCONGRUENT',
        isTarget,
        stimulusDurationMs: config.stimulusDurationMs,
      };
    });
  }

  static getDailySeed(date: Date = new Date()): number {
    const dayCount = Math.floor(date.getTime() / (1000 * 60 * 60 * 24));
    return 2_000_000 + dayCount;
  }

  // Utility methods
  private static randomChoice<T>(array: T[], random: SeededRandom): T {
    return array[random.nextInt(array.length)];
  }

  private static shuffle<T>(array: T[], random: SeededRandom): void {
    for (let i = array.length - 1; i > 0; i--) {
      const j = random.nextInt(i + 1);
      [array[i], array[j]] = [array[j], array[i]];
    }
  }
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