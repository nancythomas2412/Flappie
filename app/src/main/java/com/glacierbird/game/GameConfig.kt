package com.glacierbird.game

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * GameConfig - Centralized game configuration and settings management
 * Handles loading and saving of game settings with proper defaults
 */
data class GameConfig(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val difficulty: DifficultyLevel = DifficultyLevel.NORMAL,
    val bestScore: Int = 0,
    val totalCoins: Int = 0,
    val gamesPlayed: Int = 0,
    val totalJumps: Int = 0,
    val totalCollectiblesCollected: Int = 0,
    val playerLives: Int = 3,
    val savedExtraLives: Int = 0  // Saved extra life power-ups when lives were full
) {
    companion object {
        private const val PREFS_NAME = "GlacierBirdPrefs"
        private const val KEY_SOUND_ENABLED = "soundEnabled"
        private const val KEY_MUSIC_ENABLED = "musicEnabled"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_BEST_SCORE = "bestScore"
        private const val KEY_TOTAL_COINS = "totalCoins"
        private const val KEY_GAMES_PLAYED = "gamesPlayed"
        private const val KEY_TOTAL_JUMPS = "totalJumps"
        private const val KEY_TOTAL_COLLECTIBLES = "totalCollectiblesCollected"
        private const val KEY_PLAYER_LIVES = "playerLives"
        private const val KEY_SAVED_EXTRA_LIVES = "savedExtraLives"
        
        /**
         * Load game configuration from SharedPreferences
         * @param context Android context
         * @return GameConfig with loaded or default values
         */
        fun load(context: Context): GameConfig {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            
            return GameConfig(
                soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
                musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true),
                difficulty = DifficultyLevel.valueOf(
                    prefs.getString(KEY_DIFFICULTY, DifficultyLevel.NORMAL.name) 
                        ?: DifficultyLevel.NORMAL.name
                ),
                bestScore = prefs.getInt(KEY_BEST_SCORE, 0),
                totalCoins = prefs.getInt(KEY_TOTAL_COINS, 0),
                gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0),
                totalJumps = prefs.getInt(KEY_TOTAL_JUMPS, 0),
                totalCollectiblesCollected = prefs.getInt(KEY_TOTAL_COLLECTIBLES, 0),
                playerLives = prefs.getInt(KEY_PLAYER_LIVES, 3),
                savedExtraLives = prefs.getInt(KEY_SAVED_EXTRA_LIVES, 0)
            )
        }
        
        /**
         * Save game configuration to SharedPreferences
         * @param context Android context
         * @param config GameConfig to save
         */
        fun save(context: Context, config: GameConfig) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            
            prefs.edit {
                putBoolean(KEY_SOUND_ENABLED, config.soundEnabled)
                putBoolean(KEY_MUSIC_ENABLED, config.musicEnabled)
                putString(KEY_DIFFICULTY, config.difficulty.name)
                putInt(KEY_BEST_SCORE, config.bestScore)
                putInt(KEY_TOTAL_COINS, config.totalCoins)
                putInt(KEY_GAMES_PLAYED, config.gamesPlayed)
                putInt(KEY_TOTAL_JUMPS, config.totalJumps)
                putInt(KEY_TOTAL_COLLECTIBLES, config.totalCollectiblesCollected)
                putInt(KEY_PLAYER_LIVES, config.playerLives)
                putInt(KEY_SAVED_EXTRA_LIVES, config.savedExtraLives)
            }
        }
    }
    
    /**
     * Create a new config with updated audio settings
     */
    fun withAudioSettings(soundEnabled: Boolean, musicEnabled: Boolean): GameConfig {
        return copy(soundEnabled = soundEnabled, musicEnabled = musicEnabled)
    }
    
    /**
     * Create a new config with updated score (if it's a new best score)
     */
    fun withPotentialNewBestScore(newScore: Int): GameConfig {
        return if (newScore > bestScore) {
            copy(bestScore = newScore)
        } else {
            this
        }
    }
    
    /**
     * Create a new config with updated coins
     */
    fun withCoins(newTotalCoins: Int): GameConfig {
        return copy(totalCoins = newTotalCoins)
    }
    
    /**
     * Create a new config with incremented game statistics
     */
    fun withGameCompleted(jumps: Int, collectiblesCollected: Int): GameConfig {
        return copy(
            gamesPlayed = gamesPlayed + 1,
            totalJumps = totalJumps + jumps,
            totalCollectiblesCollected = totalCollectiblesCollected + collectiblesCollected
        )
    }
    
    /**
     * Create a new config with updated player lives
     */
    fun withLives(newLives: Int): GameConfig {
        return copy(playerLives = maxOf(0, minOf(newLives, 3)))
    }
    
    /**
     * Create a new config with one life lost
     */
    fun withLifeLost(): GameConfig {
        return copy(playerLives = maxOf(0, playerLives - 1))
    }
    
    /**
     * Get difficulty multipliers for game balance
     */
    fun getDifficultyMultipliers(): DifficultyMultipliers {
        return when (difficulty) {
            DifficultyLevel.EASY -> DifficultyMultipliers(
                pipeSpeedMultiplier = 0.7f,
                gapSizeMultiplier = 1.3f,
                powerUpSpawnMultiplier = 1.5f
            )
            DifficultyLevel.NORMAL -> DifficultyMultipliers(
                pipeSpeedMultiplier = 1.0f,
                gapSizeMultiplier = 1.0f,
                powerUpSpawnMultiplier = 1.0f
            )
            DifficultyLevel.HARD -> DifficultyMultipliers(
                pipeSpeedMultiplier = 1.3f,
                gapSizeMultiplier = 0.8f,
                powerUpSpawnMultiplier = 0.7f
            )
        }
    }
}

