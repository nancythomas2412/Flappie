package com.flappie.game

/**
 * GameConstants - Centralized constants for the Flappie game
 * This reduces magic numbers and makes configuration easier
 */
object GameConstants {
    
    // Game Thread Constants
    const val TARGET_FPS = 60
    const val TARGET_FRAME_TIME = 1000L / TARGET_FPS
    
    // Bird Physics Constants - Now density-aware
    val BIRD_RADIUS get() = DensityUtils.GameElements.getBirdRadius()
    val BIRD_COLLISION_RADIUS get() = DensityUtils.GameElements.getBirdCollisionRadius()
    val BIRD_GRAVITY get() = DensityUtils.GameElements.getBirdGravity()
    val BIRD_JUMP_STRENGTH get() = DensityUtils.GameElements.getBirdJump()
    val BIRD_MAX_VELOCITY get() = DensityUtils.GameElements.getBirdMaxVelocity()
    
    // Bird Animation Constants
    const val BIRD_ANIMATION_SPEED = 5
    const val BIRD_ANIMATION_FRAMES = 5
    const val BIRD_TRAIL_LIFETIME = 25
    const val BIRD_TRAIL_PARTICLES = 3
    
    // Lives System Constants
    const val MAX_LIVES = 3
    const val INVULNERABLE_DURATION = 60
    
    // Screen Layout Constants - Now density-aware
    val GROUND_HEIGHT get() = DensityUtils.scale(50f)
    const val HEART_SIZE_RATIO = 0.08f  // Relative to screen width
    const val HEART_SPACING_RATIO = 0.09f
    val HEART_MARGIN_X get() = DensityUtils.UI.getSpacingLarge()
    val HEART_MARGIN_Y get() = DensityUtils.Layout.getTopSafeArea() + DensityUtils.UI.getSpacingSmall()

    // UI Button Constants
    const val BUTTON_SIZE_RATIO = 0.12f  // Relative to screen width
    const val BUTTON_SPACING_RATIO = 0.14f
    val BUTTON_MARGIN_X get() = DensityUtils.UI.getSpacingLarge()
    val BUTTON_MARGIN_Y get() = DensityUtils.UI.getSpacingMedium()
    
    // Pipe Constants - Now density-aware
    val PIPE_WIDTH get() = DensityUtils.GameElements.getPipeWidth()
    val PIPE_BASE_GAP_SIZE get() = DensityUtils.GameElements.getPipeGapSize()
    val PIPE_MIN_GAP_SIZE get() = DensityUtils.GameElements.getPipeMinGap()
    const val PIPE_GAP_REDUCTION_RATE = 1.5f
    val PIPE_BASE_SPEED get() = DensityUtils.GameElements.getPipeSpeed()
    const val PIPE_MAX_SPEED = 7f
    const val PIPE_SPEED_INCREASE = 0.1f
    const val PIPE_SCORE_INTERVAL = 15  // Points needed for difficulty increase
    const val PIPE_MIN_GAP_TOP = 200f
    const val PIPE_MIN_GAP_BOTTOM = 200f
    const val PIPE_CAP_WIDTH = 10f
    const val PIPE_CAP_HEIGHT = 30f
    const val PIPE_COLLISION_FORGIVENESS = 5f
    
    // Pipe Spawn Timing Constants - Increased spacing for easier gameplay
    object PipeSpacing {
        const val EASY = 300      // 0-150 points (+50 frames)
        const val NORMAL = 290    // 151-300 points (+60 frames)
        const val MEDIUM = 250    // 301-600 points (+40 frames)
        const val HARD = 235      // 601-900 points (+45 frames)
        const val HARDER = 200    // 901-1200 points (+30 frames)
        const val HARDEST = 180   // 1201-1500 points (+30 frames)
        const val INSANE = 170    // 1500+ points (+40 frames)
    }
    
    // Collectibles Constants - Now density-aware
    val POWERUP_SIZE get() = DensityUtils.GameElements.getPowerUpSize()
    val COIN_SIZE get() = DensityUtils.GameElements.getCoinSize()
    const val COLLECTIBLE_SPEED = 2f
    const val COLLECTIBLE_ATTRACTION_RANGE = 250f
    const val COIN_MAGNET_RANGE = 660f  // Increased by 10% for stronger magnetic pull
    const val COLLECTIBLE_ATTRACTION_FORCE = 4f
    const val COIN_MAGNET_FORCE = 22f  // Increased by 10% for stronger magnetic force
    const val COLLECTIBLE_SPAWN_SCORE_THRESHOLD = 60
    
