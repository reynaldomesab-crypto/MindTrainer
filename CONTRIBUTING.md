# Contributing to MindTrainer

Thank you for your interest in contributing to MindTrainer! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting Started

### Prerequisites
- JDK 21+
- Node.js 20+
- Android Studio (for Android development)
- PostgreSQL 16+ (for backend)
- Docker (optional, for containerized development)

### Development Setup

1. **Fork and clone the repository**
   ```bash
   git clone https://github.com/yourusername/MindTrainer.git
   cd MindTrainer
   ```

2. **Start infrastructure**
   ```bash
   docker-compose up -d postgres redis
   ```

3. **Backend**
   ```bash
   cd backend
   ./gradlew bootRun
   ```

4. **Android**
   - Open `android/` in Android Studio
   - Run on device/emulator

5. **Web**
   ```bash
   cd web
   npm install
   npm run dev
   ```

## Development Workflow

### Branch Naming
- `feature/description` - New features
- `fix/description` - Bug fixes
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring
- `chore/description` - Maintenance tasks

### Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):
```
type(scope): description

[optional body]

[optional footer]
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`

Example:
```
feat(schulte): add 7x7 grid size option

Adds support for 7x7 Schulte grids for advanced users.
Updates difficulty progression to include new size.

Closes #123
```

### Pull Request Process
1. Create a feature branch from `develop`
2. Make your changes with tests
3. Ensure all CI checks pass
4. Request review from maintainers
5. Address feedback
6. Squash and merge after approval

## Coding Standards

### General
- Follow project-specific style guides
- Write self-documenting code
- Add tests for new functionality
- Update documentation for API changes

### Backend (Kotlin/Spring Boot)
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use constructor injection
- Write unit tests with JUnit 5 + MockK
- Integration tests with Testcontainers

### Android (Kotlin/Compose)
- Follow [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- Use Compose Material 3
- Hilt for dependency injection
- Room for local database
- Write UI tests with Compose Testing

### Web (TypeScript/Next.js)
- Follow [TypeScript Best Practices](https://typescript-eslint.io/rules/)
- Use functional components with hooks
- Tailwind CSS for styling
- shadcn/ui for components
- Write tests with Vitest + React Testing Library

## Testing Requirements

- **Unit tests**: ≥80% coverage for new code
- **Integration tests**: For API endpoints and database operations
- **E2E tests**: For critical user flows (Web: Playwright, Android: Espresso)
- **Contract tests**: For API compatibility (OpenAPI)

Run tests:
```bash
# Backend
cd backend && ./gradlew test

# Android
cd android && ./gradlew testDebugUnitTest

# Web
cd web && npm run test
```

## Adding New Languages

MindTrainer supports 12 languages with **cultural localization** (not translation).

### Process
1. Check if language is in supported list
2. Create corpus files in `corpus/` directory
3. Add translations to `web/src/messages/{lang}.json`
4. Add Android strings in `android/app/src/main/res/values-{lang}/strings.xml`
5. Test thoroughly with native speakers

### Corpus Requirements
- Native-authored content (not translated)
- 300+ texts per exercise per language
- Cultural relevance (local science, history, nature)
- Proper character encoding (UTF-8)

## Reporting Issues

### Bug Reports
Use the bug report template with:
- Clear reproduction steps
- Expected vs actual behavior
- Environment details (OS, device, app version)
- Screenshots/logs if applicable

### Feature Requests
Use the feature request template with:
- Problem statement
- Proposed solution
- Alternatives considered
- Impact assessment

## Security

Report security vulnerabilities privately to security@mindtrainer.app
Do not open public issues for security problems.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.