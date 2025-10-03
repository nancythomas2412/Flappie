package com.glacierbird.game

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

    // Visual properties - Ice spike theme
    private val iceColor = "#B0E0E6".toColorInt() // Light ice blue
    private val iceOutlineColor = "#4682B4".toColorInt() // Steel blue outline
    private val iceHighlightColor = "#F0F8FF".toColorInt() // Alice blue highlight

    // Paint objects for ice spikes
    private val icePaint = Paint().apply {
        color = iceColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val outlinePaint = Paint().apply {
        color = iceOutlineColor
        style = Paint.Style.STROKE
        strokeWidth = 3f  // Ice crystal outline
        isAntiAlias = true
    }

    private val highlightPaint = Paint().apply {
        color = iceHighlightColor
        style = Paint.Style.FILL
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
    fun update(globalPipeSpeed: Float, deltaTime: Float = 1f/60f) {
        // True frame-rate independent movement
        x -= globalPipeSpeed * deltaTime * 60f // Scale to maintain same speed as 60fps baseline
    }

    /**
     * Draw ice spike obstacles with crystalline appearance
     */
    fun draw(canvas: Canvas) {
        // Draw top ice spike - main body
        val topSpike = RectF(x, 0f, x + width, gapTop)
        canvas.drawRect(topSpike, icePaint)

        // Add ice highlight on left edge of top spike
        val topHighlight = RectF(x, 0f, x + width * 0.3f, gapTop)
        canvas.drawRect(topHighlight, highlightPaint)

        // Draw top spike outline
        canvas.drawRect(topSpike, outlinePaint)

        // Draw ice spike cap (jagged crystal formation)
        val topCap = RectF(x - GameConstants.PIPE_CAP_WIDTH, gapTop - GameConstants.PIPE_CAP_HEIGHT, x + width + GameConstants.PIPE_CAP_WIDTH, gapTop)
        canvas.drawRect(topCap, icePaint)
        canvas.drawRect(topCap, outlinePaint)

        // Draw bottom ice spike - main body
        val bottomSpike = RectF(x, gapBottom, x + width, screenHeight - groundHeight)
        canvas.drawRect(bottomSpike, icePaint)

        // Add ice highlight on left edge of bottom spike
        val bottomHighlight = RectF(x, gapBottom, x + width * 0.3f, screenHeight - groundHeight)
        canvas.drawRect(bottomHighlight, highlightPaint)

        // Draw bottom spike outline
        canvas.drawRect(bottomSpike, outlinePaint)

        // Draw bottom spike cap (jagged crystal formation)
        val bottomCap = RectF(x - GameConstants.PIPE_CAP_WIDTH, gapBottom, x + width + GameConstants.PIPE_CAP_WIDTH, gapBottom + GameConstants.PIPE_CAP_HEIGHT)
        canvas.drawRect(bottomCap, icePaint)
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
