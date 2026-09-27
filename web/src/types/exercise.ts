export interface ExerciseType {
  id: string;
  name: string;
  description: string;
  icon: string;
  color: 'primary' | 'secondary' | 'accent' | 'destructive';
}

export interface SchulteConfig {
  seed: number;
  size: 4 | 5 | 6 | 7;
  timeLimitMs: number;
  showNumbers: boolean;
}

export interface SchulteDaily {
  config: SchulteConfig;
  numbers: number[];
  expiresAt: string;
}

export interface BlindfoldText {
  id: string;
  language: string;
  text: string;
  charCount: number;
  tier: 1 | 2 | 3;
  topic: string;
  rareCharDensity: number;
}

export interface BlindfoldDaily {
  text: BlindfoldText;
  expiresAt: string;
}

export interface Point {
  x: number;
  y: number;
}

export interface ShapePath {
  id: string;
  type: 'SPIRAL' | 'FIGURE8' | 'ZIGZAG' | 'WAVE' | 'MAZE';
  points: Point[];
  targetTimeMs: number;
  difficulty: number;
}

export interface TapSequence {
  id: string;
  gridSize: number;
  targets: number[];
  count: number;
  timeLimitMs: number;
}

export interface NonDomDaily {
  copywriting: BlindfoldText;
  tracing: ShapePath;
  tapping: TapSequence;
  expiresAt: string;
}

export interface StroopStimulus {
  word: string;
  inkColor: string;
  position?: 'LEFT' | 'RIGHT' | 'CENTER';
  audioColor?: string;
}

export interface StroopSessionConfig {
  colorCount: 4 | 5 | 6;
  incongruentRatio: number;
  stimulusDurationMs: number;
  switchFrequency: number;
  sequenceLength: number;
}

export interface StroopDaily {
  mode: 'CLASSIC' | 'REVERSE' | 'SWITCH' | 'SPATIAL' | 'SPEED' | 'SEQUENCE';
  config: StroopSessionConfig;
  stimuli: StroopStimulus[];
  expiresAt: string;
}

export interface StroopWordSets {
  language: string;
  colorWords: string[];
  neutralWords: string[];
  emotionalWords: string[];
}