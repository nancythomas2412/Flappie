package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * GameRenderer - Handles in-game rendering (score, effects)
 */
class GameRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val textPaint: Paint,
    private val smallTextPaint: Paint
) {
    
    private val shieldPaint = Paint().apply {
        color = GameConstants.SHIELD_COLOR.toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
        alpha = 150
        isAntiAlias = true
    }
    
    /**
     * Draw current score at top center
     */
    fun drawScore(canvas: Canvas, score: Int) {
        canvas.drawText(score.toString(), screenWidth / 2f, 160f, textPaint)
    }
    
    /**
     * Draw shield effect around bird
     */
    fun drawShieldEffect(canvas: Canvas, bird: Bird) {
        canvas.drawCircle(bird.x, bird.y, GameConstants.BIRD_RADIUS + 20f, shieldPaint)
    }
}