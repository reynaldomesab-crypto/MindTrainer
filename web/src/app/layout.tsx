import type { Metadata, Viewport } from 'next';
import { Inter } from 'next/font/google';
import './styles/globals.css';
import { Providers } from './providers';

const inter = Inter({
  subsets: ['latin'],
  display: 'swap',
  variable: '--font-inter',
});

export const metadata: Metadata = {
  title: 'MindTrainer - Daily Brain Training',
  description: 'Train your brain with daily cognitive exercises. Schulte Tables, Blindfold Writing, Non-Dominant Hand, and Stroop Challenge.',
  keywords: ['brain training', 'cognitive exercises', 'mental fitness', 'neuroplasticity'],
  authors: [{ name: 'MindTrainer Team' }],
  creator: 'MindTrainer',
  publisher: 'MindTrainer',
  robots: 'index, follow',
  openGraph: {
    type: 'website',
    locale: 'en_US',
    url: 'https://mindtrainer.app',
    title: 'MindTrainer - Daily Brain Training',
    description: 'Train your brain with daily cognitive exercises.',
    siteName: 'MindTrainer',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'MindTrainer - Daily Brain Training',
    description: 'Train your brain with daily cognitive exercises.',
  },
  icons: {
    icon: '/favicon.ico',
    shortcut: '/favicon-16x16.png',
    apple: '/apple-touch-icon.png',
  },
  manifest: '/site.webmanifest',
};

export const viewport: Viewport = {
  themeColor: [
    { media: '(prefers-color-scheme: light)', color: '#F8F9FA' },
    { media: '(prefers-color-scheme: dark)', color: '#181C22' },
  ],
  width: 'device-width',
  initialScale: 1,
  maximumScale: 5,
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning className={`${inter.variable} antialiased`}>
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
      </head>
      <body className="min-h-screen bg-background text-foreground">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}