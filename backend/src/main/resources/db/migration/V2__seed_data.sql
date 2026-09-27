-- MindTrainer Seed Data
-- Version: 1.1.0
-- Description: Insert reference data for cognitive domains, exercise types, games, achievements

-- ============================================
-- COGNITIVE DOMAINS
-- ============================================

INSERT INTO cognitive_domains (key, name, description, icon, display_order) VALUES
('processing_speed', 'Processing Speed', 'How quickly you can perceive, process, and respond to information', 'speed', 1),
('attention', 'Attention & Focus', 'Ability to concentrate on relevant stimuli while ignoring distractions', 'focus', 2),
('working_memory', 'Working Memory', 'Capacity to hold and manipulate information in mind over short periods', 'memory', 3),
('executive_function', 'Executive Function', 'Higher-order cognitive processes: planning, inhibition, flexibility', 'executive', 4),
('episodic_memory', 'Episodic Memory', 'Memory for personal experiences and specific events in time', 'episodic', 5),
('fluid_intelligence', 'Fluid Intelligence', 'Ability to solve novel problems, reason, and identify patterns', 'fluid', 6),
('motor_coordination', 'Motor Coordination', 'Fine and gross motor control, hand-eye coordination', 'motor', 7);

-- ============================================
-- EXERCISE TYPES
-- ============================================

INSERT INTO exercise_types (key, name, description, max_daily_sessions) VALUES
('SCHULTE', 'Schulte Tables', 'Visual search and processing speed training using number grids', 1),
('BLINDFOLD', 'Blindfold Writing', 'Proprioception and motor memory training by typing with eyes closed', 1),
('NON_DOMINANT', 'Non-Dominant Hand', 'Interhemispheric coordination through non-dominant hand tasks', 1),
('STROOP', 'Stroop Challenge', 'Cognitive inhibition and selective attention via color-word interference', 1);

-- ============================================
-- GAMES
-- ============================================

-- Schulte Tables game
INSERT INTO games (domain_id, key, name, description, difficulty_config_json, scoring_formula) VALUES
((SELECT id FROM cognitive_domains WHERE key = 'processing_speed'),
 'schulte_tables',
 'Schulte Tables',
 'Find numbers 1-N in ascending order on a grid as fast as possible',
 '{"sizes": [4,5,6,7], "default_size": 5, "time_limits_ms": [60000, 45000, 30000, 20000]}',
 'speed_accuracy');

-- Blindfold Writing game
INSERT INTO games (domain_id, key, name, description, difficulty_config_json, scoring_formula) VALUES
((SELECT id FROM cognitive_domains WHERE key = 'motor_coordination'),
 'blindfold_writing',
 'Blindfold Writing',
 'Type a given text with eyes closed, focusing on accuracy and rhythm',
 '{"tiers": [1,2,3], "char_ranges": [[200,250], [250,300], [300,350]], "keyboard_layouts": ["QWERTY","QWERTZ","AZERTY","QZERTY","JCUKEN","ARABIC_101","PINYIN","FLICK","GODAN","HANGUL_2SET","HANGUL_3SET","DEVANAGARI"]}',
 'keyboard_geometry');

-- Non-Dominant Hand game
INSERT INTO games (domain_id, key, name, description, difficulty_config_json, scoring_formula) VALUES
((SELECT id FROM cognitive_domains WHERE key = 'motor_coordination'),
 'non_dominant_hand',
 'Non-Dominant Hand Tasks',
 'Copywriting, shape tracing, and tap sequences with non-dominant hand',
 '{"task_types": ["COPYWRITING","TRACING","TAPPING"], "path_types": ["SPIRAL","FIGURE8","ZIGZAG","WAVE","MAZE"], "grid_sizes": [3,4,5]}',
 'motor_precision');

-- Stroop Challenge game
INSERT INTO games (domain_id, key, name, description, difficulty_config_json, scoring_formula) VALUES
((SELECT id FROM cognitive_domains WHERE key = 'executive_function'),
 'stroop_challenge',
 'Stroop Challenge',
 'Name the ink color while ignoring the word meaning - multiple modes available',
 '{"modes": ["CLASSIC","REVERSE","SWITCH","SPATIAL","SPEED","SEQUENCE"], "color_counts": [4,5,6], "default_mode": "CLASSIC"}',
 'inhibition_index');

-- ============================================
-- GAME LEVELS (Sample configurations)
-- ============================================

