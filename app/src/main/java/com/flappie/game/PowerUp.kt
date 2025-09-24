package com.flappie.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Bitmap
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt

/**
 * PowerUp - FIXED: Better attraction and movement
 */
class PowerUp(
    initialX: Float,
    initialY: Float,
    val type: PowerUpType,
    private val sprite: Bitmap?
) {

    var x: Float = initialX
    var y: Float = initialY
    private val xSpeed = GameConstants.COLLECTIBLE_SPEED
    private val size = GameConstants.POWERUP_SIZE

    // Animation properties
    private val floatingAnimator = FloatingAnimator()
    private val powerUpAnimator = PowerUpAnimator()

    // Paint for fallback rendering
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    /**
     * FIXED: Better bird attraction behavior
     */
    fun update(birdX: Float = 0f, birdY: Float = 0f, @Suppress("UNUSED_PARAMETER") magnetActive: Boolean = false) {
        // Slow horizontal movement
        x -= xSpeed

        // Enhanced attraction to bird
        val dx = birdX - x
        val dy = birdY - y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < GameConstants.COLLECTIBLE_ATTRACTION_RANGE) {
            val attractionForce = GameConstants.COLLECTIBLE_ATTRACTION_FORCE
            x += (dx / distance) * attractionForce * 0.45f  // Enhanced x attraction (+12.5%)
            y += (dy / distance) * attractionForce * 1.8f   // Enhanced y attraction (+50%)
        } else {
            // Natural floating when not attracted
            y += floatingAnimator.getNaturalFloatOffset()
        }

        // Update animators
        floatingAnimator.update(distance < GameConstants.COLLECTIBLE_ATTRACTION_RANGE)
        powerUpAnimator.update()
    }

    /**
     * Draw the power-up with floating animation
     */
    fun draw(canvas: Canvas) {
        val floatOffset = floatingAnimator.getFloatOffset()
        val drawY = y + floatOffset

        if (sprite != null) {
            val rect = RectF(
                x - size/2, drawY - size/2,
                x + size/2, drawY + size/2
            )
            canvas.drawBitmap(sprite, null, rect, null)
        } else {
            // Fallback: Draw colored circle with type indicator
            paint.color = when (type) {
                PowerUpType.SHIELD -> GameConstants.SHIELD_COLOR.toColorInt()
                PowerUpType.SLOW_MOTION -> "#87CEEB".toColorInt()
                PowerUpType.SCORE_MULTIPLIER -> GameConstants.COIN_GOLD_COLOR.toColorInt()
                PowerUpType.MAGNET -> "#DC143C".toColorInt()
                PowerUpType.EXTRA_LIFE -> "#FF69B4".toColorInt()
            }
            canvas.drawCircle(x, drawY, size/2, paint)
            
            // Draw type indicator text
            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 24f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val text = when (type) {
                PowerUpType.SHIELD -> "S"
                PowerUpType.SLOW_MOTION -> "T"
                PowerUpType.SCORE_MULTIPLIER -> "2X"
                PowerUpType.MAGNET -> "M"
                PowerUpType.EXTRA_LIFE -> "+"
            }
            canvas.drawText(text, x, drawY + 8f, textPaint)
        }
    }

    fun isOffScreen(): Boolean = x + size < -50f

    fun collidesWith(bird: Bird): Boolean {
        return CollisionUtils.checkBirdCollectibleCollision(bird, x, y, size)
    }
}

enum class PowerUpType(val duration: Int, val displayName: String) {
    SHIELD(GameConstants.PowerUpDuration.SHIELD, "Shield"),
    SLOW_MOTION(GameConstants.PowerUpDuration.SLOW_MOTION, "Slow Motion"),
    SCORE_MULTIPLIER(GameConstants.PowerUpDuration.SCORE_MULTIPLIER, "Score Boost"),
    MAGNET(GameConstants.PowerUpDuration.MAGNET, "Magnet"),
    EXTRA_LIFE(GameConstants.PowerUpDuration.EXTRA_LIFE, "Extra Life")
}