'use client';

import { ExerciseType } from '@/types/exercise';
import { cn } from '@/lib/utils';

interface ExerciseCardProps {
  exercise: ExerciseType;
  completed: boolean;
  onClick: () => void;
  disabled?: boolean;
}

export function ExerciseCard({ exercise, completed, onClick, disabled }: ExerciseCardProps) {
  const colorClasses = {
    primary: 'bg-primary-50 dark:bg-primary-900/20 border-primary-200 dark:border-primary-800',
    secondary: 'bg-secondary-50 dark:bg-secondary-900/20 border-secondary-200 dark:border-secondary-800',
    accent: 'bg-accent-green-50 dark:bg-accent-green-900/20 border-accent-green-200 dark:border-accent-green-800',
    destructive: 'bg-destructive-50 dark:bg-destructive-900/20 border-destructive-200 dark:border-destructive-800',
  };

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={cn(
        'relative group p-6 rounded-xl border-2 transition-all duration-200',
        'hover:shadow-card-hover hover:-translate-y-0.5',
        'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2',
        'disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:shadow-none disabled:hover:translate-y-0',
        completed
          ? 'bg-muted/50 border-muted-300 dark:border-muted-700'
          : colorClasses[exercise.color as keyof typeof colorClasses] || colorClasses.primary
      )}
    >
      {/* Completion badge */}
      {completed && (
        <div className="absolute -top-2 -right-2">
          <span className="flex h-6 w-6 items-center justify-center rounded-full bg-green-500 text-white text-xs font-medium">
            ✓
          </span>
        </div>
      )}

      <div className="flex items-start gap-4">
        {/* Icon */}
        <div className={cn(
          'flex h-14 w-14 items-center justify-center rounded-xl text-3xl',
          completed ? 'bg-muted/50 text-muted-foreground' : 'bg-background/50'
        )}>
          <span>{exercise.icon}</span>
        </div>

        {/* Content */}
        <div className="flex-1 min-w-0">
          <h3 className={cn(
            'font-medium text-title-md truncate',
            completed ? 'text-muted-foreground' : 'text-foreground'
          )}>
            {exercise.name}
          </h3>
          <p className={cn(
            'mt-1 text-body-sm line-clamp-2',
            completed ? 'text-muted-foreground/70' : 'text-muted-foreground'
          )}>
            {exercise.description}
          </p>
          
          {/* Progress indicator for incomplete */}
          {!completed && (
            <div className="mt-3 flex items-center gap-2 text-xs text-muted-foreground">
              <span className="flex items-center gap-1">
                <svg className="h-3.5 w-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <polyline points="12 6 12 12 16 14" />
                </svg>
                Available now
              </span>
            </div>
          )}
        </div>

        {/* Arrow */}
        <div className={cn(
          'flex h-8 w-8 items-center justify-center rounded-lg transition-colors',
          completed ? 'bg-muted/50' : 'bg-background/50 group-hover:bg-primary/10'
        )}>
          <svg className="h-5 w-5 text-muted-foreground" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M5 12h14M12 5l7 7-7 7" />
          </svg>
        </div>
      </div>
    </button>
  );
}