    // Power-up Duration Constants (in frames)
    object PowerUpDuration {
        const val SHIELD = 300
        const val SLOW_MOTION = 480
        const val SCORE_MULTIPLIER = 600
        const val MAGNET = 720
        const val EXTRA_LIFE = 0
    }
    
    // Spawn Timing Constants
    const val POWERUP_SPAWN_MIN = 3600  // 60 seconds at 60fps
    const val POWERUP_SPAWN_MAX = 5401  // 90 seconds at 60fps
    const val COIN_SPAWN_DELAY = 900    // 15 seconds at 60fps
    
    // Audio Constants
    const val SOUND_VOLUME = 0.7f
    const val SCORE_VOLUME = 1.0f
    const val MUSIC_VOLUME = 0.35f
    const val MAX_AUDIO_STREAMS = 6
    
    // Score Constants
    const val SCORE_PER_PIPE = 5
    const val SCORE_MULTIPLIER_BONUS = 10
    const val COIN_GOLD_VALUE = 10
    const val COIN_DIAMOND_VALUE = 50
    const val EXTRA_LIFE_COIN_BONUS = 50
    const val EXTRA_LIFE_SCORE_BONUS = 25
    
    // Color Constants (as hex strings for easier modification)
    const val BACKGROUND_COLOR = "#87CEEB"  // Sky blue
    const val GROUND_COLOR = "#228B22"      // Forest green
    const val PIPE_COLOR = "#32CD32"        // Lime green
    const val PIPE_OUTLINE_COLOR = "#228B22" // Dark green
    const val BIRD_COLOR = "#FFD700"        // Gold
    const val SHIELD_COLOR = "#4169E1"      // Royal blue
    
    // UI Color Constants
    const val BUTTON_COLOR = "#80FFFFFF"     // Semi-transparent white
    const val RED_BUTTON_COLOR = "#FF4444"   // Red
    const val PANEL_COLOR = "#F5F5F5"        // Light gray
    const val HEADER_COLOR = "#2C3E50"       // Dark blue-gray
    const val COIN_GOLD_COLOR = "#FFD700"    // Gold
    const val COIN_DIAMOND_COLOR = "#00BFFF" // Deep sky blue
    
    // Animation Constants
    const val FLOAT_ANIMATION_SPEED = 0.03f
    const val SPIN_ANIMATION_SPEED = 0.15f
    const val GLOW_ANIMATION_SPEED = 0.1f
    const val COIN_SPIN_SPEED = 2f
    
    // Text Size Constants (relative to base size)
    // Elderly-friendly font sizes (moderately increased)
    private const val BASE_LARGE_TEXT_SIZE = 76f      // Was 64f, increased 19%
    private const val BASE_MEDIUM_TEXT_SIZE = 58f     // Was 48f, increased 21%
    private const val BASE_SMALL_TEXT_SIZE = 48f      // Was 40f, increased 20%
    private const val BASE_TINY_TEXT_SIZE = 34f       // Was 28f, increased 21%
    
    // Responsive scaling functions - maintain visual consistency across screen sizes
    fun getScaleFactor(screenWidth: Int, screenHeight: Int): Float {
        // Base screen size for scaling (typical phone resolution)
        val baseWidth = 1080f
        val baseHeight = 1920f
        
        // Use diagonal scaling to maintain aspect ratio consistency
        val currentDiagonal = kotlin.math.sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toDouble()).toFloat()
        val baseDiagonal = kotlin.math.sqrt((baseWidth * baseWidth + baseHeight * baseHeight).toDouble()).toFloat()
        
