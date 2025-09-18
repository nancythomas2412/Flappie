package com.flappie.game

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.edit

/**
 * HatManager - Manages bird hat collection and selection
 * Achievement-based cosmetic system
 */
class HatManager(private val context: Context) {
    
    companion object {
        private const val TAG = "HatManager"
        private const val PREFS_NAME = "hat_prefs"
        private const val KEY_SELECTED_HAT = "selected_hat"
        private const val KEY_UNLOCKED_HATS = "unlocked_hats"
        
        // Default unlocked hats (no hat is always available)
        private val DEFAULT_UNLOCKED = setOf(HatType.NONE.id)
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val resourceManager = ResourceManager(context)
    
    /**
     * Get currently selected hat
     */
    fun getSelectedHat(): HatType {
        val selectedId = prefs.getString(KEY_SELECTED_HAT, HatType.NONE.id)
        return HatType.values().find { it.id == selectedId } ?: HatType.NONE
    }
    
    /**
     * Set selected hat
     */
    fun selectHat(hatType: HatType): Boolean {
        if (!isUnlocked(hatType)) {
            Log.w(TAG, "Attempted to select locked hat: ${hatType.id}")
            return false
        }
        
        prefs.edit {
            putString(KEY_SELECTED_HAT, hatType.id)
        }
        
        Log.d(TAG, "Selected hat: ${hatType.displayName}")
        return true
    }
    
    /**
     * Check if a hat is unlocked
     */
    fun isUnlocked(hatType: HatType): Boolean {
        val unlockedHats = getUnlockedHats()
        return unlockedHats.contains(hatType.id)
    }
    
    /**
     * Unlock a hat through achievement
     */
    fun unlockHat(hatType: HatType): Boolean {
        if (isUnlocked(hatType)) {
            return false // Already unlocked
        }
        
        val currentUnlocked = getUnlockedHats().toMutableSet()
        currentUnlocked.add(hatType.id)
        
        prefs.edit {
            putStringSet(KEY_UNLOCKED_HATS, currentUnlocked)
        }
        
        Log.d(TAG, "Unlocked hat: ${hatType.displayName}")
        return true
    }
    
    /**
     * Check and unlock hats based on high score
     */
    fun checkAndUnlockHatsForScore(highScore: Int): List<HatType> {
        val newlyUnlocked = mutableListOf<HatType>()
        
        HatType.values().forEach { hatType ->
            if (hatType.unlockMethod == UnlockMethod.ACHIEVEMENT && !isUnlocked(hatType)) {
                val requiredScore = getRequiredScoreForHat(hatType)
                if (highScore >= requiredScore) {
                    if (unlockHat(hatType)) {
                        newlyUnlocked.add(hatType)
                    }
                }
            }
        }
        
        return newlyUnlocked
    }
    
    /**
     * Get required score for achievement-based hat
     */
    private fun getRequiredScoreForHat(hatType: HatType): Int {
        return when (hatType) {
            HatType.BASEBALL_CAP -> 1000
            HatType.CROWN -> 2500
            HatType.WIZARD_HAT -> 5000
            HatType.VIKING_HELMET -> 10000
            HatType.ROYAL_CROWN -> 25000
            HatType.PARTY_HAT -> 15000
            HatType.PIRATE_HAT -> 7500
            else -> 0
        }
    }
    
    /**
     * Get all unlocked hats
     */
    fun getUnlockedHats(): Set<String> {
        return prefs.getStringSet(KEY_UNLOCKED_HATS, DEFAULT_UNLOCKED)?.toSet() ?: DEFAULT_UNLOCKED
    }
    
    /**
     * Get all available hats with unlock status
     */
    fun getAllHatsWithStatus(): List<HatInfo> {
        val unlockedHats = getUnlockedHats()
        val selectedHat = getSelectedHat()
        
        return HatType.values().map { hatType ->
            HatInfo(
                type = hatType,
                isUnlocked = unlockedHats.contains(hatType.id),
                isSelected = hatType == selectedHat,
                sprite = loadHatSprite(hatType)
            )
        }
    }
    
    /**
     * Load sprite for specific hat
     */
    private fun loadHatSprite(hatType: HatType): Bitmap? {
        return try {
            when (hatType) {
                HatType.NONE -> resourceManager.loadHatSprite("flappieimg") // Use flappieimg.png for no hat
                HatType.BASEBALL_CAP -> resourceManager.loadHatSprite("baseball_cap")
                HatType.CROWN -> resourceManager.loadHatSprite("crown")
                HatType.WIZARD_HAT -> resourceManager.loadHatSprite("wizard_hat")
                HatType.VIKING_HELMET -> resourceManager.loadHatSprite("viking_helmet")
                HatType.ROYAL_CROWN -> resourceManager.loadHatSprite("royal_crown")
                HatType.PARTY_HAT -> resourceManager.loadHatSprite("party_hat")
                HatType.PIRATE_HAT -> resourceManager.loadHatSprite("pirate_hat")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load sprite for ${hatType.id}: ${e.message}")
            null
        }
    }
    
    /**
     * Get current hat sprite based on selected hat
     */
    fun getCurrentHatSprite(): Bitmap? {
        val selectedHat = getSelectedHat()
        return loadHatSprite(selectedHat)
    }
    
    /**
     * Get current bird sprites (always same, hats are overlays)
     */
    fun getCurrentBirdSprites(): BirdSprites? {
        return try {
            resourceManager.loadBirdSprites()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load bird sprites: ${e.message}")
            null
        }
    }
    
    /**
     * Get hats available for achievement unlock (no purchases)
     */
    fun getLockedHatsWithRequirements(): List<LockedHat> {
        val unlockedHats = getUnlockedHats()
        
        return HatType.values().filter { !unlockedHats.contains(it.id) }.map { hatType ->
            LockedHat(
                hatInfo = HatInfo(
                    type = hatType,
                    isUnlocked = false,
                    isSelected = false,
                    sprite = loadHatSprite(hatType)
                ),
                requiredScore = getRequiredScoreForHat(hatType)
            )
        }
    }
    
    /**
     * Process hat unlock based on achievement (no purchases)
     */
    fun processHatUnlock(hatType: HatType): Boolean {
        return unlockHat(hatType)
    }
    
    /**
     * Get statistics about hat collection
     */
    fun getHatCollectionStats(): HatCollectionStats {
        val totalHats = HatType.values().size
        val unlockedCount = getUnlockedHats().size
        val selectedHat = getSelectedHat()
        
        return HatCollectionStats(
            totalHats = totalHats,
            unlockedHats = unlockedCount,
            lockedHats = totalHats - unlockedCount,
            selectedHat = selectedHat,
            collectionPercentage = (unlockedCount * 100) / totalHats
        )
    }
    
    /**
     * Reset all hat data (for testing)
     */
    fun resetHatData() {
        prefs.edit { clear() }
        Log.d(TAG, "All hat data reset")
    }
}

/**
 * Hat types with metadata
 */
enum class HatType(
    val id: String,
    val displayName: String,
    val description: String,
    val rarity: HatRarity,
    val price: String,
    val billingProductId: String,
    val unlockMethod: UnlockMethod
) {
    NONE(
        id = "none",
        displayName = "No Hat",
        description = "The classic look - no hat needed!",
        rarity = HatRarity.COMMON,
        price = "Free",
        billingProductId = "",
        unlockMethod = UnlockMethod.DEFAULT
    ),
    BASEBALL_CAP(
        id = "baseball_cap",
        displayName = "Baseball Cap",
        description = "Reach 1000 points to unlock this sporty cap",
        rarity = HatRarity.COMMON,
        price = "Score: 1000",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    ),
    CROWN(
        id = "crown",
        displayName = "Crown",
        description = "Reach 2500 points to unlock this golden crown",
        rarity = HatRarity.RARE,
        price = "Score: 2500",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    ),
    WIZARD_HAT(
        id = "wizard_hat",
        displayName = "Wizard Hat",
        description = "Reach 5000 points to unlock this magical hat",
        rarity = HatRarity.EPIC,
        price = "Score: 5000",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    ),
    VIKING_HELMET(
        id = "viking_helmet",
        displayName = "Viking Helmet",
        description = "Reach 10000 points to unlock this warrior helmet",
        rarity = HatRarity.EPIC,
        price = "Score: 10000",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    ),
    ROYAL_CROWN(
        id = "royal_crown",
        displayName = "Royal Crown",
        description = "Reach 25000 points to unlock this majestic crown",
        rarity = HatRarity.LEGENDARY,
        price = "Score: 25000",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    ),
    PARTY_HAT(
        id = "party_hat",
        displayName = "Party Hat",
        description = "Reach 15000 points to unlock this festive hat",
        rarity = HatRarity.RARE,
        price = "Score: 15000",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    ),
    PIRATE_HAT(
        id = "pirate_hat",
        displayName = "Pirate Hat",
        description = "Reach 7500 points to unlock this adventurous hat",
        rarity = HatRarity.RARE,
        price = "Score: 7500",
        billingProductId = "",
        unlockMethod = UnlockMethod.ACHIEVEMENT
    )
}

/**
 * Hat rarity levels
 */
enum class HatRarity(val displayName: String, val color: String) {
    COMMON("Common", "#95A5A6"),
    RARE("Rare", "#3498DB"),
    EPIC("Epic", "#9B59B6"),
    LEGENDARY("Legendary", "#F39C12")
}

/**
 * How skins can be unlocked
 */
enum class UnlockMethod {
    DEFAULT,      // Available by default
    ACHIEVEMENT,  // Unlock through score achievements
    SEASONAL      // Limited time events
}

/**
 * Hat information with unlock status
 */
data class HatInfo(
    val type: HatType,
    val isUnlocked: Boolean,
    val isSelected: Boolean,
    val sprite: Bitmap?
)

/**
 * Locked hat with achievement requirement
 */
data class LockedHat(
    val hatInfo: HatInfo,
    val requiredScore: Int
)

/**
 * Hat collection statistics
 */
data class HatCollectionStats(
    val totalHats: Int,
    val unlockedHats: Int,
    val lockedHats: Int,
    val selectedHat: HatType,
    val collectionPercentage: Int
)