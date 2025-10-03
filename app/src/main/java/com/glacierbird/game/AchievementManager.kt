package com.glacierbird.game

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject

/**
 * AchievementManager - Handles achievement tracking, unlocking, and persistence
 */
class AchievementManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AchievementManager"
        private const val PREFS_NAME = "achievements_prefs"
        private const val KEY_ACHIEVEMENT_DATA = "achievement_data"
        private const val KEY_TOTAL_GAMES_PLAYED = "total_games_played"
        private const val KEY_TOTAL_PLAYTIME = "total_playtime_seconds"
        private const val KEY_TOTAL_COINS_COLLECTED = "total_coins_collected"
        private const val KEY_TOTAL_POWER_UPS_USED = "total_power_ups_used"
        private const val KEY_SHIELD_USES = "shield_uses"
        private const val KEY_SLOW_MOTION_USES = "slow_motion_uses"
        private const val KEY_DIAMOND_COINS_COLLECTED = "diamond_coins_collected"
        private const val KEY_LIVES_PURCHASED = "lives_purchased"
        private const val KEY_CONTINUES_USED = "continues_used"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var achievements: MutableList<Achievement> = mutableListOf()
    private var newlyUnlockedAchievements: MutableList<Achievement> = mutableListOf()
    
    // Statistics tracking
    private var totalGamesPlayed: Int = 0
    private var totalPlaytimeSeconds: Int = 0
    private var totalCoinsCollected: Int = 0
    private var totalPowerUpsUsed: Int = 0
    private var shieldUses: Int = 0
    private var slowMotionUses: Int = 0
    private var diamondCoinsCollected: Int = 0
    private var livesPurchased: Int = 0
    private var continuesUsed: Int = 0
    
    init {
        loadAchievements()
        loadStatistics()
    }
    
    /**
     * Load achievements from persistent storage
     */
    private fun loadAchievements() {
        achievements = Achievements.getAllAchievements().toMutableList()
        
        val savedData = prefs.getString(KEY_ACHIEVEMENT_DATA, "{}")
        try {
            val jsonData = JSONObject(savedData!!)
            achievements.forEach { achievement ->
                if (jsonData.has(achievement.id)) {
                    val achievementJson = jsonData.getJSONObject(achievement.id)
                    achievement.progress = achievementJson.optInt("progress", 0)
                    achievement.isUnlocked = achievementJson.optBoolean("unlocked", false)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load achievement data: ${e.message}")
        }
        
        Log.d(TAG, "Loaded ${achievements.size} achievements")
    }
    
    /**
     * Load statistics from persistent storage
     */
    private fun loadStatistics() {
        totalGamesPlayed = prefs.getInt(KEY_TOTAL_GAMES_PLAYED, 0)
        totalPlaytimeSeconds = prefs.getInt(KEY_TOTAL_PLAYTIME, 0)
        totalCoinsCollected = prefs.getInt(KEY_TOTAL_COINS_COLLECTED, 0)
        totalPowerUpsUsed = prefs.getInt(KEY_TOTAL_POWER_UPS_USED, 0)
        shieldUses = prefs.getInt(KEY_SHIELD_USES, 0)
        slowMotionUses = prefs.getInt(KEY_SLOW_MOTION_USES, 0)
        diamondCoinsCollected = prefs.getInt(KEY_DIAMOND_COINS_COLLECTED, 0)
        livesPurchased = prefs.getInt(KEY_LIVES_PURCHASED, 0)
        continuesUsed = prefs.getInt(KEY_CONTINUES_USED, 0)
    }
    
    /**
     * Save achievements to persistent storage
     */
    private fun saveAchievements() {
        val jsonData = JSONObject()
        achievements.forEach { achievement ->
            val achievementJson = JSONObject().apply {
                put("progress", achievement.progress)
                put("unlocked", achievement.isUnlocked)
            }
            jsonData.put(achievement.id, achievementJson)
        }
        
        prefs.edit()
            .putString(KEY_ACHIEVEMENT_DATA, jsonData.toString())
            .apply()
    }
    
    /**
     * Save statistics to persistent storage
     */
    private fun saveStatistics() {
        prefs.edit()
            .putInt(KEY_TOTAL_GAMES_PLAYED, totalGamesPlayed)
            .putInt(KEY_TOTAL_PLAYTIME, totalPlaytimeSeconds)
            .putInt(KEY_TOTAL_COINS_COLLECTED, totalCoinsCollected)
            .putInt(KEY_TOTAL_POWER_UPS_USED, totalPowerUpsUsed)
            .putInt(KEY_SHIELD_USES, shieldUses)
            .putInt(KEY_SLOW_MOTION_USES, slowMotionUses)
            .putInt(KEY_DIAMOND_COINS_COLLECTED, diamondCoinsCollected)
            .putInt(KEY_LIVES_PURCHASED, livesPurchased)
            .putInt(KEY_CONTINUES_USED, continuesUsed)
            .apply()
    }
    
    // ========== ACHIEVEMENT TRACKING METHODS ==========
    
    /**
     * Track a score achievement
     */
    fun trackScore(score: Int): List<Achievement> {
        val unlockedAchievements = mutableListOf<Achievement>()
        
        achievements.filter { it.type == AchievementType.SCORE }.forEach { achievement ->
            if (achievement.updateProgress(score)) {
                unlockedAchievements.add(achievement)
                Log.d(TAG, "Achievement unlocked: ${achievement.title}")
            }
        }
        
        if (unlockedAchievements.isNotEmpty()) {
            newlyUnlockedAchievements.addAll(unlockedAchievements)
            saveAchievements()
        }
        
        return unlockedAchievements
    }
    
    /**
     * Track coin collection
     */
    fun trackCoinCollected(isGold: Boolean = true): List<Achievement> {
        totalCoinsCollected++
        if (!isGold) diamondCoinsCollected++
        
        val unlockedAchievements = mutableListOf<Achievement>()
        
        // Update coin collection achievements
        achievements.filter { it.type == AchievementType.COLLECTION }.forEach { achievement ->
            val newProgress = when (achievement.id) {
                "coin_collector", "treasure_hunter", "rich_bird" -> totalCoinsCollected
                "diamond_collector" -> diamondCoinsCollected
                else -> achievement.progress
            }
            
            if (achievement.updateProgress(newProgress)) {
                unlockedAchievements.add(achievement)
            }
        }
        
        saveStatistics()
        if (unlockedAchievements.isNotEmpty()) {
            newlyUnlockedAchievements.addAll(unlockedAchievements)
            saveAchievements()
        }
        
        return unlockedAchievements
    }
    
    /**
     * Track power-up usage
     */
    fun trackPowerUpUsed(powerUpType: PowerUpType): List<Achievement> {
        totalPowerUpsUsed++
        
        when (powerUpType) {
            PowerUpType.SHIELD -> shieldUses++
            PowerUpType.SLOW_MOTION -> slowMotionUses++
            else -> {}
        }
        
        val unlockedAchievements = mutableListOf<Achievement>()
        
        achievements.filter { it.type == AchievementType.POWER_UP }.forEach { achievement ->
            val newProgress = when (achievement.id) {
                "power_user" -> totalPowerUpsUsed
                "shield_master" -> shieldUses
                "time_manipulator" -> slowMotionUses
                else -> achievement.progress
            }
            
            if (achievement.updateProgress(newProgress)) {
                unlockedAchievements.add(achievement)
            }
        }
        
        saveStatistics()
        if (unlockedAchievements.isNotEmpty()) {
            newlyUnlockedAchievements.addAll(unlockedAchievements)
            saveAchievements()
        }
        
        return unlockedAchievements
    }
    
    /**
     * Track gameplay session
     */
    fun trackGamePlayed(playtimeSeconds: Int): List<Achievement> {
        totalGamesPlayed++
        totalPlaytimeSeconds += playtimeSeconds
        
        val unlockedAchievements = mutableListOf<Achievement>()
        
        achievements.forEach { achievement ->
            val newProgress = when (achievement.id) {
                "persistent" -> totalGamesPlayed
                "survivor", "marathon_flyer" -> totalPlaytimeSeconds
                else -> achievement.progress
            }
            
            if (newProgress != achievement.progress && achievement.updateProgress(newProgress)) {
                unlockedAchievements.add(achievement)
            }
        }
        
        saveStatistics()
        if (unlockedAchievements.isNotEmpty()) {
            newlyUnlockedAchievements.addAll(unlockedAchievements)
            saveAchievements()
        }
        
        return unlockedAchievements
    }
    
    /**
     * Track life purchase
     */
    fun trackLifePurchased(): List<Achievement> {
        livesPurchased++
        
        val unlockedAchievements = mutableListOf<Achievement>()
        val achievement = achievements.find { it.id == "shopaholic" }
        
        if (achievement?.updateProgress(livesPurchased) == true) {
            unlockedAchievements.add(achievement)
            newlyUnlockedAchievements.add(achievement)
            saveAchievements()
        }
        
        saveStatistics()
        return unlockedAchievements
    }
    
    /**
     * Track continue usage
     */
    fun trackContinueUsed(): List<Achievement> {
        continuesUsed++
        
        val unlockedAchievements = mutableListOf<Achievement>()
        val achievement = achievements.find { it.id == "comeback_king" }
        
        if (achievement?.updateProgress(continuesUsed) == true) {
            unlockedAchievements.add(achievement)
            newlyUnlockedAchievements.add(achievement)
            saveAchievements()
        }
        
        saveStatistics()
        return unlockedAchievements
    }
    
    // ========== GETTER METHODS ==========
    
    fun getAllAchievements(): List<Achievement> = achievements.toList()
    
    fun getUnlockedAchievements(): List<Achievement> = achievements.filter { it.isUnlocked }
    
    fun getNewlyUnlockedAchievements(): List<Achievement> {
        val result = newlyUnlockedAchievements.toList()
        newlyUnlockedAchievements.clear()
        return result
    }
    
    fun getTotalCoinsEarned(): Int {
        return achievements.filter { it.isUnlocked }.sumOf { it.rewardCoins }
    }
    
    fun getAchievementProgress(): Pair<Int, Int> {
        val unlocked = achievements.count { it.isUnlocked }
        val total = achievements.size
        return Pair(unlocked, total)
    }
    
    // Statistics getters
    fun getTotalGamesPlayed(): Int = totalGamesPlayed
    fun getTotalPlaytime(): Int = totalPlaytimeSeconds
    fun getTotalCoinsCollected(): Int = totalCoinsCollected
    fun getTotalPowerUpsUsed(): Int = totalPowerUpsUsed
}