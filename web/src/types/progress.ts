export interface DailyExerciseStatus {
  exerciseType: string;
  completed: boolean;
  completedAt?: string;
  sessionId?: string;
}

export interface SessionHistoryItem {
  date: string;
  score: number;
  level: number;
  metrics: Record<string, number>;
}

export interface ExerciseProgress {
  exerciseType: string;
  currentLevel: number;
  sessionsCompleted: number;
  lastSession?: string;
  trend: 'IMPROVING' | 'STABLE' | 'DECLINING';
  personalBests: Record<string, number>;
  history: SessionHistoryItem[];
}

export interface GlobalMetrics {
  cognitiveAge: number;
  consistencyScore: number;
  focusIndex: number;
  motorSymmetry: number;
  weeklyActiveDays: number;
}

export interface StreakData {
  current: number;
  longest: number;
  lastActiveDate: string;
  streakFreezeUsed: boolean;
}

export interface Insight {
  id: string;
  type: string;
  title: string;
  description: string;
  generatedAt: string;
  readAt?: string;
}

export interface ProgressDashboard {
  todayStatus: DailyExerciseStatus[];
  exerciseProgress: Record<string, ExerciseProgress>;
  globalMetrics: GlobalMetrics;
  streaks: StreakData;
  insights: Insight[];
}