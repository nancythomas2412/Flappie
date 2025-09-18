package com.flappie.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.toColorInt

/**
 * Pipe - Obstacle class with progressive difficulty
 */
class Pipe(
    initialX: Float,
    private val screenHeight: Int,
    private val groundHeight: Float,
    private val currentScore: Int  // Add score parameter for difficulty scaling
) {

    // Position and size properties
    var x: Float = initialX
    private val width = GameConstants.PIPE_WIDTH

    // Progressive difficulty settings
    private val gapSize: Float
    // Note: speed is now handled globally, not per-pipe

    // Gap position (randomized)
    private val gapTop: Float
    private val gapBottom: Float

    // Visual properties
    private val pipeColor = "#32CD32".toColorInt() // Lighter green, more friendly
    private val pipeOutlineColor = "#228B22".toColorInt() // Darker green outline

    // Paint objects
    private val pipePaint = Paint().apply {
        color = pipeColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val outlinePaint = Paint().apply {
        color = pipeOutlineColor
        style = Paint.Style.STROKE
        strokeWidth = 4f  // Thinner outline
        isAntiAlias = true
    }

    // Track if bird has passed this pipe
    var passed = false

    init {
        // Progressive difficulty based on score
        gapSize = calculateGapSize(currentScore)
        // Speed is now handled globally - removed per-pipe speed calculation

        // Randomize gap position with more generous bounds
        val minGapTop = GameConstants.PIPE_MIN_GAP_TOP
        val maxGapTop = screenHeight - groundHeight - gapSize - GameConstants.PIPE_MIN_GAP_BOTTOM
        gapTop = minGapTop + Math.random().toFloat() * (maxGapTop - minGapTop)
        gapBottom = gapTop + gapSize
    }

    /**
     * Calculate gap size based on current score (even more generous)
     */
    private fun calculateGapSize(score: Int): Float {
        val baseGapSize = GameConstants.PIPE_BASE_GAP_SIZE
        val minGapSize = GameConstants.PIPE_MIN_GAP_SIZE
        val reductionRate = GameConstants.PIPE_GAP_REDUCTION_RATE

        val reduction = (score / GameConstants.PIPE_SCORE_INTERVAL) * reductionRate
        return minGapSize.coerceAtLeast(baseGapSize - reduction)
    }

    /**
     * Update pipe position with global speed
     */
    fun update(globalPipeSpeed: Float) {
        x -= globalPipeSpeed
    }

    /**
     * Draw both pipe segments with rounded corners effect
     */
    fun draw(canvas: Canvas) {
        // Draw top pipe
        val topPipe = RectF(x, 0f, x + width, gapTop)
        canvas.drawRect(topPipe, pipePaint)
        canvas.drawRect(topPipe, outlinePaint)

        // Draw pipe cap (wider top part)
        val topCap = RectF(x - GameConstants.PIPE_CAP_WIDTH, gapTop - GameConstants.PIPE_CAP_HEIGHT, x + width + GameConstants.PIPE_CAP_WIDTH, gapTop)
        canvas.drawRect(topCap, pipePaint)
        canvas.drawRect(topCap, outlinePaint)

        // Draw bottom pipe
        val bottomPipe = RectF(x, gapBottom, x + width, screenHeight - groundHeight)
        canvas.drawRect(bottomPipe, pipePaint)
        canvas.drawRect(bottomPipe, outlinePaint)

        // Draw bottom pipe cap
        val bottomCap = RectF(x - GameConstants.PIPE_CAP_WIDTH, gapBottom, x + width + GameConstants.PIPE_CAP_WIDTH, gapBottom + GameConstants.PIPE_CAP_HEIGHT)
        canvas.drawRect(bottomCap, pipePaint)
        canvas.drawRect(bottomCap, outlinePaint)
    }

    /**
     * Check if pipe is off screen
     */
    fun isOffScreen(): Boolean {
        return x + width < 0
    }

    /**
     * Check collision with bird (more forgiving collision)
     */
    fun collidesWith(bird: Bird): Boolean {
        return CollisionUtils.checkBirdPipeCollision(
            bird, x, width, gapTop, gapBottom, screenHeight
        )
    }

    /**
     * Check if bird has passed through this pipe
     */
    fun hasBirdPassed(bird: Bird): Boolean {
        return !passed && bird.x > x + width
    }
}
