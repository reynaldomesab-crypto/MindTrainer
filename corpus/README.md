# MindTrainer Corpus Data

This directory contains the exercise content corpus for all supported languages.

## Structure

```
corpus/
├── schulte/          # Schulte Tables (procedural - seeds only)
├── blindfold/        # Blindfold Writing texts
├── nondom/           # Non-Dominant Hand content
│   ├── texts/        # Copywriting texts (200-300 chars)
│   ├── paths/        # Shape tracing paths (SVG points)
│   └── sequences/    # Tap sequences (grid positions)
└── stroop/           # Stroop Challenge word sets
```

## Language Support (12)

| Code | Language | Native Name |
|------|----------|-------------|
| en   | English  | English |
| es   | Spanish  | Español |
| fr   | French   | Français |
| de   | German   | Deutsch |
| pt   | Portuguese | Português |
| zh   | Chinese (Simplified) | 中文 |
| ja   | Japanese | 日本語 |
| ko   | Korean   | 한국어 |
| ru   | Russian  | Русский |
| ar   | Arabic   | العربية |
| hi   | Hindi    | हिन्दी |
| it   | Italian  | Italiano |

## Content Guidelines

### Cultural Localization (Not Translation)
Each language has **native-authored content** — not translated English content.
- Science curiosities relevant to the culture
- Historical facts from the region
- Local animal/plant facts
- Cultural trivia and idioms

### Blindfold Writing Texts
- Length: 200-300 characters (including spaces)
- Tiers: 1 (easier), 2 (medium), 3 (harder)
- Topics: Science, history, nature, linguistics, culture
- Rare character density tracked for keyboard geometry scoring

### Non-Dominant Hand Texts
- Same tier structure as blindfold
- Used for copywriting task
- Shape paths are language-agnostic (visual only)
- Tap sequences are language-agnostic

### Stroop Word Sets
- Color words: Native color names (6 colors)
- Neutral words: Common nouns (20 words)
- Emotional words: Opt-in set for Emotional Stroop variant (15 words)

## File Formats

### Schulte Tables (`schulte/{lang}.json`)
Procedural generation - only seeds stored:
```json
{
  "language": "en",
  "exerciseType": "SCHULTE",
  "items": [{"seed": 1001, "size": 5, "timeLimitMs": 30000, "showNumbers": true}],
  "count": 1000,
  "generatedAt": "2024-01-15T10:00:00Z"
}
```

### Blindfold Texts (`blindfold/{lang}.json`)
```json
{
  "language": "en",
  "exerciseType": "BLINDFOLD",
  "items": [{
    "id": "en_bf_001",
    "language": "en",
    "text": "The quick brown fox...",
    "charCount": 94,
    "tier": 1,
    "topic": "classic pangram",
    "rareCharDensity": 0.08
  }],
  "count": 300,
  "generatedAt": "2024-01-15T10:00:00Z"
}
```

### Non-Dominant Texts (`nondom/texts/{lang}.json`)
Same structure as blindfold texts.

### Tracing Paths (`nondom/paths/{lang}.json`)
Language-agnostic, same file for all languages:
```json
{
  "exerciseType": "NON_DOMINANT",
  "items": [{
    "id": "path_spiral_01",
    "type": "SPIRAL",
    "points": [{"x": 0.5, "y": 0.5}, ...],
    "targetTimeMs": 8000,
    "difficulty": 0.3
  }],
  "count": 50
}
```

### Tap Sequences (`nondom/sequences/{lang}.json`)
Language-agnostic:
```json
{
  "exerciseType": "NON_DOMINANT",
  "items": [{
    "id": "tap_seq_01",
    "gridSize": 3,
    "targets": [0, 4, 8, 2, 6],
    "count": 5,
    "timeLimitMs": 15000
  }],
  "count": 100
}
```

### Stroop Word Sets (`stroop/{lang}.json`)
```json
{
  "language": "en",
  "colorWords": ["RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE"],
  "neutralWords": ["DOG", "HOUSE", ...],
  "emotionalWords": ["ANXIETY", "FEAR", ...]
}
```

## Content Generation

### For Contributors
1. Fork the repository
2. Add content for your native language
3. Follow the cultural localization guidelines
4. Submit a PR with new language files

### Validation
Run the validation script:
```bash
python scripts/validate_corpus.py --lang en
```

Checks:
- Character count ranges
- Tier distribution balance
- Unique content (no duplicates)
- Proper JSON formatting
- UTF-8 encoding
- Cultural appropriateness review