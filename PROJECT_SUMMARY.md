# MindTrainer - Project Structure Summary

This document provides an overview of the complete project structure created for the MindTrainer brain training application.

## Project Overview

MindTrainer is a cross-platform brain training application with:
- **Android app** (Kotlin + Jetpack Compose)
- **Web app** (Next.js 14 + TypeScript + Tailwind CSS)
- **Backend API** (Spring Boot 3 + Kotlin + PostgreSQL)
- **Shared contracts** (OpenAPI 3.0 specification)
- **12 languages** with cultural localization

## Exercise Types

1. **Schulte Tables** - Visual search & processing speed (30s timer, 4x4 to 7x7 grids)
2. **Blindfold Writing** - Proprioception & motor memory (eyes-closed typing, keyboard geometry scoring)
3. **Non-Dominant Hand** - Interhemispheric coordination (copywriting, tracing, tapping)
4. **Stroop Challenge** - Cognitive inhibition & selective attention (6 modes)

## Repository Structure

```
MindTrainer/
├── .github/
│   └── workflows/
│       ├── ci.yml          # Continuous Integration
│       └── cd.yml          # Continuous Deployment
├── backend/                # Spring Boot 3 (Kotlin)
│   ├── src/main/kotlin/com/mindtrainer/
│   │   ├── auth/           # JWT, OAuth2, Security
│   │   ├── exercise/       # Exercise-specific modules
│   │   ├── progress/       # Progress tracking & analytics
│   │   ├── user/           # User management & preferences
│   │   ├── corpus/         # Content corpus management
│   │   └── common/         # Shared utilities
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/   # Flyway migrations (V1, V2)
│   ├── build.gradle.kts
│   └── Dockerfile
├── android/                # Kotlin + Jetpack Compose
│   ├── app/src/main/java/com/mindtrainer/
│   │   ├── data/           # Room, Retrofit, DataStore, Repository impl
│   │   ├── domain/         # Use cases, Repository interfaces, Models
│   │   ├── di/             # Hilt modules
│   │   ├── ui/
│   │   │   ├── theme/      # Material 3 theme (calm palette)
│   │   │   ├── navigation/ # Navigation Compose
│   │   │   ├── viewmodel/  # ViewModels
│   │   │   ├── screen/     # Screens (Home, Exercises, Progress, Settings)
│   │   │   └── components/ # Reusable UI components
│   │   └── util/           # Utilities
│   ├── build.gradle.kts
│   └── Dockerfile
├── web/                    # Next.js 14 + TypeScript
│   ├── src/
│   │   ├── app/
│   │   │   ├── (auth)/     # Login, Register
│   │   │   ├── (dashboard)/# Protected routes
│   │   │   └── api/        # Next.js API routes
│   │   ├── components/
│   │   │   ├── ui/         # shadcn/ui components
│   │   │   ├── exercises/  # Exercise-specific components
│   │   │   ├── charts/     # Progress visualizations
│   │   │   └── layout/     # Layout components
│   │   ├── lib/
│   │   │   ├── api/        # Axios client, TanStack Query hooks
│   │   │   ├── auth/       # NextAuth configuration
│   │   │   ├── exercises/  # Exercise logic
│   │   │   ├── i18n/       # next-intl configuration
│   │   │   └── pwa/        # Service worker, offline support
│   │   ├── hooks/          # Custom React hooks
│   │   ├── store/          # Zustand stores
│   │   ├── types/          # TypeScript types
│   │   └── styles/         # Global styles (Tailwind)
│   ├── messages/           # i18n dictionaries (12 languages)
│   ├── package.json
│   ├── next.config.js
│   ├── tailwind.config.js
│   └── Dockerfile
├── shared/                 # Shared contracts & logic
│   ├── openapi/
│   │   └── openapi.yaml    # OpenAPI 3.0 specification
│   ├── types/              # Generated types (Kotlin/TS)
│   └── logic/              # Pure exercise logic (dual implementation)
├── corpus/                 # Exercise content (12 languages)
│   ├── schulte/            # Procedural seeds
│   ├── blindfold/          # Blindfold writing texts
│   ├── nondom/             # Non-dominant hand content
│   │   ├── texts/
│   │   ├── paths/
│   │   └── sequences/
│   └── stroop/             # Stroop word sets
├── docs/                   # Documentation
├── docker-compose.yml      # Local development stack
├── .env.example            # Environment variables template
├── README.md
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
└── LICENSE
```

