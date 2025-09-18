package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * UIRenderer - Centralized UI rendering system
 * Handles all user interface drawing operations
 */
class UIRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    // Color caching for performance
    private var cachedColor: Int? = null
    private var lastScore = -1 // Track the exact score we cached for

    // Paint objects for different UI elements
    private val backgroundPaint = Paint().apply {
        color = GameConstants.BACKGROUND_COLOR.toColorInt()
        style = Paint.Style.FILL
    }
    
    private val groundPaint = Paint().apply {
        color = GameConstants.GROUND_COLOR.toColorInt()
        style = Paint.Style.FILL
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = GameConstants.LARGE_TEXT_SIZE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val smallTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = GameConstants.MEDIUM_TEXT_SIZE
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val buttonPaint = Paint().apply {
        color = GameConstants.BUTTON_COLOR.toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val buttonTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = GameConstants.MEDIUM_TEXT_SIZE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    // Specialized renderers
    private val menuRenderer = MenuRenderer(screenWidth, screenHeight, textPaint, smallTextPaint)
    private val gameRenderer = GameRenderer(screenWidth, screenHeight, textPaint, smallTextPaint)
    private val hudRenderer = HUDRenderer(screenWidth, screenHeight)
    private val overlayRenderer = OverlayRenderer(screenWidth, screenHeight)
    
    /**
     * Draw background and ground with progressive day-to-night transition
     */
    fun drawBackground(canvas: Canvas, score: Int = 0) {
        // Calculate background color based on score progression
        val backgroundColor = calculateProgressiveBackgroundColor(score)
        backgroundPaint.color = backgroundColor

        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), backgroundPaint)
        canvas.drawRect(
            0f,
            screenHeight - GameConstants.GROUND_HEIGHT,
            screenWidth.toFloat(),
            screenHeight.toFloat(),
            groundPaint
        )
    }

    /**
     * Draw background for menu/game over screens with fixed color
     */
    fun drawMenuBackground(canvas: Canvas) {
        backgroundPaint.color = "#73c1e6".toColorInt()
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), backgroundPaint)
        canvas.drawRect(
            0f,
            screenHeight - GameConstants.GROUND_HEIGHT,
            screenWidth.toFloat(),
            screenHeight.toFloat(),
            groundPaint
        )
    }
    
    /**
     * Calculate background color based on score for bidirectional day-to-night cycle
     * Starts with morning and alternates direction for natural progression:
     * Cycle 1 (0-2000): Morning → Day → Evening → Night (forward)
     * Cycle 2 (2001-4000): Night → Evening → Day → Morning (reverse)
     * Cycle 3 (4001-6000): Morning → Day → Evening → Night (forward)
     * And so on, alternating direction for variety...
     */
    private fun calculateProgressiveBackgroundColor(score: Int): Int {
        // Cache color for every 5 points to balance smoothness with performance
        val cacheKey = (score / 5) * 5

        // Only recalculate if we haven't cached this score range yet
        if (cacheKey != lastScore || cachedColor == null) {
            cachedColor = calculateSkyColor(score)
            lastScore = cacheKey
        }

        return cachedColor!!
    }

    /**
     * Calculate the actual sky color based on score
     */
    private fun calculateSkyColor(score: Int): Int {
        // Create a bidirectional cycle every 2000 points
        val cycleLength = 2000
        val fullCycle = score / cycleLength
        val cyclePosition = score % cycleLength

        // Determine if we're going forward or reverse
        val isForwardCycle = fullCycle % 2 == 0

        // Define the 4 key colors
        val morningColor = "#BBFBFF"  // Light cyan
        val dayColor = "#8DD8FF"      // Sky blue
        val eveningColor = "#4E71FF"  // Deep blue
        val nightColor = "#5409DA"    // Dark purple

        return if (isForwardCycle) {
            // Forward cycle: Morning → Day → Evening → Night
            when {
                cyclePosition <= 500 -> {
                    // Morning to Day transition
                    val progress = cyclePosition.toFloat() / 500f
                    interpolateColor(morningColor, dayColor, progress)
                }
                cyclePosition <= 1000 -> {
                    // Day time - stable
                    dayColor.toColorInt()
                }
                cyclePosition <= 1500 -> {
                    // Day to Evening transition
                    val progress = (cyclePosition - 1000).toFloat() / 500f
                    interpolateColor(dayColor, eveningColor, progress)
                }
                else -> {
                    // Evening to Night transition
                    val progress = (cyclePosition - 1500).toFloat() / 500f
                    interpolateColor(eveningColor, nightColor, progress)
                }
            }
        } else {
            // Reverse cycle: Night → Evening → Day → Morning
            when {
                cyclePosition <= 500 -> {
                    // Night to Evening transition
                    val progress = cyclePosition.toFloat() / 500f
                    interpolateColor(nightColor, eveningColor, progress)
                }
                cyclePosition <= 1000 -> {
                    // Evening time - stable
                    eveningColor.toColorInt()
                }
                cyclePosition <= 1500 -> {
                    // Evening to Day transition
                    val progress = (cyclePosition - 1000).toFloat() / 500f
                    interpolateColor(eveningColor, dayColor, progress)
                }
                else -> {
                    // Day to Morning transition
                    val progress = (cyclePosition - 1500).toFloat() / 500f
                    interpolateColor(dayColor, morningColor, progress)
                }
            }
        }
    }
    
    /**
     * Interpolate between two hex colors
     */
    private fun interpolateColor(startColor: String, endColor: String, factor: Float): Int {
        val clampedFactor = factor.coerceIn(0f, 1f)
        
        val start = startColor.toColorInt()
        val end = endColor.toColorInt()
        
        val startR = Color.red(start)
        val startG = Color.green(start)
        val startB = Color.blue(start)
        
        val endR = Color.red(end)
        val endG = Color.green(end)
        val endB = Color.blue(end)
        
        val r = (startR + (endR - startR) * clampedFactor).toInt()
        val g = (startG + (endG - startG) * clampedFactor).toInt()
        val b = (startB + (endB - startB) * clampedFactor).toInt()
        
        return Color.rgb(r, g, b)
    }
    
    /**
     * Draw main menu
     */
    fun drawMenu(canvas: Canvas, bestScore: Int, bird: Bird, canPlay: Boolean = true, heartRefillTime: String? = null, totalCoins: Int = 0, currentLives: Int = 3, heartSprites: HeartSprites? = null) {
        menuRenderer.draw(canvas, bestScore, bird, canPlay, heartRefillTime, totalCoins, currentLives, heartSprites)
    }
    
    /**
     * Draw game over screen
     */
    fun drawGameOver(canvas: Canvas, score: Int, totalCoins: Int, bestScore: Int, sadBirdSprite: Bitmap?, bird: Bird, canPlay: Boolean = true, heartRefillTime: String? = null, currentLives: Int = 0, maxLives: Int = 3, heartSprites: HeartSprites? = null) {
        menuRenderer.drawGameOver(canvas, score, totalCoins, bestScore, sadBirdSprite, bird, canPlay, heartRefillTime, currentLives, maxLives, heartSprites)
    }
    
    /**
     * Draw in-game UI elements
     */
    fun drawGameUI(canvas: Canvas, score: Int, totalCoins: Int, playerLives: Int, maxLives: Int, heartSprites: HeartSprites) {
        gameRenderer.drawScore(canvas, score)
        hudRenderer.drawLives(canvas, playerLives, maxLives, heartSprites)
        hudRenderer.drawCoinCounter(canvas, totalCoins)
    }
    
    /**
     * Draw UI buttons
     */
    fun drawUIButtons(canvas: Canvas, buttonAreas: ButtonAreas, gameState: GameState) {
        val showPauseButton = gameState == GameState.PLAYING
        val showExitButton = gameState == GameState.MENU || gameState == GameState.GAME_OVER
        val showHomeButton = gameState == GameState.GAME_OVER
        val isMenuScreen = gameState == GameState.MENU || gameState == GameState.GAME_OVER
        hudRenderer.drawUIButtons(canvas, buttonAreas, buttonPaint, buttonTextPaint, showPauseButton, showExitButton, showHomeButton, isMenuScreen)
    }
    
    /**
     * Draw power-up indicators
     */
    fun drawPowerUpIndicators(canvas: Canvas, powerUpStates: PowerUpStates, birdX: Float = 0f, birdY: Float = 0f) {
        hudRenderer.drawPowerUpIndicators(canvas, powerUpStates, birdX, birdY)
    }
    
    /**
     * Draw pause menu overlay
     */
    fun drawPauseMenu(canvas: Canvas) {
        overlayRenderer.drawPauseMenu(canvas)
    }
    
    /**
     * Draw settings menu overlay
     */
    fun drawSettings(canvas: Canvas, soundEnabled: Boolean, musicEnabled: Boolean) {
        overlayRenderer.drawSettings(canvas, soundEnabled, musicEnabled)
    }
    
    /**
     * Draw shop menu overlay
     */
    fun drawShop(canvas: Canvas, totalCoins: Int) {
        overlayRenderer.drawShop(canvas, totalCoins)
    }
    
    /**
     * Draw shield effect around bird
     */
    fun drawShieldEffect(canvas: Canvas, bird: Bird) {
        gameRenderer.drawShieldEffect(canvas, bird)
    }
    
    /**
     * Draw score popup text
     */
    fun drawScorePopup(canvas: Canvas, text: String, x: Float, y: Float) {
        val popupPaint = Paint().apply {
            color = Color.YELLOW
            textSize = 80f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(text, x, y, popupPaint)
    }
    
}

/**
 * PowerUpStates - Container for active power-up information
 */
// Moved PowerUpStates to separate file to avoid circular import issues