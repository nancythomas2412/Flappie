package com.glacierbird.game

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlin.math.max
import kotlin.math.min

/**
 * StatisticsManager - Comprehensive player analytics and statistics
 * Essential for player engagement and Google Play Store success
 */
class StatisticsManager(private val context: Context) {
    
    companion object {
        private const val TAG = "StatisticsManager"
        private const val PREFS_NAME = "statistics_prefs"
        
        // Game statistics keys
        private const val KEY_GAMES_PLAYED = "games_played"
        private const val KEY_TOTAL_SCORE = "total_score"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_TOTAL_PIPES_PASSED = "total_pipes_passed"
        private const val KEY_TOTAL_COINS_COLLECTED = "total_coins_collected"
        private const val KEY_TOTAL_POWER_UPS_USED = "total_power_ups_used"
        private const val KEY_TOTAL_DEATHS = "total_deaths"
        private const val KEY_TOTAL_CONTINUES = "total_continues"
        private const val KEY_TOTAL_PLAYTIME = "total_playtime_seconds"
        
        // Streak tracking
        private const val KEY_CURRENT_WIN_STREAK = "current_win_streak"
        private const val KEY_LONGEST_WIN_STREAK = "longest_win_streak"
        private const val KEY_CURRENT_PLAY_STREAK = "current_play_streak"
        private const val KEY_LONGEST_PLAY_STREAK = "longest_play_streak"
        
        // Power-up specific stats
        private const val KEY_SHIELD_USES = "shield_uses"
        private const val KEY_MAGNET_USES = "magnet_uses"
        private const val KEY_SLOW_MOTION_USES = "slow_motion_uses"
        
        // Score brackets (for difficulty analysis)
        private const val KEY_SCORES_0_10 = "scores_0_10"
        private const val KEY_SCORES_11_25 = "scores_11_25"
        private const val KEY_SCORES_26_50 = "scores_26_50"
        private const val KEY_SCORES_51_100 = "scores_51_100"
        private const val KEY_SCORES_100_PLUS = "scores_100_plus"
        
        // Time-based stats
        private const val KEY_FIRST_GAME_DATE = "first_game_date"
        private const val KEY_LAST_GAME_DATE = "last_game_date"
        private const val KEY_SESSIONS_TODAY = "sessions_today"
        private const val KEY_LAST_SESSION_DATE = "last_session_date"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var sessionStartTime: Long = 0
    
    /**
     * Start a new game session
     */
    fun startSession() {
        sessionStartTime = System.currentTimeMillis()
        updateSessionCount()
        
        // Set first game date if not set
        if (prefs.getString(KEY_FIRST_GAME_DATE, "")?.isEmpty() != false) {
            prefs.edit().putString(KEY_FIRST_GAME_DATE, getCurrentDateString()).apply()
        }
        
        Log.d(TAG, "Game session started")
    }
    
    /**
     * Record game completion stats
     */
    fun recordGameCompletion(
        score: Int,
        pipesPassedThisGame: Int,
        coinsCollectedThisGame: Int,
        powerUpsUsedThisGame: Map<String, Int>,
        died: Boolean,
        usedContinue: Boolean
    ) {
        val editor = prefs.edit()
        
        // Basic game stats
        editor.putInt(KEY_GAMES_PLAYED, prefs.getInt(KEY_GAMES_PLAYED, 0) + 1)
        editor.putLong(KEY_TOTAL_SCORE, prefs.getLong(KEY_TOTAL_SCORE, 0) + score)
        editor.putInt(KEY_HIGH_SCORE, max(prefs.getInt(KEY_HIGH_SCORE, 0), score))
        editor.putInt(KEY_TOTAL_PIPES_PASSED, prefs.getInt(KEY_TOTAL_PIPES_PASSED, 0) + pipesPassedThisGame)
        editor.putInt(KEY_TOTAL_COINS_COLLECTED, prefs.getInt(KEY_TOTAL_COINS_COLLECTED, 0) + coinsCollectedThisGame)
        
        // Death and continue tracking
        if (died) {
            editor.putInt(KEY_TOTAL_DEATHS, prefs.getInt(KEY_TOTAL_DEATHS, 0) + 1)
        }
        if (usedContinue) {
            editor.putInt(KEY_TOTAL_CONTINUES, prefs.getInt(KEY_TOTAL_CONTINUES, 0) + 1)
        }
        
        // Power-up usage tracking
        var totalPowerUpsThisGame = 0
        powerUpsUsedThisGame.forEach { (powerUpType, count) ->
            totalPowerUpsThisGame += count
            when (powerUpType) {
                "shield" -> editor.putInt(KEY_SHIELD_USES, prefs.getInt(KEY_SHIELD_USES, 0) + count)
                "magnet" -> editor.putInt(KEY_MAGNET_USES, prefs.getInt(KEY_MAGNET_USES, 0) + count)
                "slow_motion" -> editor.putInt(KEY_SLOW_MOTION_USES, prefs.getInt(KEY_SLOW_MOTION_USES, 0) + count)
            }
        }
        editor.putInt(KEY_TOTAL_POWER_UPS_USED, prefs.getInt(KEY_TOTAL_POWER_UPS_USED, 0) + totalPowerUpsThisGame)
        
        // Score bracket tracking
        updateScoreBracket(editor, score)
        
        // Streak tracking
        updateStreaks(editor, score, died)
        
        // Session time tracking
        if (sessionStartTime > 0) {
            val sessionTime = (System.currentTimeMillis() - sessionStartTime) / 1000
            editor.putLong(KEY_TOTAL_PLAYTIME, prefs.getLong(KEY_TOTAL_PLAYTIME, 0) + sessionTime)
        }
        
        // Update last game date
        editor.putString(KEY_LAST_GAME_DATE, getCurrentDateString())
        
        editor.apply()
        
        Log.d(TAG, "Game completion recorded - Score: $score, Pipes: $pipesPassedThisGame, Died: $died")
    }
    
    /**
     * Update score bracket statistics
     */
    private fun updateScoreBracket(editor: SharedPreferences.Editor, score: Int) {
        when {
            score <= 10 -> editor.putInt(KEY_SCORES_0_10, prefs.getInt(KEY_SCORES_0_10, 0) + 1)
            score <= 25 -> editor.putInt(KEY_SCORES_11_25, prefs.getInt(KEY_SCORES_11_25, 0) + 1)
            score <= 50 -> editor.putInt(KEY_SCORES_26_50, prefs.getInt(KEY_SCORES_26_50, 0) + 1)
            score <= 100 -> editor.putInt(KEY_SCORES_51_100, prefs.getInt(KEY_SCORES_51_100, 0) + 1)
            else -> editor.putInt(KEY_SCORES_100_PLUS, prefs.getInt(KEY_SCORES_100_PLUS, 0) + 1)
        }
    }
    
    /**
     * Update win and play streaks
     */
    private fun updateStreaks(editor: SharedPreferences.Editor, score: Int, died: Boolean) {
        val currentWinStreak = prefs.getInt(KEY_CURRENT_WIN_STREAK, 0)
        val longestWinStreak = prefs.getInt(KEY_LONGEST_WIN_STREAK, 0)
        
        if (!died && score > 10) { // Consider score > 10 as a "win"
            val newWinStreak = currentWinStreak + 1
            editor.putInt(KEY_CURRENT_WIN_STREAK, newWinStreak)
            editor.putInt(KEY_LONGEST_WIN_STREAK, max(longestWinStreak, newWinStreak))
        } else if (died) {
            editor.putInt(KEY_CURRENT_WIN_STREAK, 0)
        }
        
        // Play streak (consecutive days)
        updatePlayStreak(editor)
    }
    
    /**
     * Update daily play streak
     */
    private fun updatePlayStreak(editor: SharedPreferences.Editor) {
        val today = getCurrentDateString()
        val lastGameDate = prefs.getString(KEY_LAST_GAME_DATE, "") ?: ""
        val currentPlayStreak = prefs.getInt(KEY_CURRENT_PLAY_STREAK, 0)
        val longestPlayStreak = prefs.getInt(KEY_LONGEST_PLAY_STREAK, 0)
        
        if (lastGameDate != today) {
            val yesterday = getYesterdayDateString()
            val newPlayStreak = if (lastGameDate == yesterday) currentPlayStreak + 1 else 1
            
            editor.putInt(KEY_CURRENT_PLAY_STREAK, newPlayStreak)
            editor.putInt(KEY_LONGEST_PLAY_STREAK, max(longestPlayStreak, newPlayStreak))
        }
    }
    
    /**
     * Update session count for today
     */
    private fun updateSessionCount() {
        val today = getCurrentDateString()
        val lastSessionDate = prefs.getString(KEY_LAST_SESSION_DATE, "") ?: ""
        
        val sessionsToday = if (lastSessionDate == today) {
            prefs.getInt(KEY_SESSIONS_TODAY, 0) + 1
        } else {
            1
        }
        
        prefs.edit()
            .putInt(KEY_SESSIONS_TODAY, sessionsToday)
            .putString(KEY_LAST_SESSION_DATE, today)
            .apply()
    }
    
    /**
     * Get comprehensive statistics
     */
    fun getStatistics(): GameStatistics {
        val gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0)
        val totalScore = prefs.getLong(KEY_TOTAL_SCORE, 0)
        val averageScore = if (gamesPlayed > 0) (totalScore / gamesPlayed).toInt() else 0
        
        return GameStatistics(
            // Basic stats
            gamesPlayed = gamesPlayed,
            totalScore = totalScore,
            highScore = prefs.getInt(KEY_HIGH_SCORE, 0),
            averageScore = averageScore,
            
            // Gameplay stats
            totalPipesPassed = prefs.getInt(KEY_TOTAL_PIPES_PASSED, 0),
            totalCoinsCollected = prefs.getInt(KEY_TOTAL_COINS_COLLECTED, 0),
            totalPowerUpsUsed = prefs.getInt(KEY_TOTAL_POWER_UPS_USED, 0),
            totalDeaths = prefs.getInt(KEY_TOTAL_DEATHS, 0),
            totalContinues = prefs.getInt(KEY_TOTAL_CONTINUES, 0),
            
            // Time stats
            totalPlaytimeSeconds = prefs.getLong(KEY_TOTAL_PLAYTIME, 0),
            averageSessionLength = if (gamesPlayed > 0) prefs.getLong(KEY_TOTAL_PLAYTIME, 0) / gamesPlayed else 0,
            
            // Streak stats
            currentWinStreak = prefs.getInt(KEY_CURRENT_WIN_STREAK, 0),
            longestWinStreak = prefs.getInt(KEY_LONGEST_WIN_STREAK, 0),
            currentPlayStreak = prefs.getInt(KEY_CURRENT_PLAY_STREAK, 0),
            longestPlayStreak = prefs.getInt(KEY_LONGEST_PLAY_STREAK, 0),
            
            // Power-up stats
            shieldUses = prefs.getInt(KEY_SHIELD_USES, 0),
            magnetUses = prefs.getInt(KEY_MAGNET_USES, 0),
            slowMotionUses = prefs.getInt(KEY_SLOW_MOTION_USES, 0),
            
            // Score distribution
            scores0to10 = prefs.getInt(KEY_SCORES_0_10, 0),
            scores11to25 = prefs.getInt(KEY_SCORES_11_25, 0),
            scores26to50 = prefs.getInt(KEY_SCORES_26_50, 0),
            scores51to100 = prefs.getInt(KEY_SCORES_51_100, 0),
            scores100Plus = prefs.getInt(KEY_SCORES_100_PLUS, 0),
            
            // Dates
            firstGameDate = prefs.getString(KEY_FIRST_GAME_DATE, ""),
            lastGameDate = prefs.getString(KEY_LAST_GAME_DATE, ""),
            sessionsToday = prefs.getInt(KEY_SESSIONS_TODAY, 0)
        )
    }
    
