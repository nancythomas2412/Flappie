package com.glacierbird.game

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation

//import kotlin.math.*

/**
 * Bird - Large bird with smooth wing flapping animation
 */
class Bird(
    initialX: Float,
    initialY: Float,
    private val context: Context,
    private val hatManager: HatManager? = null
) {

    // Position and physics properties
    var x: Float = initialX
    var y: Float = initialY
    var velocityY: Float = 0f

    // Visual properties - LARGE and visible
    val radius: Float = GameConstants.BIRD_RADIUS
    val collisionRadius: Float = GameConstants.BIRD_COLLISION_RADIUS

    // Physics constants
    private val gravity = GameConstants.BIRD_GRAVITY
    private val jumpStrength = GameConstants.BIRD_JUMP_STRENGTH
    private val maxVelocity = GameConstants.BIRD_MAX_VELOCITY

    // Animation properties (now managed by BirdAnimator)
    private val birdAnimator = BirdAnimator()

    // Bird animation frames - 5-frame smooth cycle
    private var birdNeutral: Bitmap? = null      // Wings middle
    private var birdTransition: Bitmap? = null   // Transition frame (used twice)
    private var birdWingsUp: Bitmap? = null      // Wings up
    private var birdWingsDown: Bitmap? = null    // Wings down

    // Hat caching for performance
    private var cachedHatSprite: Bitmap? = null
    private var cachedHatType: HatType? = null


    // Visual effects
    private val trailPoints = mutableListOf<TrailPoint>()

    private val trailPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Fallback large circle
    private val fallbackPaint = Paint().apply {
        color = "#FFD700".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val outlinePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    init {
        loadBirdSprites()
    }

    /**
     * Load the wing animation frames using direct resource references
     */
    private fun loadBirdSprites() {
        try {
            birdNeutral = BitmapFactory.decodeResource(context.resources, R.drawable.bird_neutral)
            birdTransition = BitmapFactory.decodeResource(context.resources, R.drawable.bird_transition)
            birdWingsUp = BitmapFactory.decodeResource(context.resources, R.drawable.bird_wings_up)
            birdWingsDown = BitmapFactory.decodeResource(context.resources, R.drawable.bird_wings_down)
        } catch (e: Exception) {
            Log.w("Bird", "Failed to load bird sprites: ${e.message}")
            // Will use fallback circle
        }
    }


    /**
     * Update bird physics and animation
     * @param deltaTime Time elapsed since last frame in seconds (kept for compatibility)
     */
    @Suppress("UNUSED_PARAMETER")
    fun update(deltaTime: Float = 1f/60f) {
        // Simplified physics for smoother feel

        // Apply gravity
        velocityY += gravity

        // Smooth velocity damping instead of hard ceiling
        if (velocityY > maxVelocity) {
            velocityY = velocityY * 0.95f + maxVelocity * 0.05f // Gradual approach to max
        }

        // Update position
        y += velocityY

        // Update animation through animator
        birdAnimator.update(velocityY)

        // Update trail effect
        updateTrail()
    }

    /**
     * Legacy update method for compatibility
     */
    fun update() = update(1f/60f)

    /**
     * Make bird jump - simple and reliable
     */
    fun jump() {
        // Simple direct jump with reliable upward velocity
        velocityY = jumpStrength
        birdAnimator.addTrailEffect(x, y)
    }

    /**
     * Reset to starting state
     */
    fun reset(newX: Float, newY: Float) {
        x = newX
        y = newY
        velocityY = 0f
        birdAnimator.reset()
        trailPoints.clear()
    }

    /**
     * Draw bird with smooth wing animation and selected hat
     */
    fun draw(canvas: Canvas) {
        // Draw trail first
        drawTrail(canvas)

        // Apply natural rotation from animator using KTX extension
        canvas.withRotation(birdAnimator.getCurrentRotation(), x, y) {
            // Get current sprite from animator
            val birdSprites = BirdSprites(birdNeutral, birdTransition, birdWingsUp, birdWingsDown, null)
            val currentSprite = birdAnimator.getCurrentSprite(birdSprites)

            if (currentSprite != null) {
                // Draw large sprite with proper animation
                val spriteRect = RectF(
                    x - radius, y - radius,
                    x + radius, y + radius
                )
                drawBitmap(currentSprite, null, spriteRect, null)
            } else {
                // Large fallback circle (still looks good)
                drawCircle(x, y, radius, fallbackPaint)
                drawCircle(x, y, radius, outlinePaint)

                // Eye
                drawCircle(x + radius * 0.3f, y - radius * 0.2f, radius * 0.12f, outlinePaint)
            }
        }

        // Draw hat OUTSIDE the rotation block to avoid animation performance issues
        drawHat(canvas)
    }

    /**
     * Draw the selected hat on top of the bird (optimized for performance)
     */
    private fun drawHat(canvas: Canvas) {
        hatManager?.let { manager ->
            val selectedHat = manager.getSelectedHat()

            // Don't draw hat if "No Hat" is selected
            if (selectedHat == HatType.NONE) {
                cachedHatSprite = null
                cachedHatType = null
                return
            }

            // Update cached hat sprite only when hat type changes
            if (cachedHatType != selectedHat) {
                cachedHatSprite = manager.getCurrentHatSprite()
                cachedHatType = selectedHat
            }

            cachedHatSprite?.let { hatSprite ->
                // Smaller hat size so bird's face is visible
                val hatSize = radius * 0.4f // Reduced further for better performance
                val hatOffsetY = -radius * 0.5f // Brought down from -0.7f to -0.5f

                // Apply slight left tilt to the hat for more natural look
                canvas.save()
                canvas.rotate(-15f, x, y + hatOffsetY) // 15 degrees tilt to the left

                val hatRect = RectF(
                    x - hatSize, y + hatOffsetY - hatSize,
                    x + hatSize, y + hatOffsetY + hatSize
                )
                canvas.drawBitmap(hatSprite, null, hatRect, null)
                canvas.restore()
            }
        }
    }

    /**
     * Force refresh the hat cache (call when player changes hat)
     */
    fun refreshHatCache() {
        cachedHatSprite = null
        cachedHatType = null
    }

    /**
     * Get trail points from animator
     */
    private fun getTrailPoints(): List<TrailPoint> = birdAnimator.getTrailPoints()

    /**
     * Update trail particles
     */
    private fun updateTrail() {
        trailPoints.removeAll { it.life-- <= 0 }
    }

    /**
     * Draw golden trail
     */
    private fun drawTrail(canvas: Canvas) {
        for (point in getTrailPoints()) {
            val alpha = (point.life.toFloat() / GameConstants.BIRD_TRAIL_LIFETIME.toFloat() * 120f).toInt()
            val size = (point.life.toFloat() / GameConstants.BIRD_TRAIL_LIFETIME.toFloat() * 12f) + 4f

            trailPaint.color = Color.argb(alpha, 255, 215, 0)
            canvas.drawCircle(point.x, point.y, size, trailPaint)
        }
    }

    /**
     * Collision detection
     */
    fun getBounds(): FloatArray {
        return floatArrayOf(
            x - collisionRadius * 0.7f, y - collisionRadius * 0.7f,
            x + collisionRadius * 0.7f, y + collisionRadius * 0.7f
        )
    }

}

/**
 * Trail particle class
 */
data class TrailPoint(val x: Float, val y: Float, var life: Int)
