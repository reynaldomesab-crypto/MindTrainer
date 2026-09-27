'use client';

import { useEffect, useState } from 'react';
import { useSession } from 'next-auth/react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Slider } from '@/components/ui/slider';
import { User, Bell, Brain, Eye, FontSize, Shield, Download, Trash2, Moon, Sun, Monitor, Palette } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useSession, signOut } from 'next-auth/react';

interface SettingsPageProps {
  user: any;
}

function SettingsContent({ user }: SettingsPageProps) {
  const [settings, setSettings] = useState({
    language: 'en',
    remindersEnabled: false,
    reminderTime: '19:00',
    reminderTone: 'GENTLE',
    reminderDays: ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'],
    reminderMessage: '',
    difficultySchulte: 'MAINTAIN',
    difficultyBlindfold: 'MAINTAIN',
    difficultyNonDominant: 'MAINTAIN',
    difficultyStroop: 'MAINTAIN',
    colorblindMode: false,
    reducedMotion: false,
    largeText: false,
    analyticsOptIn: false,
    theme: 'system' as 'light' | 'dark' | 'system',
  });

  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  const handleSave = async () => {
    setSaving(true);
    try {
      const response = await fetch('/api/users/me/preferences', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(settings),
      });
      if (response.ok) {
        setSaved(true);
        setTimeout(() => setSaved(false), 3000);
      }
    } catch (error) {
      console.error('Failed to save:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleSignOut = async () => {
    await signOut({ callbackUrl: '/auth/login' });
  };

  const handleDeleteAccount = async () => {
    if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      try {
        await fetch('/api/users/me', { method: 'DELETE' });
        await signOut({ callbackUrl: '/auth/login' });
      } catch (error) {
        console.error('Failed to delete account:', error);
      }
    }
  };

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  return (
    <div className="max-w-2xl mx-auto space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-medium text-foreground">Settings</h1>
        <p className="text-muted-foreground">Manage your account and preferences</p>
      </div>

      {/* Profile */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <User className="h-5 w-5" />
            Profile
          </CardTitle>
          <CardDescription>Your account information</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input id="username" defaultValue={user?.name || ''} disabled />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" defaultValue={user?.email || ''} disabled />
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="language">Language</Label>
            <Select value={settings.language} onValueChange={v => setSettings(s => ({ ...s, language: v }))}>
              <SelectTrigger>
                <SelectValue placeholder="Select language" />
              </SelectTrigger>
              <SelectContent>
                {[
                  { code: 'en', name: 'English' },
                  { code: 'es', name: 'Español' },
                  { code: 'fr', name: 'Français' },
                  { code: 'de', name: 'Deutsch' },
                  { code: 'pt', name: 'Português' },
                  { code: 'zh', name: '中文' },
                  { code: 'ja', name: '日本語' },
                  { code: 'ko', name: '한국어' },
                  { code: 'ru', name: 'Русский' },
                  { code: 'ar', name: 'العربية' },
                  { code: 'hi', name: 'हिन्दी' },
                  { code: 'it', name: 'Italiano' },
                ].map(lang => (
                  <SelectItem key={lang.code} value={lang.code}>{lang.name}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Reminders */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Bell className="h-5 w-5" />
            Reminders
          </CardTitle>
          <CardDescription>Get notified when it's time to train</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <Label>Daily Reminder</Label>
              <p className="text-sm text-muted-foreground">Receive a notification at your chosen time</p>
            </div>
            <Switch
              checked={settings.remindersEnabled}
              onCheckedChange={checked => setSettings(s => ({ ...s, remindersEnabled: checked }))}
            />
          </div>

          {settings.remindersEnabled && (
            <div className="space-y-4 border-t border-border/50 pt-6">
              <div className="space-y-2">
                <Label htmlFor="reminderTime">Reminder Time</Label>
                <Input
                  id="reminderTime"
                  type="time"
                  value={settings.reminderTime}
                  onChange={e => setSettings(s => ({ ...s, reminderTime: e.target.value }))}
                />
              </div>

              <div className="space-y-2">
                <Label>Reminder Tone</Label>
                <Select value={settings.reminderTone} onValueChange={v => setSettings(s => ({ ...s, reminderTone: v }))}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select tone" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="GENTLE">Gentle - "Almost time for your brain practice"</SelectItem>
                    <SelectItem value="STANDARD">Standard - "Daily exercises available"</SelectItem>
                    <SelectItem value="NONE">Silent - Badge only</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label>Days of Week</Label>
                <div className="flex flex-wrap gap-2">
                  {days.map(day => (
                    <Label
                      key={day}
                      className={`px-3 py-1.5 rounded-full text-sm cursor-pointer transition-colors ${
                        settings.reminderDays.includes(day)
                          ? 'bg-primary text-primary-foreground'
                          : 'bg-muted text-muted-foreground hover:bg-muted/80'
                      }`}
                    >
                      <input
                        type="checkbox"
                        checked={settings.reminderDays.includes(day)}
                        onChange={e => setSettings(s => ({
                          ...s,
                          reminderDays: e.target.checked
                            ? [...s.reminderDays, day]
                            : s.reminderDays.filter(d => d !== day)
                        }))}
                        className="sr-only"
                      />
                      {day.slice(0, 3)}
                    </Label>
                  ))}
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reminderMessage">Custom Message (optional)</Label>
                <Input
                  id="reminderMessage"
                  value={settings.reminderMessage}
                  onChange={e => setSettings(s => ({ ...s, reminderMessage: e.target.value }))}
                  placeholder="Your personal reminder message"
                />
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Difficulty Preferences */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Brain className="h-5 w-5" />
            Difficulty Preferences
          </CardTitle>
          <CardDescription>How should the app adapt to your performance?</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground">
            Choose how quickly the difficulty should adapt to your performance.
          </p>
          <div className="grid gap-4 sm:grid-cols-2">
            {[
              { key: 'schulte', label: 'Schulte Tables', icon: '🔢' },
              { key: 'blindfold', label: 'Blindfold Writing', icon: '⌨️' },
              { key: 'nonDominant', label: 'Non-Dominant Hand', icon: '✍️' },
              { key: 'stroop', label: 'Stroop Challenge', icon: '🎨' },
            ].map(item => (
              <div key={item.key} className="space-y-2">
                <Label className="flex items-center gap-2">
                  <span className="text-xl">{item.icon}</span>
                  {item.label}
                </Label>
                <Select
                  value={settings[`difficulty${item.key.charAt(0).toUpperCase() + item.key.slice(1)}` as keyof typeof settings] as string}
                  onValueChange={v => setSettings(s => ({ ...s, [`difficulty${item.key.charAt(0).toUpperCase() + item.key.slice(1)}`]: v }))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select preference" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="CHALLENGE">Challenge me - Faster progression</SelectItem>
                    <SelectItem value="MAINTAIN">Maintain - Balanced progression</SelectItem>
                    <SelectItem value="RELAX">Relaxed - Slower, comfortable progression</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Accessibility */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Eye className="h-5 w-5" />
            Accessibility
          </CardTitle>
          <CardDescription>Customize the app for your needs</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="flex items-center justify-between">
              <div>
                <Label>Colorblind Mode</Label>
                <p className="text-sm text-muted-foreground">Use patterns instead of colors for colorblind accessibility</p>
              </div>
              <Switch
                checked={settings.colorblindMode}
                onCheckedChange={checked => setSettings(s => ({ ...s, colorblindMode: checked }))}
              />
            </div>
            <div className="flex items-center justify-between">
              <div>
                <Label>Reduced Motion</Label>
                <p className="text-sm text-muted-foreground">Minimize animations and transitions</p>
              </div>
              <Switch
                checked={settings.reducedMotion}
                onCheckedChange={checked => setSettings(s => ({ ...s, reducedMotion: checked }))}
              />
            </div>
            <div className="flex items-center justify-between">
              <div>
                <Label>Large Text</Label>
                <p className="text-sm text-muted-foreground">Increase text size throughout the app</p>
              </div>
              <Switch
                checked={settings.largeText}
                onCheckedChange={checked => setSettings(s => ({ ...s, largeText: checked }))}
              />
            </div>
            <div className="flex items-center justify-between">
              <div>
                <Label>Theme</Label>
                <p className="text-sm text-muted-foreground">Choose your preferred color scheme</p>
              </div>
              <Select value={settings.theme} onValueChange={v => setSettings(s => ({ ...s, theme: v as any }))}>
                <SelectTrigger className="w-48">
                  <SelectValue placeholder="Select theme" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="light">Light</SelectItem>
                  <SelectItem value="dark">Dark</SelectItem>
                  <SelectItem value="system">System</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Privacy & Data */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Shield className="h-5 w-5" />
            Privacy & Data
          </CardTitle>
          <CardDescription>Control your data and privacy settings</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <Label>Anonymous Analytics</Label>
              <p className="text-sm text-muted-foreground">Help improve the app by sharing anonymous usage data</p>
            </div>
            <Switch
              checked={settings.analyticsOptIn}
              onCheckedChange={checked => setSettings(s => ({ ...s, analyticsOptIn: checked }))}
            />
          </div>

          <Separator />

          <div className="flex items-center justify-between">
            <div>
              <Label>Export My Data</Label>
              <p className="text-sm text-muted-foreground">Download a copy of all your data</p>
            </div>
            <Button variant="outline" size="sm" onClick={() => { /* export data */ }}>
              <Download className="h-4 w-4 mr-2" />
              Export Data
            </Button>
          </div>

          <Separator />

          <div className="flex items-center justify-between">
            <div>
              <Label className="text-destructive">Delete Account</Label>
              <p className="text-sm text-muted-foreground">Permanently delete your account and all data</p>
            </div>
            <Button variant="destructive" size="sm" onClick={() => { /* delete account */ }}>
              <Trash2 className="h-4 w-4 mr-2" />
              Delete Account
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Save Button */}
      <div className="flex justify-end">
        <Button 
          onClick={handleSave} 
          disabled={saving}
          className="px-8 py-3"
        >
          {saving ? 'Saving...' : saved ? 'Saved!' : 'Save Changes'}
        </Button>
      </div>

      {/* Danger Zone */}
      <Card className="border-destructive/50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-destructive">
            <Trash2 className="h-5 w-5" />
            Danger Zone
          </CardTitle>
          <CardDescription>Irreversible actions</CardDescription>
        </CardHeader>
        <CardContent>
          <Button variant="destructive" onClick={handleDeleteAccount}>
            <Trash2 className="h-4 w-4 mr-2" />
            Delete Account Permanently
          </Button>
        </CardContent>
      </Card>

      {/* Sign Out */}
      <div className="text-center">
        <Button variant="ghost" onClick={handleSignOut}>
          <Moon className="h-4 w-4 mr-2" />
          Sign Out
        </Button>
      </div>
    </div>
  );
}

export default function SettingsPage() {
  const { data: session, status } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (status === 'unauthenticated') {
      router.push('/auth/login');
    }
  }, [status, router]);

  if (status === 'loading') {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-muted-foreground">Loading settings...</div>
      </div>
    );
  }

  if (status === 'unauthenticated') {
    return null;
  }

  return (
    <div className="p-6">
      <SettingsContent user={session?.user} />
    </div>
  );
}