/**
 * Available difficulty levels
 */
enum class DifficultyLevel(val displayName: String) {
    EASY("Easy"),
    NORMAL("Normal"),
    HARD("Hard")
}

/**
 * Difficulty multipliers for game balance
 */
data class DifficultyMultipliers(
    val pipeSpeedMultiplier: Float,
    val gapSizeMultiplier: Float,
    val powerUpSpawnMultiplier: Float
)

/**
 * GameConfigManager - Helper class to manage config updates during gameplay
 * Provides convenient methods for updating configuration without directly exposing SharedPreferences
 */
class GameConfigManager(private val context: Context) {
    
    private var currentConfig: GameConfig = GameConfig.load(context)
    private val heartRefillManager = HeartRefillManager(context)
    
    /**
     * Get the current configuration
     */
    fun getCurrentConfig(): GameConfig = currentConfig
    
    /**
     * Update audio settings and save
     */
    fun updateAudioSettings(soundEnabled: Boolean, musicEnabled: Boolean) {
        currentConfig = currentConfig.withAudioSettings(soundEnabled, musicEnabled)
        GameConfig.save(context, currentConfig)
    }
    
    /**
     * Update best score if the new score is higher
     */
    fun updateBestScore(newScore: Int): Boolean {
        val oldBestScore = currentConfig.bestScore
        currentConfig = currentConfig.withPotentialNewBestScore(newScore)
        
        if (currentConfig.bestScore != oldBestScore) {
            GameConfig.save(context, currentConfig)
            return true // New best score achieved
        }
        return false
    }
    
    /**
     * Update total coins
     */
    fun updateCoins(newTotalCoins: Int) {
        currentConfig = currentConfig.withCoins(newTotalCoins)
        GameConfig.save(context, currentConfig)
    }
    
    /**
     * Record a completed game with statistics
     */
    fun recordGameCompleted(jumps: Int, collectiblesCollected: Int) {
        currentConfig = currentConfig.withGameCompleted(jumps, collectiblesCollected)
        GameConfig.save(context, currentConfig)
    }
    
    /**
     * Get current difficulty multipliers
     */
    fun getDifficultyMultipliers(): DifficultyMultipliers {
        return currentConfig.getDifficultyMultipliers()
    }
    
