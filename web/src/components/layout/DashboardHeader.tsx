'use client';

import { useSession, signOut } from 'next-auth/react';
import { cn } from '@/lib/utils';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger, DropdownMenuSeparator } from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Settings, LogOut, User, BarChart3, Bell } from 'lucide-react';
import { useRouter } from 'next/navigation';

interface DashboardHeaderProps {
  user?: {
    name?: string | null;
    email?: string | null;
    image?: string | null;
  };
}

export function DashboardHeader({ user }: DashboardHeaderProps) {
  const router = useRouter();
  const { data: session } = useSession();

  const handleSignOut = async () => {
    await signOut({ callbackUrl: '/auth/login' });
  };

  return (
    <header className="sticky top-0 z-40 w-full border-b border-border/50 bg-background/80 backdrop-blur-sm">
      <div className="mx-auto max-w-4xl px-4">
        <div className="flex h-16 items-center justify-between">
          {/* Logo / Title */}
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary text-primary-foreground">
              <span className="text-xl">🧠</span>
            </div>
            <div>
              <h1 className="font-medium text-headline-sm text-foreground">MindTrainer</h1>
              <p className="text-xs text-muted-foreground">Daily brain practice</p>
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-2">
            {/* Progress Link */}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => router.push('/progress')}
              className="gap-2 px-3"
            >
              <BarChart3 className="h-4 w-4" />
              <span className="hidden sm:inline">Progress</span>
            </Button>

            {/* Settings Link */}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => router.push('/settings')}
              className="gap-2 px-3"
            >
              <Settings className="h-4 w-4" />
              <span className="hidden sm:inline">Settings</span>
            </Button>

            {/* User Menu */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" className="h-9 w-9 rounded-full">
                  <Avatar className="h-9 w-9">
                    <AvatarImage src={user?.image || ''} alt={user?.name || 'User'} />
                    <AvatarFallback className="text-xs font-medium">
                      {user?.name?.charAt(0).toUpperCase() || 'U'}
                    </AvatarFallback>
                  </Avatar>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <div className="px-2 py-1">
                  <p className="text-sm font-medium truncate">{user?.name || 'User'}</p>
                  <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
                </div>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => router.push('/settings')}>
                  <User className="mr-2 h-4 w-4" />
                  Profile
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => router.push('/progress')}>
                  <BarChart3 className="mr-2 h-4 w-4" />
                  Progress
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => router.push('/settings')}>
                  <Settings className="mr-2 h-4 w-4" />
                  Settings
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleSignOut} className="text-destructive focus:text-destructive">
                  <LogOut className="mr-2 h-4 w-4" />
                  Sign out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </div>
    </header>
  );
}