        // Scale factor with reasonable bounds to prevent extreme scaling
        return (currentDiagonal / baseDiagonal).coerceIn(0.7f, 1.5f)
    }
    
    fun scaledTextSize(baseSize: Float, screenWidth: Int, screenHeight: Int): Float {
        return baseSize * getScaleFactor(screenWidth, screenHeight)
    }
    
    fun scaledSize(baseSize: Float, screenWidth: Int, screenHeight: Int): Float {
        return baseSize * getScaleFactor(screenWidth, screenHeight)
    }
    
    // Responsive text size getters - use these instead of direct constants
    fun getLargeTextSize(screenWidth: Int, screenHeight: Int): Float = 
        scaledTextSize(BASE_LARGE_TEXT_SIZE, screenWidth, screenHeight)
    
    fun getMediumTextSize(screenWidth: Int, screenHeight: Int): Float = 
        scaledTextSize(BASE_MEDIUM_TEXT_SIZE, screenWidth, screenHeight)
    
    fun getSmallTextSize(screenWidth: Int, screenHeight: Int): Float = 
        scaledTextSize(BASE_SMALL_TEXT_SIZE, screenWidth, screenHeight)
    
    fun getTinyTextSize(screenWidth: Int, screenHeight: Int): Float = 
        scaledTextSize(BASE_TINY_TEXT_SIZE, screenWidth, screenHeight)
    
    // Reference device constants (OnePlus 9RT - perfect display)
    private const val REFERENCE_LARGE_TEXT = 76f
    private const val REFERENCE_MEDIUM_TEXT = 58f
    private const val REFERENCE_SMALL_TEXT = 48f
    private const val REFERENCE_TINY_TEXT = 34f

    // OnePlus 9RT reference specs (what the game was designed for)
    private const val REFERENCE_WIDTH = 1080f
    private const val REFERENCE_HEIGHT = 2400f
    private const val REFERENCE_DENSITY = 3.0f

    // Safe scaling functions that preserve OnePlus 9RT experience
    fun getSafeLargeTextSize(screenWidth: Int, screenHeight: Int, density: Float): Float {
        return if (isReferenceDevice(screenWidth, screenHeight, density)) {
            REFERENCE_LARGE_TEXT // Keep original size for reference device
        } else {
            REFERENCE_LARGE_TEXT * getSafeScaleFactor(screenWidth, screenHeight, density)
        }
    }

    fun getSafeMediumTextSize(screenWidth: Int, screenHeight: Int, density: Float): Float {
        return if (isReferenceDevice(screenWidth, screenHeight, density)) {
            REFERENCE_MEDIUM_TEXT
        } else {
            REFERENCE_MEDIUM_TEXT * getSafeScaleFactor(screenWidth, screenHeight, density)
        }
    }

    fun getSafeSmallTextSize(screenWidth: Int, screenHeight: Int, density: Float): Float {
        return if (isReferenceDevice(screenWidth, screenHeight, density)) {
            REFERENCE_SMALL_TEXT
        } else {
            REFERENCE_SMALL_TEXT * getSafeScaleFactor(screenWidth, screenHeight, density)
        }
    }

    fun getSafeTinyTextSize(screenWidth: Int, screenHeight: Int, density: Float): Float {
        return if (isReferenceDevice(screenWidth, screenHeight, density)) {
            REFERENCE_TINY_TEXT
        } else {
            REFERENCE_TINY_TEXT * getSafeScaleFactor(screenWidth, screenHeight, density)
        }
    }

    // Check if current device is similar to OnePlus 9RT (reference device)
    private fun isReferenceDevice(screenWidth: Int, screenHeight: Int, density: Float): Boolean {
        val widthDiff = kotlin.math.abs(screenWidth - REFERENCE_WIDTH) / REFERENCE_WIDTH
        val heightDiff = kotlin.math.abs(screenHeight - REFERENCE_HEIGHT) / REFERENCE_HEIGHT
        val densityDiff = kotlin.math.abs(density - REFERENCE_DENSITY) / REFERENCE_DENSITY

        // If device is within 10% of OnePlus 9RT specs, use original sizes
        return widthDiff < 0.1f && heightDiff < 0.1f && densityDiff < 0.1f
    }

    // Conservative scaling that only affects significantly different devices
    private fun getSafeScaleFactor(screenWidth: Int, screenHeight: Int, density: Float): Float {
        val currentDiagonal = kotlin.math.sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toDouble()).toFloat()
        val referenceDiagonal = kotlin.math.sqrt((REFERENCE_WIDTH * REFERENCE_WIDTH + REFERENCE_HEIGHT * REFERENCE_HEIGHT).toDouble()).toFloat()

        // Normalize by density
        val currentNormalized = currentDiagonal / density
        val referenceNormalized = referenceDiagonal / REFERENCE_DENSITY

        // Conservative scaling with tight bounds
        return (currentNormalized / referenceNormalized).coerceIn(0.85f, 1.15f)
    }

    // Legacy constants for backward compatibility (OnePlus 9RT values)
    const val LARGE_TEXT_SIZE = 76f
    const val MEDIUM_TEXT_SIZE = 58f
    const val SMALL_TEXT_SIZE = 48f
    const val TINY_TEXT_SIZE = 34f
    
    // Game Balance Constants
    const val CHAIN_COIN_PROBABILITY = 0.3f
    const val CHAIN_LENGTH_MIN = 3
    const val CHAIN_LENGTH_MAX = 6
    const val GOLD_COIN_PROBABILITY = 0.85f  // When spawning single coins
    const val DIAMOND_COIN_PROBABILITY = 0.25f // When spawning in chains
}