-- Schulte levels
INSERT INTO game_levels (game_id, level_num, config_json, unlock_requirement_json, xp_reward) VALUES
((SELECT id FROM games WHERE key = 'schulte_tables'), 1, '{"size": 4, "time_limit_ms": 60000}', NULL, 10),
((SELECT id FROM games WHERE key = 'schulte_tables'), 2, '{"size": 5, "time_limit_ms": 45000}', '{"min_sessions": 3, "min_accuracy": 0.9}', 20),
((SELECT id FROM games WHERE key = 'schulte_tables'), 3, '{"size": 5, "time_limit_ms": 30000}', '{"min_sessions": 5, "min_accuracy": 0.95}', 30),
((SELECT id FROM games WHERE key = 'schulte_tables'), 4, '{"size": 6, "time_limit_ms": 45000}', '{"min_sessions": 10, "min_accuracy": 0.9}', 50),
((SELECT id FROM games WHERE key = 'schulte_tables'), 5, '{"size": 6, "time_limit_ms": 30000}', '{"min_sessions": 15, "min_accuracy": 0.95}', 75),
((SELECT id FROM games WHERE key = 'schulte_tables'), 6, '{"size": 7, "time_limit_ms": 30000}', '{"min_sessions": 20, "min_accuracy": 0.95}', 100);

-- Blindfold levels
INSERT INTO game_levels (game_id, level_num, config_json, unlock_requirement_json, xp_reward) VALUES
((SELECT id FROM games WHERE key = 'blindfold_writing'), 1, '{"tier": 1, "char_range": [200, 250], "max_rms_error": 1.5}', NULL, 10),
((SELECT id FROM games WHERE key = 'blindfold_writing'), 2, '{"tier": 2, "char_range": [250, 300], "max_rms_error": 1.2}', '{"min_sessions": 3, "max_rms_error": 1.3}', 20),
((SELECT id FROM games WHERE key = 'blindfold_writing'), 3, '{"tier": 3, "char_range": [300, 350], "max_rms_error": 1.0}', '{"min_sessions": 5, "max_rms_error": 1.1}', 30);

-- Non-Dominant levels
INSERT INTO game_levels (game_id, level_num, config_json, unlock_requirement_json, xp_reward) VALUES
((SELECT id FROM games WHERE key = 'non_dominant_hand'), 1, '{"tasks": ["COPYWRITING"], "copy_tier": 1, "trace_complexity": 1}', NULL, 10),
((SELECT id FROM games WHERE key = 'non_dominant_hand'), 2, '{"tasks": ["COPYWRITING","TRACING"], "copy_tier": 1, "trace_complexity": 2}', '{"min_sessions": 3}', 20),
((SELECT id FROM games WHERE key = 'non_dominant_hand'), 3, '{"tasks": ["COPYWRITING","TRACING","TAPPING"], "copy_tier": 2, "trace_complexity": 3}', '{"min_sessions": 5}', 30),
((SELECT id FROM games WHERE key = 'non_dominant_hand'), 4, '{"tasks": ["COPYWRITING","TRACING","TAPPING"], "copy_tier": 3, "trace_complexity": 4}', '{"min_sessions": 10}', 50);

-- Stroop levels
INSERT INTO game_levels (game_id, level_num, config_json, unlock_requirement_json, xp_reward) VALUES
((SELECT id FROM games WHERE key = 'stroop_challenge'), 1, '{"mode": "CLASSIC", "color_count": 4, "incongruent_ratio": 0.2, "stimulus_duration_ms": 1000}', NULL, 10),
((SELECT id FROM games WHERE key = 'stroop_challenge'), 2, '{"mode": "CLASSIC", "color_count": 5, "incongruent_ratio": 0.4, "stimulus_duration_ms": 800}', '{"min_sessions": 3, "max_interference_ms": 200}', 20),
((SELECT id FROM games WHERE key = 'stroop_challenge'), 3, '{"mode": "SWITCH", "color_count": 5, "incongruent_ratio": 0.5, "stimulus_duration_ms": 800, "switch_frequency": 0.3}', '{"min_sessions": 5, "max_switch_cost_ms": 150}', 30),
((SELECT id FROM games WHERE key = 'stroop_challenge'), 4, '{"mode": "SPATIAL", "color_count": 6, "incongruent_ratio": 0.6, "stimulus_duration_ms": 600}', '{"min_sessions": 10, "max_interference_ms": 150}', 50),
((SELECT id FROM games WHERE key = 'stroop_challenge'), 5, '{"mode": "SPEED", "color_count": 6, "incongruent_ratio": 0.8, "stimulus_duration_ms": 400}', '{"min_sessions": 15, "min_throughput": 1.5}', 75);

-- ============================================
-- ACHIEVEMENTS
-- ============================================

INSERT INTO achievements (key, name, description, criteria_json, reward_xp, icon) VALUES
-- Streak achievements
('streak_3', 'Getting Started', 'Complete exercises 3 days in a row', '{"type": "streak", "days": 3}', 50, 'fire-1'),
('streak_7', 'Week Warrior', 'Complete exercises 7 days in a row', '{"type": "streak", "days": 7}', 100, 'fire-2'),
('streak_30', 'Monthly Master', 'Complete exercises 30 days in a row', '{"type": "streak", "days": 30}', 500, 'fire-3'),
('streak_100', 'Centurion', 'Complete exercises 100 days in a row', '{"type": "streak", "days": 100}', 1000, 'fire-4'),

