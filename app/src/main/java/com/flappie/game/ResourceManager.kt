package com.flappie.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

/**
 * ResourceManager - Centralized resource loading with proper error handling
 * Handles loading of sprites, sounds, and other game resources
 */
class ResourceManager(private val context: Context) {
    
    companion object {
        private const val TAG = "ResourceManager"
    }
    
    /**
     * Safely load a bitmap resource by resource ID
     * Returns null if resource loading fails
     */
    private fun loadBitmapSafely(resourceId: Int): Bitmap? {
        return try {
            if (resourceId != 0) {
                val resourceName = try {
                    context.resources.getResourceName(resourceId)
                } catch (_: Exception) {
                    "unknown_$resourceId"
                }
                Log.d(TAG, "Loading bitmap resource: $resourceName")
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
                if (bitmap != null) {
                    Log.d(TAG, "Successfully loaded bitmap: $resourceName")
                } else {
                    Log.w(TAG, "Bitmap loaded as null: $resourceName")
                }
                bitmap
            } else {
                Log.w(TAG, "Invalid resource ID: $resourceId")
                null
            }
        } catch (e: Exception) {
            val resourceName = try {
                context.resources.getResourceName(resourceId)
            } catch (_: Exception) {
                "unknown_$resourceId"
            }
            Log.e(TAG, "Failed to load bitmap resource '$resourceName': ${e.message}", e)
            null
        }
    }
    
    /**
     * Safely validate a raw resource ID
     * Returns the ID if valid, 0 if invalid
     */
    private fun validateRawResourceId(resourceId: Int): Int {
        Log.d(TAG, "Validating resource ID: $resourceId")
        return try {
            if (resourceId != 0) {
                // Test if resource exists by attempting to get its name
                val resourceName = context.resources.getResourceName(resourceId)
                Log.d(TAG, "Validated raw resource: $resourceName (ID: $resourceId)")
                resourceId
            } else {
                Log.w(TAG, "Raw resource ID is 0 - resource likely doesn't exist")
                0
            }
        } catch (_: Exception) {
            Log.e(TAG, "Failed to validate raw resource ID '$resourceId'")
            0
        }
    }
    
    /**
     * Load all bird animation sprites
     */
    fun loadBirdSprites(): BirdSprites {
        return BirdSprites(
            neutral = loadBitmapSafely(R.drawable.bird_neutral),
            transition = loadBitmapSafely(R.drawable.bird_transition),
            wingsUp = loadBitmapSafely(R.drawable.bird_wings_up),
            wingsDown = loadBitmapSafely(R.drawable.bird_wings_down),
            sad = loadBitmapSafely(R.drawable.sadflappie)
        )
    }
    
    /**
     * Load heart sprites for lives display
     */
    fun loadHeartSprites(): HeartSprites {
        return HeartSprites(
            full = loadBitmapSafely(R.drawable.heart_full),
            empty = loadBitmapSafely(R.drawable.heart_empty)
        )
    }
    
    /**
     * Load all power-up sprites
     */
    fun loadPowerUpSprites(): PowerUpSprites {
        Log.d(TAG, "Loading PowerUp sprites...")
        val shield = loadBitmapSafely(R.drawable.powerup_shield)
        val slow = loadBitmapSafely(R.drawable.powerup_slow)
        val multiplier = loadBitmapSafely(R.drawable.powerup_2x)
        val magnet = loadBitmapSafely(R.drawable.powerup_magnet)
        val life = loadBitmapSafely(R.drawable.powerup_life)
        
        Log.d(TAG, "PowerUp sprites loaded - Shield: ${shield != null}, Slow: ${slow != null}, " +
                "Multiplier: ${multiplier != null}, Magnet: ${magnet != null}, Life: ${life != null}")
        
        return PowerUpSprites(
            shield = shield,
            slow = slow,
            multiplier = multiplier,
            magnet = magnet,
            life = life
        )
    }
    
    /**
     * Load coin sprites
     */
    fun loadCoinSprites(): CoinSprites {
        return CoinSprites(
            gold = loadBitmapSafely(R.drawable.coin_gold),
            diamond = loadBitmapSafely(R.drawable.coin_diamond)
        )
    }
    
    /**
     * Load all sound effect resource IDs
     */
    fun loadSoundEffectIds(): SoundEffectIds {
        Log.d(TAG, "Loading sound effect IDs...")
        Log.d(TAG, "R.raw.jump = ${R.raw.jump}")
        Log.d(TAG, "R.raw.score = ${R.raw.score}")
        Log.d(TAG, "R.raw.lose_life = ${R.raw.lose_life}")
        Log.d(TAG, "R.raw.game_over = ${R.raw.game_over}")
        Log.d(TAG, "R.raw.powerup = ${R.raw.powerup}")
        Log.d(TAG, "R.raw.coin = ${R.raw.coin}")
        
        return SoundEffectIds(
            jump = validateRawResourceId(R.raw.jump),
            score = validateRawResourceId(R.raw.score),
            loseLife = validateRawResourceId(R.raw.lose_life),
            gameOver = validateRawResourceId(R.raw.game_over),
            powerUp = validateRawResourceId(R.raw.powerup),
            coin = validateRawResourceId(R.raw.coin)
        )
    }
    
    /**
     * Load background music resource ID
     */
    fun loadBackgroundMusicId(): Int {
        Log.d(TAG, "R.raw.background_music = ${R.raw.background_music}")
        return validateRawResourceId(R.raw.background_music)
    }
    
    /**
     * Load hat sprite by name
     */
    fun loadHatSprite(hatName: String): Bitmap? {
        Log.d(TAG, "Loading hat sprite: $hatName")
        val resourceId = when (hatName) {
            "flappieimg" -> R.drawable.flappieimg
            "baseball_cap" -> R.drawable.hat_baseball_cap
            "crown" -> R.drawable.hat_crown
            "wizard_hat" -> R.drawable.hat_wizard
            "viking_helmet" -> R.drawable.hat_viking
            "royal_crown" -> R.drawable.hat_royal
            "party_hat" -> R.drawable.hat_party
            "pirate_hat" -> R.drawable.hat_pirate
            else -> {
                Log.w(TAG, "Unknown hat name: $hatName")
                return null
            }
        }
        return loadBitmapSafely(resourceId)
    }
}

/**
 * Data classes to hold loaded sprites
 */
data class BirdSprites(
    val neutral: Bitmap?,
    val transition: Bitmap?,
    val wingsUp: Bitmap?,
    val wingsDown: Bitmap?,
    val sad: Bitmap?
)

data class HeartSprites(
    val full: Bitmap?,
    val empty: Bitmap?
)

data class PowerUpSprites(
    val shield: Bitmap?,
    val slow: Bitmap?,
    val multiplier: Bitmap?,
    val magnet: Bitmap?,
    val life: Bitmap?
)

data class CoinSprites(
    val gold: Bitmap?,
    val diamond: Bitmap?
)

data class SoundEffectIds(
    val jump: Int,
    val score: Int,
    val loseLife: Int,
    val gameOver: Int,
    val powerUp: Int,
    val coin: Int
)