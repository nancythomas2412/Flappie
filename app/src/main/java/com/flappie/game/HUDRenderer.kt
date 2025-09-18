package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * HUDRenderer - Handles HUD elements (lives, coins, buttons, power-up indicators)
 */
class HUDRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    private val coinPaint = Paint().apply {
        color = GameConstants.COIN_GOLD_COLOR.toColorInt()
        textSize = 50f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    
    private val indicatorPaint = Paint().apply {
        color = Color.WHITE
        textSize = GameConstants.TINY_TEXT_SIZE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }
    
    private val heartPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val emptyHeartPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }
    
    /**
     * Draw lives using heart sprites or fallback shapes
     */
    fun drawLives(canvas: Canvas, playerLives: Int, maxLives: Int, heartSprites: HeartSprites) {
        val heartSize = screenWidth * GameConstants.HEART_SIZE_RATIO
        val heartSpacing = screenWidth * GameConstants.HEART_SPACING_RATIO
        
        for (i in 0 until maxLives) {
            val heartX = GameConstants.HEART_MARGIN_X + (i * heartSpacing)
            val heartY = GameConstants.HEART_MARGIN_Y
            
            val heartSprite = if (i < playerLives) heartSprites.full else heartSprites.empty
            
            if (heartSprite != null) {
                val heartRect = RectF(
                    heartX, heartY,
                    heartX + heartSize, heartY + heartSize
                )
                canvas.drawBitmap(heartSprite, null, heartRect, null)
            } else {
                // Fallback heart drawing
                drawFallbackHeart(canvas, heartX, heartY, i < playerLives)
            }
        }
    }
    
    /**
     * Draw fallback heart shape when sprites are not available
     */
    private fun drawFallbackHeart(canvas: Canvas, x: Float, y: Float, isFull: Boolean) {
        val paint = if (isFull) heartPaint else emptyHeartPaint
        
        // Draw two circles for heart top
        canvas.drawCircle(x + 15f, y + 20f, 15f, paint)
        canvas.drawCircle(x + 35f, y + 20f, 15f, paint)
        
        // Draw triangle for heart bottom
        val path = Path()
        path.moveTo(x, y + 25f)
        path.lineTo(x + 25f, y + 50f)
        path.lineTo(x + 50f, y + 25f)
        canvas.drawPath(path, paint)
    }
    
    /**
     * Draw coin counter at top right
     */
    fun drawCoinCounter(canvas: Canvas, totalCoins: Int) {
        canvas.drawText("💰 $totalCoins", screenWidth - 30f, 160f, coinPaint)
    }
    
    /**
     * Draw power-up indicators near bird's tail
     */
    fun drawPowerUpIndicators(canvas: Canvas, powerUpStates: PowerUpStates, birdX: Float = 0f, birdY: Float = 0f) {
        // Position indicators near the bird's tail (behind the bird)
        val baseIndicatorX = if (birdX > 0) birdX - 120f else 50f // Behind bird or fallback to left side
        var indicatorY = if (birdY > 0) birdY - 20f else screenHeight - 180f // Above bird or fallback position
        
        // Ensure indicators stay within screen bounds
        val indicatorX = maxOf(10f, minOf(baseIndicatorX, screenWidth - 150f))
        indicatorY = maxOf(50f, minOf(indicatorY, screenHeight - 50f))
        
        if (powerUpStates.shieldActive) {
            canvas.drawText(
                "🛡️ ${powerUpStates.shieldTimer/60 + 1}s", 
                indicatorX, 
                indicatorY, 
                indicatorPaint
            )
            indicatorY -= 35f
        }
        
        if (powerUpStates.slowMotionActive) {
            canvas.drawText(
                "⏰ ${powerUpStates.slowMotionTimer/60 + 1}s", 
                indicatorX, 
                indicatorY, 
                indicatorPaint
            )
            indicatorY -= 35f
        }
        
        if (powerUpStates.scoreMultiplierActive) {
            canvas.drawText(
                "✨ 2x ${powerUpStates.scoreMultiplierTimer/60 + 1}s", 
                indicatorX, 
                indicatorY, 
                indicatorPaint
            )
            indicatorY -= 35f
        }
        
        if (powerUpStates.magnetActive) {
            canvas.drawText(
                "🧲 ${powerUpStates.magnetTimer/60 + 1}s", 
                indicatorX, 
                indicatorY, 
                indicatorPaint
            )
        }
    }
    
    /**
     * Draw UI buttons (pause, settings, shop)
     */
    fun drawUIButtons(canvas: Canvas, buttonAreas: ButtonAreas, buttonPaint: Paint, buttonTextPaint: Paint, showPauseButton: Boolean = true, showExitButton: Boolean = false, showHomeButton: Boolean = false, isMenuScreen: Boolean = false) {
        // Create gradient and enhanced styling for all screens
        val primaryColor = if (isMenuScreen) Color.parseColor("#2196F3") else Color.parseColor("#1976D2") // Blue gradient
        @Suppress("UNUSED_VARIABLE")
        val secondaryColor = if (isMenuScreen) Color.parseColor("#1976D2") else Color.parseColor("#1565C0") // Darker blue
        
        val enhancedButtonPaint = Paint(buttonPaint).apply {
            color = primaryColor
            style = Paint.Style.FILL
            setShadowLayer(10f, 3f, 3f, Color.parseColor("#40000000")) // Consistent shadow for all screens
        }
        
        // Border paint for elegant outline
        val borderPaint = Paint().apply {
            color = Color.parseColor("#0D47A1") // Dark blue border
            style = Paint.Style.STROKE
            strokeWidth = if (isMenuScreen) 4f else 3f
            isAntiAlias = true
        }
        
        val enhancedButtonTextPaint = Paint(buttonTextPaint).apply {
            color = Color.WHITE // Always white text for consistency
            textSize = if (isMenuScreen) GameConstants.LARGE_TEXT_SIZE * 0.8f else GameConstants.MEDIUM_TEXT_SIZE * 1.1f
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(3f, 1f, 1f, Color.parseColor("#80000000")) // Text shadow for all screens
            isAntiAlias = true
        }
        
        val cornerRadius = if (isMenuScreen) 30f else 25f
        val textOffset = if (isMenuScreen) 22f else 20f
        
        // Helper function to draw elegant button with border
        fun drawElegantButton(buttonArea: RectF, text: String) {
            // Draw button background
            canvas.drawRoundRect(buttonArea, cornerRadius, cornerRadius, enhancedButtonPaint)
            // Draw border
            canvas.drawRoundRect(buttonArea, cornerRadius, cornerRadius, borderPaint)
            // Draw text
            canvas.drawText(text, buttonArea.centerX(), buttonArea.centerY() + textOffset, enhancedButtonTextPaint)
        }
        
        // Pause button (only show during gameplay)
        if (showPauseButton) {
            drawElegantButton(buttonAreas.pauseButton, "⏸")
        }
        
        // Settings button
        drawElegantButton(buttonAreas.settingsButton, "⚙")
        
        // Shop button
        drawElegantButton(buttonAreas.shopButton, "🛒")
        
        // Exit button (only show on menu and game over screens)
        if (showExitButton) {
            drawElegantButton(buttonAreas.exitButton, "✕")
        }
        
        // Home button (only show on game over screen)
        if (showHomeButton) {
            drawElegantButton(buttonAreas.homeButton, "🏠")
        }
    }
}