-- Session achievements
('sessions_10', 'Beginner', 'Complete 10 exercise sessions', '{"type": "total_sessions", "count": 10}', 50, 'session-1'),
('sessions_50', 'Regular', 'Complete 50 exercise sessions', '{"type": "total_sessions", "count": 50}', 100, 'session-2'),
('sessions_100', 'Dedicated', 'Complete 100 exercise sessions', '{"type": "total_sessions", "count": 100}', 200, 'session-3'),
('sessions_500', 'Veteran', 'Complete 500 exercise sessions', '{"type": "total_sessions", "count": 500}', 500, 'session-4'),

-- Schulte achievements
('schulte_speed_30', 'Speed Reader', 'Complete a 5x5 Schulte table in under 30 seconds', '{"type": "exercise_score", "exercise": "SCHULTE", "metric": "time_ms", "operator": "lt", "value": 30000}', 100, 'schulte-1'),
('schulte_perfect', 'Perfect Grid', 'Complete a Schulte table with 100% accuracy', '{"type": "exercise_score", "exercise": "SCHULTE", "metric": "accuracy", "operator": "eq", "value": 1.0}', 100, 'schulte-2'),
('schulte_master_7x7', 'Grid Master', 'Complete a 7x7 Schulte table', '{"type": "exercise_level", "exercise": "SCHULTE", "level": 6}', 200, 'schulte-3'),

-- Blindfold achievements
('blindfold_rms_05', 'Blind Typist', 'Achieve RMS error below 0.5 key-widths', '{"type": "exercise_score", "exercise": "BLINDFOLD", "metric": "rms_error", "operator": "lt", "value": 0.5}', 150, 'blindfold-1'),
('blindfold_tier3', 'Touch Typist', 'Complete a Tier 3 blindfold text', '{"type": "exercise_level", "exercise": "BLINDFOLD", "level": 3}', 150, 'blindfold-2'),

-- Non-Dominant achievements
('nondom_all_tasks', 'Ambidextrous', 'Complete all three non-dominant tasks in one day', '{"type": "daily_completion", "exercise": "NON_DOMINANT", "tasks": ["COPYWRITING","TRACING","TAPPING"]}', 200, 'nondom-1'),
('nondom_trace_perfect', 'Steady Hand', 'Trace a path with deviation under 2%', '{"type": "exercise_score", "exercise": "NON_DOMINANT", "task": "TRACING", "metric": "deviation", "operator": "lt", "value": 0.02}', 150, 'nondom-2'),

-- Stroop achievements
('stroop_interference_50', 'Inhibition Expert', 'Reduce interference effect below 50ms', '{"type": "exercise_score", "exercise": "STROOP", "metric": "interference_effect", "operator": "lt", "value": 50}', 200, 'stroop-1'),
('stroop_switch_master', 'Task Switcher', 'Achieve switch cost below 30ms', '{"type": "exercise_score", "exercise": "STROOP", "mode": "SWITCH", "metric": "switch_cost", "operator": "lt", "value": 30}', 200, 'stroop-2'),
('stroop_speed_2', 'Speed Demon', 'Achieve 2+ correct responses per second in Speed mode', '{"type": "exercise_score", "exercise": "STROOP", "mode": "SPEED", "metric": "throughput", "operator": "gte", "value": 2.0}', 200, 'stroop-3'),

-- Cross-exercise achievements
('all_four_daily', 'Balanced Brain', 'Complete all 4 exercise types in one day', '{"type": "daily_all_exercises"}', 300, 'balanced-1'),
('polyglot_5', 'Polyglot', 'Complete exercises in 5 different languages', '{"type": "languages_used", "count": 5}', 200, 'polyglot-1'),

-- Social achievements
('friend_challenge_win', 'Friendly Rival', 'Win a challenge against a friend', '{"type": "challenge_win"}', 100, 'social-1'),
('friend_challenge_10', 'Social Butterfly', 'Complete 10 friend challenges', '{"type": "challenge_count", "count": 10}', 200, 'social-2');

-- ============================================
-- DEFAULT EXERCISE TYPE IDs FOR REFERENCE
-- ============================================
-- SCHULTE: (SELECT id FROM exercise_types WHERE key = 'SCHULTE')
-- BLINDFOLD: (SELECT id FROM exercise_types WHERE key = 'BLINDFOLD')
-- NON_DOMINANT: (SELECT id FROM exercise_types WHERE key = 'NON_DOMINANT')
-- STROOP: (SELECT id FROM exercise_types WHERE key = 'STROOP')