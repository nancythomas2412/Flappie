package com.glacierbird.game

import android.content.Context
import kotlin.math.*

/**
 * Universal Density Utilities - Ensures consistent experience across all Android devices
 * Automatically adapts to any screen size, density, and device type
 */
object DensityUtils {

    // Removed context reference to prevent memory leaks
    private var density: Float = 1f
    private var scaledDensity: Float = 1f
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenDiagonal: Float = 0f

    // Base design dimensions (what the game was designed for)
    private const val BASE_DESIGN_WIDTH = 1080f
    private const val BASE_DESIGN_HEIGHT = 1920f
    private const val BASE_DESIGN_DENSITY = 3f // xxxhdpi

    /**
     * Initialize with context - call once in GameView
     */
    fun initialize(ctx: Context, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        val metrics = ctx.resources.displayMetrics
        density = metrics.density
        @Suppress("DEPRECATION")
        scaledDensity = metrics.scaledDensity

        // Calculate screen diagonal for consistent sizing
        screenDiagonal = sqrt((width * width + height * height).toFloat())
    }

    /**
     * Convert dp to pixels - maintains visual consistency across densities
     */
    fun dp(dp: Float): Float = dp * density

    /**
     * Smart scaling based on screen diagonal - ensures elements feel the same size
     * on all devices regardless of screen size or density
     */
    private fun getSmartScale(): Float {
        // Safeguard against uninitialized or zero dimensions
        if (screenWidth <= 0 || screenHeight <= 0 || screenDiagonal <= 0f) {
            return 1.0f // Fallback to no scaling
        }

        val baseDiagonal = sqrt(BASE_DESIGN_WIDTH * BASE_DESIGN_WIDTH + BASE_DESIGN_HEIGHT * BASE_DESIGN_HEIGHT)
        val currentDiagonal = screenDiagonal / density // Normalize by density
        val baseNormalizedDiagonal = baseDiagonal / BASE_DESIGN_DENSITY

        // Scale factor with reasonable bounds
        return (currentDiagonal / baseNormalizedDiagonal).coerceIn(0.7f, 2.0f)
    }

    /**
     * Universal scaling that combines density and screen size awareness
     */
    fun scale(baseSize: Float): Float = baseSize * density * getSmartScale()

    /**
     * Game element scaling specifically tuned for gameplay elements
     */
    object GameElements {
        fun getBirdRadius(): Float = dp(35f) // Decreased further for better gameplay
        fun getBirdCollisionRadius(): Float = dp(26f) // Proportional to bird radius
        fun getBirdGravity(): Float = 0.8f // Slightly gentler than 1.0.4 for smoother control
        fun getBirdJump(): Float = -18f // Original 1.0.4 jump strength - smooth arcs
        fun getBirdMaxVelocity(): Float = 15f // Original 1.0.0 max velocity - NOT scaled

        fun getPowerUpSize(): Float = dp(49.5f) // Increased by 10% for better visibility
        fun getCoinSize(): Float = dp(55f) // Increased by 10% for better visibility

        fun getPipeWidth(): Float = dp(30f) // Reduced more for easier navigation
        fun getPipeSpeed(): Float = scale(0.92f) // Original 1.0.3/1.0.4 base speed
        fun getPipeGapSize(): Float {
            // Dynamic gap based on screen height for consistent difficulty
            val baseGapRatio = 0.25f // 25% of screen height
            return screenHeight * baseGapRatio
        }
        fun getPipeMinGap(): Float {
            val minGapRatio = 0.18f // 18% of screen height minimum
            return screenHeight * minGapRatio
        }
    }

    /**
     * UI element scaling for consistent interface across devices
     * Using proper density scaling instead of screen width percentages
     */
    object UI {
        fun getTextSmall(): Float = dp(14f) // Compact text for dense layouts
        fun getTextMedium(): Float = dp(16f) // Readable but compact
        fun getTextLarge(): Float = dp(18f) // Clear but not huge
        fun getTextTitle(): Float = dp(22f) // Prominent but reasonable
        fun getStatsTextMedium(): Float = dp(14f) // Compact but readable
        fun getStatsTextLarge(): Float = dp(16f) // Clear in stats context

        fun getSpacingSmall(): Float = scale(8f) // Base 8dp
        fun getSpacingMedium(): Float = scale(16f) // Base 16dp
        fun getSpacingLarge(): Float = scale(24f) // Base 24dp

        fun getStrokeWidth(): Float = scale(2f) // Base 2dp
        fun getStrokeWidthThick(): Float = scale(4f) // Base 4dp

        // Heart sizing - responsive instead of screen percentage based
        fun getHeartSize(): Float = dp(36f) // Base 36dp with smart scaling
        fun getHeartSpacing(): Float = dp(42f) // Base 42dp spacing

        // Panel sizing - responsive with aspect ratio limits
        fun getMaxPanelWidth(): Float = kotlin.math.min(screenWidth * 0.95f, dp(800f))
        fun getMaxPanelHeight(): Float = kotlin.math.min(screenHeight * 0.90f, dp(720f))
        fun getNotificationWidth(): Float = kotlin.math.min(screenWidth * 0.8f, dp(600f))

        // HUD margins - responsive instead of fixed pixels
        fun getHUDMargin(): Float = scale(60f) // Base 60dp margin
        fun getIndicatorBounds(): Float = scale(50f) // Base 50dp bounds
        fun getCoinsMargin(): Float = scale(10f) // Base 10dp margin
    }

    /**
     * Hat system scaling for consistent cosmetic experience
     * Using responsive sizing that works properly with dual initialization
     */
    object Hats {
        fun getCardHeight(): Float {
            // Increased height for better content fit
            return if (screenHeight > 0) screenHeight * 0.15f else 200f
        }
        fun getSpriteSize(): Float = dp(32f) // Appropriate sprite size
        fun getNameTextSize(): Float = dp(13f) // Larger, more prominent hat names
        fun getDescriptionTextSize(): Float = dp(9f) // Compact descriptions
    }

    /**
     * Layout helpers for positioning elements consistently
     */
    object Layout {
        fun getTopSafeArea(): Float = scale(24f) // Account for status bar

        // Portrait-only gap calculation - density scaled with reasonable maximum
        fun getPortraitMinGap(): Float = kotlin.math.min(scale(120f), screenHeight * 0.10f)
    }

}