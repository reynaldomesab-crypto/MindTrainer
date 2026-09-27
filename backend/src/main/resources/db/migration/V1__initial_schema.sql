-- MindTrainer Database Schema - Initial Migration
-- Version: 1.0.0
-- Description: Create all tables for the MindTrainer application

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- USERS & AUTHENTICATION
-- ============================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    username VARCHAR(50) NOT NULL UNIQUE,
    avatar_url VARCHAR(500),
    provider VARCHAR(20) DEFAULT 'local',
    provider_id VARCHAR(255),
    language VARCHAR(10) DEFAULT 'en',
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_provider ON users(provider, provider_id);

CREATE TABLE user_stats (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_sessions INTEGER NOT NULL DEFAULT 0,
    total_time_ms BIGINT NOT NULL DEFAULT 0,
    current_streak INTEGER NOT NULL DEFAULT 0,
    longest_streak INTEGER NOT NULL DEFAULT 0,
    last_played_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    device_info VARCHAR(500),
    ip_address INET
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);

-- ============================================
-- COGNITIVE DOMAINS & GAMES
-- ============================================

CREATE TABLE cognitive_domains (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    key VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(100),
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE games (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    domain_id UUID NOT NULL REFERENCES cognitive_domains(id),
    key VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    difficulty_config_json JSONB NOT NULL DEFAULT '{}',
    scoring_formula VARCHAR(200) NOT NULL DEFAULT 'linear',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_domain ON games(domain_id);
CREATE INDEX idx_games_active ON games(is_active);

CREATE TABLE game_levels (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    level_num INTEGER NOT NULL,
    config_json JSONB NOT NULL DEFAULT '{}',
    unlock_requirement_json JSONB,
    xp_reward INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(game_id, level_num)
);

CREATE INDEX idx_game_levels_game ON game_levels(game_id);

-- ============================================
-- EXERCISE SESSIONS & RESULTS
-- ============================================

CREATE TABLE exercise_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    key VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    max_daily_sessions INTEGER DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE game_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_type_id UUID NOT NULL REFERENCES exercise_types(id),
    game_id UUID REFERENCES games(id),
    level_id UUID REFERENCES game_levels(id),
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    score INTEGER,
    accuracy NUMERIC(5,4),
    reaction_time_ms INTEGER,
    metadata_json JSONB DEFAULT '{}',
    difficulty_level NUMERIC(4,3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_user ON game_sessions(user_id);
CREATE INDEX idx_sessions_user_date ON game_sessions(user_id, started_at DESC);
CREATE INDEX idx_sessions_exercise ON game_sessions(exercise_type_id);
CREATE INDEX idx_sessions_completed ON game_sessions(completed_at) WHERE completed_at IS NOT NULL;

CREATE TABLE session_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL REFERENCES game_sessions(id) ON DELETE CASCADE,
    timestamp_ms BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data_json JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_session_events_session ON session_events(session_id, timestamp_ms);

-- ============================================
-- DAILY EXERCISE TRACKING
-- ============================================

CREATE TABLE user_daily_exercises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_date DATE NOT NULL,
    exercise_type_id UUID NOT NULL REFERENCES exercise_types(id),
    completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    session_id UUID REFERENCES game_sessions(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, exercise_date, exercise_type_id)
);

CREATE INDEX idx_daily_exercises_user_date ON user_daily_exercises(user_id, exercise_date);

-- ============================================
-- LEADERBOARDS
-- ============================================

CREATE TABLE leaderboards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    period VARCHAR(20) NOT NULL CHECK (period IN ('daily', 'weekly', 'monthly', 'all_time')),
    scope VARCHAR(20) NOT NULL CHECK (scope IN ('global', 'friends')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(game_id, period, scope)
);

CREATE TABLE leaderboard_entries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    leaderboard_id UUID NOT NULL REFERENCES leaderboards(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rank INTEGER NOT NULL,
    score INTEGER NOT NULL,
    achieved_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(leaderboard_id, user_id)
);

CREATE INDEX idx_leaderboard_entries_board ON leaderboard_entries(leaderboard_id, rank);
CREATE INDEX idx_leaderboard_entries_user ON leaderboard_entries(user_id);

-- ============================================
-- SOCIAL FEATURES
-- ============================================

CREATE TABLE friendships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'blocked')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, friend_id),
    CHECK (user_id != friend_id)
);