    /**
     * Get performance metrics for difficulty analysis
     */
    fun getPerformanceMetrics(): PerformanceMetrics {
        val stats = getStatistics()
        val survivalRate = if (stats.gamesPlayed > 0) {
            ((stats.gamesPlayed - stats.totalDeaths).toFloat() / stats.gamesPlayed * 100).toInt()
        } else 0
        
        val continueUsageRate = if (stats.totalDeaths > 0) {
            (stats.totalContinues.toFloat() / stats.totalDeaths * 100).toInt()
        } else 0
        
        val powerUpEfficiency = if (stats.totalPowerUpsUsed > 0) {
            (stats.totalScore.toFloat() / stats.totalPowerUpsUsed).toInt()
        } else 0
        
        return PerformanceMetrics(
            survivalRate = survivalRate,
            continueUsageRate = continueUsageRate,
            powerUpEfficiency = powerUpEfficiency,
            coinsPerGame = if (stats.gamesPlayed > 0) stats.totalCoinsCollected / stats.gamesPlayed else 0,
            pipesPerGame = if (stats.gamesPlayed > 0) stats.totalPipesPassed / stats.gamesPlayed else 0,
            improvementTrend = calculateImprovementTrend()
        )
    }
    
    /**
     * Calculate improvement trend (simplified)
     */
    private fun calculateImprovementTrend(): String {
        val recentGames = min(10, prefs.getInt(KEY_GAMES_PLAYED, 0))
        val highScore = prefs.getInt(KEY_HIGH_SCORE, 0)
        val averageScore = getStatistics().averageScore
        
        return when {
            recentGames < 5 -> "Getting Started"
            highScore > averageScore * 2 -> "Improving"
            highScore > averageScore * 1.5 -> "Steady"
            else -> "Consistent"
        }
    }
    
