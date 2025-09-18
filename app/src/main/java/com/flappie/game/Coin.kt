package com.flappie.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Bitmap
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt

/**
 * Coin - FIXED: Better attraction behavior
 */
class Coin(
    initialX: Float,
    initialY: Float,
    val type: CoinType,
    private val sprite: Bitmap?
) {

    var x: Float = initialX
    var y: Float = initialY
    private val xSpeed = GameConstants.COLLECTIBLE_SPEED
    private val size = GameConstants.COIN_SIZE

    // Animation properties
    private val coinAnimator = CoinAnimator()
    private val floatingAnimator = FloatingAnimator()

    // Paint for effects
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    /**
     * FIXED: Enhanced bird attraction
     */
    fun update(birdX: Float = 0f, birdY: Float = 0f, magnetActive: Boolean = false) {
        // Slow horizontal movement
        x -= xSpeed

        // Enhanced attraction to bird
        val dx = birdX - x
        val dy = birdY - y
        val distance = sqrt(dx * dx + dy * dy)

        val attractionRange = if (magnetActive) GameConstants.COIN_MAGNET_RANGE else GameConstants.COLLECTIBLE_ATTRACTION_RANGE
        val attractionForce = if (magnetActive) GameConstants.COIN_MAGNET_FORCE else GameConstants.COLLECTIBLE_ATTRACTION_FORCE

        if (distance < attractionRange) {
            x += (dx / distance) * attractionForce * 0.3f  // Moderate x attraction
            y += (dy / distance) * attractionForce * 1.5f  // Strong y attraction
        } else {
            // Natural floating movement
            y += floatingAnimator.getNaturalFloatOffset()
        }

        // Update animators
        coinAnimator.update()
        floatingAnimator.update(distance < attractionRange)
    }

    /**
     * Draw coin with spinning animation
     */
    fun draw(canvas: Canvas) {
        val spinScale = coinAnimator.getSpinScale()
        val glowOffset = coinAnimator.getGlowOffset()

        if (sprite != null) {
            val rect = RectF(
                x - size/2 * spinScale, y - size/2 + glowOffset,
                x + size/2 * spinScale, y + size/2 + glowOffset
            )
            canvas.drawBitmap(sprite, null, rect, null)
        } else {
            paint.color = when (type) {
                CoinType.GOLD -> GameConstants.COIN_GOLD_COLOR.toColorInt()
                CoinType.DIAMOND -> GameConstants.COIN_DIAMOND_COLOR.toColorInt()
            }
            canvas.drawCircle(x, y + glowOffset, size/2 * spinScale, paint)
        }
    }

    fun isOffScreen(): Boolean = x + size < -50f

    fun collidesWith(bird: Bird): Boolean {
        return CollisionUtils.checkBirdCollectibleCollision(bird, x, y, size)
    }
}

enum class CoinType(val value: Int) {
    GOLD(GameConstants.COIN_GOLD_VALUE),
    DIAMOND(GameConstants.COIN_DIAMOND_VALUE)
}