    /**
     * Update player lives with heart refill system
     */
    fun updateLivesWithRefill(): Int {
        val updatedLives = heartRefillManager.updateHearts(currentConfig.playerLives)
        if (updatedLives != currentConfig.playerLives) {
            currentConfig = currentConfig.withLives(updatedLives)
            GameConfig.save(context, currentConfig)
        }
        return updatedLives
    }
    
    /**
     * Lose a life and start heart refill timer if needed
     */
    fun loseLife(): Int {
        val oldLives = currentConfig.playerLives
        currentConfig = currentConfig.withLifeLost()
        GameConfig.save(context, currentConfig)
        
        android.util.Log.d("HeartSystem", "Lost life: $oldLives -> ${currentConfig.playerLives}")
        
        // Start refill timer if this was the last life
        if (currentConfig.playerLives == 0) {
            android.util.Log.d("HeartSystem", "Starting heart refill timer")
            heartRefillManager.startRefillTimer(0)
        }
        
        return currentConfig.playerLives
    }
    
    /**
     * Get time remaining until next heart refill
     */
    fun getTimeUntilNextHeartRefill(): Long {
        return heartRefillManager.getTimeUntilNextRefill(currentConfig.playerLives)
    }
    
    /**
     * Format heart refill time as mm:ss
     */
    fun formatHeartRefillTime(): String {
        val timeRemaining = getTimeUntilNextHeartRefill()
        return heartRefillManager.formatTimeRemaining(timeRemaining)
    }
    
    /**
     * Check if heart refill timer is active
     */
    fun isHeartRefillActive(): Boolean {
        return heartRefillManager.isRefillTimerActive(currentConfig.playerLives)
    }
    
    /**
     * Check if player can play (has at least 1 heart) - with heart refill check
     */
    fun canPlay(): Boolean {
        return updateLivesWithRefill() > 0
    }
    
    /**
     * Check if player can play without triggering heart refill (for gameplay logic)
     */
    fun canPlayWithoutRefill(): Boolean {
        return currentConfig.playerLives > 0
    }
    
    /**
     * Set player lives (for starting fresh games)
     */
    fun setLives(lives: Int) {
        val oldLives = currentConfig.playerLives
        currentConfig = currentConfig.withLives(lives)
        GameConfig.save(context, currentConfig)

        // Integrate with heart refill timer when lives change
        if (lives < GameConstants.MAX_LIVES) {
            // Start/restart timer for remaining hearts
            heartRefillManager.startRefillTimer(lives)
        } else {
            // Hearts are full, no timer needed
            heartRefillManager.resetRefillTimer()
        }

        android.util.Log.d("HeartSystem", "Set lives: $oldLives -> $lives, timer updated")
    }

    /**
     * Add a saved extra life when lives are full
     */
    fun addSavedExtraLife() {
        currentConfig = currentConfig.copy(savedExtraLives = currentConfig.savedExtraLives + 1)
        GameConfig.save(context, currentConfig)
        android.util.Log.d("ExtraLifeSystem", "Saved extra life: ${currentConfig.savedExtraLives - 1} -> ${currentConfig.savedExtraLives}")
    }

    /**
     * Use a saved extra life when all regular lives are lost
     * @return true if a saved life was used, false if none available
     */
    fun useSavedExtraLife(): Boolean {
        if (currentConfig.savedExtraLives > 0 && currentConfig.playerLives == 0) {
            currentConfig = currentConfig.copy(
                savedExtraLives = currentConfig.savedExtraLives - 1,
                playerLives = 1 // Give one life back
            )
            GameConfig.save(context, currentConfig)
            android.util.Log.d("ExtraLifeSystem", "Used saved extra life: ${currentConfig.savedExtraLives + 1} -> ${currentConfig.savedExtraLives}, lives: 0 -> 1")
            return true
        }
        return false
    }

    /**
     * Get number of saved extra lives
     */
    fun getSavedExtraLives(): Int = currentConfig.savedExtraLives

    /**
     * Check if player has any saved extra lives
     */
    fun hasSavedExtraLives(): Boolean = currentConfig.savedExtraLives > 0
}