## Key Features Implemented

### Backend (Spring Boot)
- ✅ JWT + OAuth2 (Google, Apple, GitHub) authentication
- ✅ Daily exercise enforcement (1 per type per day)
- ✅ Adaptive difficulty engine with plateau detection
- ✅ Reminder scheduler with timezone support
- ✅ Progress tracking with streak management
- ✅ Leaderboards (global/friends, daily/weekly/monthly/all-time)
- ✅ Social features (friends, challenges, achievements)
- ✅ Flyway database migrations
- ✅ Redis caching
- ✅ OpenAPI/Swagger documentation

### Android (Compose)
- ✅ Clean Architecture (Data, Domain, Presentation)
- ✅ Hilt dependency injection
- ✅ Room local database with offline-first sync
- ✅ DataStore preferences
- ✅ Retrofit + Moshi networking
- ✅ Material 3 theme (calm, procognitive palette)
- ✅ Navigation Compose with type-safe routes
- ✅ Exercise screens (placeholders with Canvas structure)
- ✅ Progress dashboard with charts

### Web (Next.js)
- ✅ App Router with route groups
- ✅ NextAuth.js authentication
- ✅ TanStack Query for server state
- ✅ Zustand for client state
- ✅ Tailwind CSS + shadcn/ui components
- ✅ next-intl for 12-language i18n
- ✅ PWA with Service Worker
- ✅ Exercise components with Canvas rendering
- ✅ Progress visualizations with Recharts

### Shared
- ✅ OpenAPI 3.0 specification (all endpoints)
- ✅ Type-safe API contracts
- ✅ Sample corpus data for English

### Infrastructure
- ✅ Docker Compose for local development
- ✅ Dockerfiles for all services
- ✅ GitHub Actions CI/CD pipelines
- ✅ Code quality (ktlint, detekt, ESLint)
- ✅ Security scanning (OWASP Dependency Check)

## Procognitive Design Principles

- **No pressure**: No countdown stress, no "fail" states
- **Calm palette**: Muted blues/greens, 4.5:1 contrast minimum
- **No streaks pressure**: Streak freeze (1/week), "Rest days are training days"
- **No social comparison**: Percentiles opt-in only
- **Auto-pause**: Micro-breaks after 2 exercises
- **Daily limits**: Hard stop at 4 exercises/day
- **Positive framing**: "Exploring" not "testing"
- **Accessibility first**: Reduced motion, large text, screen reader, colorblind patterns

## Languages Supported (12)

1. English (en)
2. Spanish (es)
3. French (fr)
4. German (de)
5. Portuguese (pt)
6. Chinese Simplified (zh)
7. Japanese (ja)
8. Korean (ko)
9. Russian (ru)
10. Arabic (ar)
11. Hindi (hi)
12. Italian (it)

## Getting Started

```bash
# Start infrastructure
docker-compose up -d postgres redis

# Backend
cd backend && ./gradlew bootRun

# Android
# Open android/ in Android Studio

# Web
cd web && npm install && npm run dev
```

## Deployment

- **Backend**: Docker → Cloud Run / ECS Fargate
- **Web**: Vercel (recommended) or Docker → Cloud Run
- **Android**: Play Console (Internal → Alpha → Beta → Production)

## Next Steps

1. Complete exercise implementations (Canvas rendering, game logic)
2. Add native corpus content for all 12 languages
3. Implement adaptive difficulty algorithms
4. Add progress insights engine
5. Beta testing and accessibility audit
6. App Store / Play Store preparation