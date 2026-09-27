# MindTrainer

Cross-platform brain training app (Android + Web) with 4 cognitive exercises, adaptive difficulty, multi-language cultural localization, daily limits for mental health, and progress dashboard.

## Exercises

| Exercise | Cognitive Domain | Daily Limit |
|----------|-----------------|-------------|
| **Schulte Tables** | Processing Speed, Visual Attention | 1/day |
| **Blindfold Writing** | Fine Motor, Proprioception, Interhemispheric | 1/day |
| **Non-Dominant Hand** | Motor Control, Coordination | 1/day |
| **Stroop Challenge** | Inhibition, Cognitive Flexibility | 1/day |

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Android** | Kotlin, Jetpack Compose, Clean Architecture, Hilt, Room, Retrofit |
| **Web** | Next.js 14 (App Router), React 18, TypeScript, Tailwind CSS, shadcn/ui |
| **Backend** | Spring Boot 3 (Kotlin), Spring Security, JPA/Hibernate, PostgreSQL |
| **Auth** | Custom JWT + OAuth2 (Google, Apple, GitHub) |
| **API** | REST (OpenAPI 3.0 spec), JSON |
| **Infra** | Docker, GitHub Actions, AWS/GCP (TBD) |

## Project Structure

```
mindtrainer/
├── backend/                 # Spring Boot (Kotlin)
├── android/                 # Kotlin + Compose
├── web/                     # Next.js + TypeScript
├── shared/                  # OpenAPI spec + generated types + pure logic
├── corpus/                  # Source content (JSON per language)
├── docs/                    # Architecture, API, contribution
└── .github/workflows/       # CI/CD pipelines
```

## Languages Supported (12)

English, Spanish, French, German, Portuguese, Chinese (Simplified), Japanese, Korean, Russian, Arabic, Hindi, Italian

## Core Principles

- **Procognitive Design**: No pressure, no streaks pressure, calm palette, positive framing
- **Daily Limits**: Hard stop at 4 exercises/day (one of each type)
- **Cultural Localization**: Native content per language, not translations
- **Adaptive Difficulty**: Per-exercise, per-user, with plateau detection
- **Offline-First**: Local data, background sync

## Getting Started

### Prerequisites

- JDK 21+
- Node.js 20+
- Android Studio (for Android)
- PostgreSQL 16+ (for backend)
- Docker (optional, for containerized backend)

### Backend

```bash
cd backend
./gradlew bootRun
```

### Android

Open `android/` in Android Studio and run.

### Web

```bash
cd web
npm install
npm run dev
```

## License

MIT License - see [LICENSE](LICENSE) for details.