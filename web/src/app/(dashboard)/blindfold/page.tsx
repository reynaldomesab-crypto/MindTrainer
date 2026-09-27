'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useSession } from 'next-auth/react';
import { BlindfoldGame } from '@/components/exercises/BlindfoldGame';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Type, Brain, Zap } from 'lucide-react';

interface BlindfoldPageProps {
  dailyData: any;
  loading: boolean;
}

function BlindfoldContent({ dailyData, loading }: BlindfoldPageProps) {
  const handleComplete = async (result: any) => {
    try {
      await fetch('/api/exercises/blindfold/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(result),
      });
    } catch (error) {
      console.error('Failed to submit:', error);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-muted-foreground">Loading today's challenge...</div>
      </div>
    );
  }

  if (!dailyData) {
    return (
      <div className="text-center py-12">
        <div className="text-4xl mb-4">📅</div>
        <h3 className="text-xl font-medium mb-2">Already completed today!</h3>
        <p className="text-muted-foreground">Come back tomorrow for a new challenge.</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-medium text-foreground">Blindfold Writing</h1>
        <p className="text-muted-foreground">Type with eyes closed. Focus on rhythm.</p>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <Type className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Characters</p>
                <p className="text-xl font-medium">{dailyData.text.charCount}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-green-100 dark:bg-green-900/30 rounded-lg">
                <Brain className="h-5 w-5 text-green-600 dark:text-green-400" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Tier</p>
                <p className="text-xl font-medium">{dailyData.text.tier}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-amber-100 dark:bg-amber-900/30 rounded-lg">
                <Zap className="h-5 w-5 text-amber-600 dark:text-amber-400" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Topic</p>
                <p className="text-xl font-medium text-xs capitalize">{dailyData.text.topic}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <BlindfoldGame dailyData={dailyData} onComplete={async (result) => {
        try {
          await fetch('/api/exercises/blindfold/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(result),
          });
        } catch (error) {
          console.error('Failed to submit:', error);
        }
      }} />
    </div>
  );
}

export default function BlindfoldPage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [dailyData, setDailyData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (status === 'unauthenticated') {
      router.push('/auth/login');
      return;
    }
    if (status === 'authenticated') {
      loadDaily();
    }
  }, [status, router]);

  const loadDaily = async () => {
    try {
      const response = await fetch('/api/exercises/blindfold/daily?lang=en');
      if (response.ok) {
        const data = await response.json();
        setDailyData(data);
      } else if (response.status === 403) {
        setDailyData(null);
      }
    } catch (error) {
      console.error('Failed to load daily:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <BlindfoldContent dailyData={dailyData} loading={loading} />
    </div>
  );
}