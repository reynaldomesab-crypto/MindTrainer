'use client';

import { useQuery } from '@tanstack/react-query';
import api from '@/lib/api/client';
import { ProgressDashboard } from '@/types/progress';

export function useProgress() {
  return useQuery({
    queryKey: ['progress', 'dashboard'],
    queryFn: async (): Promise<ProgressDashboard> => {
      const response = await api.get('/progress/dashboard');
      return response.data;
    },
    staleTime: 60 * 1000, // 1 minute
    gcTime: 5 * 60 * 1000, // 5 minutes
  });
}

export function useExerciseProgress(exerciseType: string, days: number = 30) {
  return useQuery({
    queryKey: ['progress', 'exercise', exerciseType, days],
    queryFn: async () => {
      const response = await api.get(`/progress/exercise/${exerciseType}`, {
        params: { days },
      });
      return response.data;
    },
    enabled: !!exerciseType,
    staleTime: 60 * 1000,
  });
}

export function useInsights() {
  return useQuery({
    queryKey: ['progress', 'insights'],
    queryFn: async () => {
      const response = await api.get('/progress/insights');
      return response.data;
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}