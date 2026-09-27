'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useSession } from 'next-auth/react';
import { SchulteGame } from '@/components/exercises/SchulteGame';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Download, RefreshCw, Clock, Target } from 'lucide-react';
import { SchulteDaily, SchultePackage } from '@/shared/logic/schulte/SchulteGenerator';
import api from '@/lib/api/client';

interface SchultePageProps {
  dailyData: SchulteDaily | null;
  loading: boolean;
}

function SchulteContent({ dailyData, loading }: SchultePageProps) {
  const [packageDownloaded, setPackageDownloaded] = useState(false);
  const [downloading, setDownloading] = useState(false);

  const handleComplete = async (result: {
    completionTimeMs: number;
    tappedSequence: number[];
    accuracy: number;
  }) => {
    try {
      await api.post('/exercises/schulte/submit', {
        config: {
          seed: dailyData!.config.seed,
          size: dailyData!.config.size,
          timeLimitMs: dailyData!.config.timeLimitMs,
          showNumbers: dailyData!.config.showNumbers,
        },
        completionTimeMs: result.completionTimeMs,
        tappedSequence: result.tappedSequence,
        accuracy: result.accuracy,
      });
      // Refresh page or show success
    } catch (error) {
      console.error('Failed to submit:', error);
    }
  };

  const handleDownloadPackage = async () => {
    setDownloading(true);
    try {
      const response = await api.get('/exercises/schulte/package', {
        params: { count: 500, size: dailyData!.config.size },
      });
      // Create download
      const blob = new Blob([JSON.stringify(response.data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `schulte_package_${dailyData!.config.size}x${dailyData!.config.size}_500.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      setPackageDownloaded(true);
      setTimeout(() => setPackageDownloaded(false), 3000);
    } catch (error) {
      console.error('Download failed:', error);
    } finally {
      setDownloading(false);
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
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-medium text-foreground">Schulte Tables</h1>
          <p className="text-muted-foreground">Find numbers 1-25 in order. 30 seconds.</p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={handleDownloadPackage} disabled={downloading}>
            <Download className="h-4 w-4 mr-2" />
            {downloading ? 'Downloading...' : 'Download 500 Tables'}
          </Button>
        </div>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-3 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <Clock className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Time Limit</p>
                <p className="text-xl font-medium">{dailyData.config.timeLimitMs / 1000}s</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-green-100 dark:bg-green-900/30 rounded-lg">
                <Target className="h-5 w-5 text-green-600 dark:text-green-400" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Grid Size</p>
                <p className="text-xl font-medium">{dailyData.config.size}×{dailyData.config.size}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-amber-100 dark:bg-amber-900/30 rounded-lg">
                <RefreshCw className="h-5 w-5 text-amber-600 dark:text-amber-400" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Numbers</p>
                <p className="text-xl font-medium">{dailyData.config.size * dailyData.config.size}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Game */}
      <SchulteGame dailyData={dailyData} onComplete={handleComplete} />
    </div>
  );
}

export default function SchultePage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [dailyData, setDailyData] = useState<SchulteDaily | null>(null);
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
      const response = await fetch('/api/exercises/schulte/daily?size=5');
      if (response.ok) {
        const data = await response.json();
        setDailyData(data);
      } else if (response.status === 403) {
        setDailyData(null); // Already completed
      }
    } catch (error) {
      console.error('Failed to load daily:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <SchulteContent dailyData={dailyData} loading={loading} />
    </div>
  );
}