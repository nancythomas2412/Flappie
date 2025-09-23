package com.flappie.game

import android.content.Context
import android.util.TypedValue
import kotlin.math.*

/**
 * Universal Density Utilities - Ensures consistent experience across all Android devices
 * Automatically adapts to any screen size, density, and device type
 */
object DensityUtils {

    private var context: Context? = null
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
        context = ctx
        screenWidth = width
        screenHeight = height

        val metrics = ctx.resources.displayMetrics
        density = metrics.density
        @Suppress("DEPRECATION")
        scaledDensity = metrics.scaledDensity

        // Calculate screen diagonal for consistent sizing
        screenDiagonal = sqrt((width * width + height * height).toFloat())
    }

    private fun getDensityName(): String = when (density) {
        in 0f..1f -> "ldpi"
        in 1f..1.5f -> "mdpi"
        in 1.5f..2f -> "hdpi"
        in 2f..3f -> "xhdpi"
        in 3f..4f -> "xxhdpi"
        else -> "xxxhdpi+"
    }

    /**
     * Convert dp to pixels - maintains visual consistency across densities
     */
    fun dp(dp: Float): Float = dp * density

    /**
     * Convert sp to pixels for text - respects user's font size preferences
     */
    fun sp(sp: Float): Float = sp * scaledDensity

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
     * Text scaling that respects both density and user preferences
     */
    fun textScale(baseSize: Float): Float = baseSize * scaledDensity * getSmartScale()

    /**
     * Game element scaling specifically tuned for gameplay elements
     */
    object GameElements {
        fun getBirdRadius(): Float = dp(35f) // Decreased further for better gameplay
        fun getBirdCollisionRadius(): Float = dp(26f) // Proportional to bird radius
        fun getBirdGravity(): Float = 0.5f // Further reduced for easier bird control
        fun getBirdJump(): Float = -16f // Reduced jump strength for gentler control
        fun getBirdMaxVelocity(): Float = 15f // Original 1.0.0 max velocity - NOT scaled

        fun getPowerUpSize(): Float = dp(49.5f) // Increased by 10% for better visibility
        fun getCoinSize(): Float = dp(55f) // Increased by 10% for better visibility

        fun getPipeWidth(): Float = dp(30f) // Reduced more for easier navigation
        fun getPipeSpeed(): Float = scale(1f) // Base speed scaled
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
        fun getTextHuge(): Float = dp(20f) // Large but manageable

        // Specific sizes for statistics screen (very compact)
        fun getStatsTextSmall(): Float = dp(12f) // Very compact for stats
        fun getStatsTextMedium(): Float = dp(14f) // Compact but readable
        fun getStatsTextLarge(): Float = dp(16f) // Clear in stats context

        fun getButtonHeight(): Float = scale(48f) // Base 48dp (recommended touch target)
        fun getButtonMinWidth(): Float = scale(88f) // Base 88dp minimum

        fun getIconSize(): Float = scale(24f) // Base 24dp
        fun getIconLarge(): Float = scale(32f) // Base 32dp

        fun getSpacingSmall(): Float = scale(8f) // Base 8dp
        fun getSpacingMedium(): Float = scale(16f) // Base 16dp
        fun getSpacingLarge(): Float = scale(24f) // Base 24dp

        fun getStrokeWidth(): Float = scale(2f) // Base 2dp
        fun getStrokeWidthThick(): Float = scale(4f) // Base 4dp
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
        fun getCardWidth(): Float {
            // Increased width for better content fit
            return if (screenWidth > 0) screenWidth * 0.23f else 180f
        }
        fun getSpriteSize(): Float = dp(32f) // Appropriate sprite size
        fun getNameTextSize(): Float = dp(13f) // Larger, more prominent hat names
        fun getDescriptionTextSize(): Float = dp(9f) // Compact descriptions
    }

    /**
     * Touch target scaling for accessibility and usability
     */
    object Touch {
        fun getMinimumTouchTarget(): Float = scale(48f) // Android recommended 48dp
        fun getComfortableTouchTarget(): Float = scale(56f) // More comfortable 56dp

        fun expandTouchArea(originalSize: Float): Float {
            val minTouch = getMinimumTouchTarget()
            return if (originalSize < minTouch) minTouch else originalSize
        }
    }

    /**
     * Performance scaling - adjust rendering quality based on device capabilities
     */
    object Performance {
        fun getParticleCount(): Int {
            // Reduce particles on lower-end devices (inferred from density)
            return when {
                density < 2f -> 5 // Lower-end devices
                density < 3f -> 8 // Mid-range devices
                else -> 12 // High-end devices
            }
        }

        fun shouldUseSimpleAnimations(): Boolean = density < 2f

        fun getTrailLength(): Int = when {
            density < 2f -> 3
            density < 3f -> 5
            else -> 8
        }
    }

    /**
     * Layout helpers for positioning elements consistently
     */
    object Layout {
        fun centerX(): Float = screenWidth / 2f
        fun centerY(): Float = screenHeight / 2f

        fun getTopSafeArea(): Float = scale(24f) // Account for status bar
        fun getBottomSafeArea(): Float = scale(16f) // Account for navigation

        fun getContentWidth(): Float = screenWidth - (getSpacingLarge() * 2)
        fun getContentHeight(): Float = screenHeight - getTopSafeArea() - getBottomSafeArea()

        private fun getSpacingLarge(): Float = UI.getSpacingLarge()
    }

    /**
     * Safe text scaling that preserves OnePlus 9RT experience
     */
    object SafeText {
        fun getLargeText(): Float = GameConstants.getSafeLargeTextSize(screenWidth, screenHeight, density)
        fun getMediumText(): Float = GameConstants.getSafeMediumTextSize(screenWidth, screenHeight, density)
        fun getSmallText(): Float = GameConstants.getSafeSmallTextSize(screenWidth, screenHeight, density)
        fun getTinyText(): Float = GameConstants.getSafeTinyTextSize(screenWidth, screenHeight, density)
    }
}