    /**
     * Reset all statistics (for testing)
     */
    fun resetStatistics() {
        prefs.edit().clear().apply()
        Log.d(TAG, "All statistics reset")
    }
    
    /**
     * Get current date as string
     */
    private fun getCurrentDateString(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
    
    /**
     * Get yesterday's date as string
     */
    private fun getYesterdayDateString(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}

/**
 * Comprehensive game statistics data class
 */
data class GameStatistics(
    // Basic game stats
    val gamesPlayed: Int,
    val totalScore: Long,
    val highScore: Int,
    val averageScore: Int,
    
    // Gameplay stats
    val totalPipesPassed: Int,
    val totalCoinsCollected: Int,
    val totalPowerUpsUsed: Int,
    val totalDeaths: Int,
    val totalContinues: Int,
    
    // Time stats
    val totalPlaytimeSeconds: Long,
    val averageSessionLength: Long,
    
    // Streak stats
    val currentWinStreak: Int,
    val longestWinStreak: Int,
    val currentPlayStreak: Int,
    val longestPlayStreak: Int,
    
    // Power-up specific stats
    val shieldUses: Int,
    val magnetUses: Int,
    val slowMotionUses: Int,
    
    // Score distribution
    val scores0to10: Int,
    val scores11to25: Int,
    val scores26to50: Int,
    val scores51to100: Int,
    val scores100Plus: Int,
    
    // Date tracking
    val firstGameDate: String?,
    val lastGameDate: String?,
    val sessionsToday: Int
) {
    val totalPlaytimeMinutes: Long
        get() = totalPlaytimeSeconds / 60
        
    val totalPlaytimeHours: Long
        get() = totalPlaytimeMinutes / 60
        
    val averageSessionMinutes: Long
        get() = averageSessionLength / 60
}

/**
 * Performance metrics for player analysis
 */
data class PerformanceMetrics(
    val survivalRate: Int, // Percentage of games survived without dying
    val continueUsageRate: Int, // Percentage of deaths where continue was used
    val powerUpEfficiency: Int, // Average score per power-up used
    val coinsPerGame: Int,
    val pipesPerGame: Int,
    val improvementTrend: String
)