CREATE INDEX idx_friendships_user ON friendships(user_id, status);
CREATE INDEX idx_friendships_friend ON friendships(friend_id, status);

CREATE TABLE challenges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    challenger_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenged_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id UUID NOT NULL REFERENCES games(id),
    level_id UUID REFERENCES game_levels(id),
    challenger_score INTEGER,
    challenged_score INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'completed', 'declined', 'expired')),
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_challenges_challenger ON challenges(challenger_id, status);
CREATE INDEX idx_challenges_challenged ON challenges(challenged_id, status);
CREATE INDEX idx_challenges_expires ON challenges(expires_at);

CREATE TABLE achievements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    key VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    criteria_json JSONB NOT NULL,
    reward_xp INTEGER DEFAULT 0,
    icon VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_achievements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_id UUID NOT NULL REFERENCES achievements(id),
    unlocked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, achievement_id)
);

CREATE INDEX idx_user_achievements_user ON user_achievements(user_id);

-- ============================================
-- USER DIFFICULTY & PREFERENCES
-- ============================================

CREATE TABLE user_difficulty (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_type_id UUID NOT NULL REFERENCES exercise_types(id),
    current_level NUMERIC(4,3) NOT NULL DEFAULT 0.0,
    preference VARCHAR(20) DEFAULT 'MAINTAIN' CHECK (preference IN ('CHALLENGE', 'MAINTAIN', 'RELAX')),
    last_updated TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, exercise_type_id)
);

CREATE INDEX idx_user_difficulty_user ON user_difficulty(user_id);

CREATE TABLE user_preferences (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    language VARCHAR(10) DEFAULT 'en',
    reminders_enabled BOOLEAN DEFAULT FALSE,
    reminder_time TIME,
    reminder_timezone VARCHAR(50) DEFAULT 'UTC',
    reminder_days_bitmask INTEGER DEFAULT 127,
    reminder_tone VARCHAR(20) DEFAULT 'GENTLE' CHECK (reminder_tone IN ('GENTLE', 'STANDARD', 'NONE')),
    reminder_message TEXT,
    difficulty_schulte VARCHAR(20) DEFAULT 'MAINTAIN' CHECK (difficulty_schulte IN ('CHALLENGE', 'MAINTAIN', 'RELAX')),
    difficulty_blindfold VARCHAR(20) DEFAULT 'MAINTAIN' CHECK (difficulty_blindfold IN ('CHALLENGE', 'MAINTAIN', 'RELAX')),
    difficulty_non_dominant VARCHAR(20) DEFAULT 'MAINTAIN' CHECK (difficulty_non_dominant IN ('CHALLENGE', 'MAINTAIN', 'RELAX')),
    difficulty_stroop VARCHAR(20) DEFAULT 'MAINTAIN' CHECK (difficulty_stroop IN ('CHALLENGE', 'MAINTAIN', 'RELAX')),
    colorblind_mode BOOLEAN DEFAULT FALSE,
    reduced_motion BOOLEAN DEFAULT FALSE,
    large_text BOOLEAN DEFAULT FALSE,
    analytics_opt_in BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================
-- PROGRESS & ANALYTICS
-- ============================================

CREATE TABLE daily_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    progress_date DATE NOT NULL,
    exercise_type_id UUID NOT NULL REFERENCES exercise_types(id),
    level NUMERIC(4,3),
    score INTEGER,
    metrics_json JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, progress_date, exercise_type_id)
);

CREATE INDEX idx_daily_progress_user_date ON daily_progress(user_id, progress_date DESC);

CREATE TABLE user_insights (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    payload_json JSONB DEFAULT '{}',
    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    read_at TIMESTAMPTZ,
    is_dismissed BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_user_insights_user ON user_insights(user_id, generated_at DESC);
CREATE INDEX idx_user_insights_unread ON user_insights(user_id, read_at) WHERE read_at IS NULL;

-- ============================================
-- CORPUS MANAGEMENT
-- ============================================

CREATE TABLE corpus_versions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    exercise_type_id UUID NOT NULL REFERENCES exercise_types(id),
    language VARCHAR(10) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash VARCHAR(64),
    item_count INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(exercise_type_id, language, version)
);

-- ============================================
-- UPDATED_AT TRIGGER FUNCTION
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_games_updated_at BEFORE UPDATE ON games FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_friendships_updated_at BEFORE UPDATE ON friendships FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();