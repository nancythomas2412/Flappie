package com.glacierbird.game

import android.graphics.Bitmap
import kotlin.math.sin

/**
 * BirdAnimator - Handles all bird animation logic
 * Manages wing flapping animation, trail effects, and visual states
 */
class BirdAnimator {
    
    // Animation frame tracking
    private var animationFrame = 0
    private var animationCounter = 0
    
    // Natural rotation based on movement
    private var rotationAngle = 0f
    
    // Trail effect particles
    private val trailPoints = mutableListOf<TrailPoint>()
    
    /**
     * Update wing animation - smooth 5-frame cycle
     */
    fun updateWingAnimation() {
        animationCounter++
        
        if (animationCounter >= GameConstants.BIRD_ANIMATION_SPEED) {
            animationFrame = (animationFrame + 1) % GameConstants.BIRD_ANIMATION_FRAMES
            animationCounter = 0
        }
    }
    
    /**
     * Update rotation angle based on bird's velocity
     * @param velocityY Bird's vertical velocity
     */
    fun updateRotation(velocityY: Float) {
        rotationAngle = (velocityY * 1.5f).coerceIn(-20f, 20f)
    }
    
    /**
     * Update trail effect particles
     */
    fun updateTrail() {
        trailPoints.removeAll { it.life-- <= 0 }
    }
    
    /**
     * Add golden trail particles on jump
     * @param birdX Bird's current X position
     * @param birdY Bird's current Y position
     */
    fun addTrailEffect(birdX: Float, birdY: Float) {
        repeat(GameConstants.BIRD_TRAIL_PARTICLES) {
            trailPoints.add(TrailPoint(
                x = birdX + (Math.random().toFloat() - 0.5f) * 30f,
                y = birdY + (Math.random().toFloat() - 0.5f) * 30f,
                life = GameConstants.BIRD_TRAIL_LIFETIME
            ))
        }
    }
    
    /**
     * Get current wing animation sprite based on animation frame
     * @param sprites Bird sprites loaded by ResourceManager
     * @return Current sprite to display, or null for fallback
     */
    fun getCurrentSprite(sprites: BirdSprites): Bitmap? {
        return when (animationFrame) {
            0 -> sprites.neutral      // Wings middle
            1 -> sprites.transition   // Transition after neutral
            2 -> sprites.wingsUp      // Wings up
            3 -> sprites.wingsDown    // Wings down
            4 -> sprites.transition   // Transition after down
            else -> sprites.neutral
        }
    }
    
    /**
     * Get current rotation angle for drawing
     */
    fun getCurrentRotation(): Float = rotationAngle
    
    /**
     * Get trail points for rendering
     */
    fun getTrailPoints(): List<TrailPoint> = trailPoints
    
    /**
     * Reset animation state
     */
    fun reset() {
        animationFrame = 0
        animationCounter = 0
        rotationAngle = 0f
        trailPoints.clear()
    }
    
    /**
     * Update all animation components
     * Should be called every frame during bird update
     * @param velocityY Bird's vertical velocity
     */
    fun update(velocityY: Float) {
        updateWingAnimation()
        updateRotation(velocityY)
        updateTrail()
    }
}

/**
 * FloatingAnimator - Handles floating animation for collectibles
 * Provides smooth floating motion for power-ups and coins
 */
class FloatingAnimator {
    
    private var animationTimer = 0f
    private var floatTimer = 0f
    
    /**
     * Update floating animation
     * @param attractionActive Whether magnetic attraction is active
     */
    fun update(attractionActive: Boolean = false) {
        animationTimer += GameConstants.GLOW_ANIMATION_SPEED
        
        if (!attractionActive) {
            floatTimer += GameConstants.FLOAT_ANIMATION_SPEED
        }
    }
    
    /**
     * Get current float offset for Y position
     */
    fun getFloatOffset(): Float {
        return sin(animationTimer) * 5f
    }
    
    /**
     * Get natural floating movement offset
     */
    fun getNaturalFloatOffset(): Float {
        return sin(floatTimer) * 1f
    }
    
    /**
     * Reset animation state
     */
    fun reset() {
        animationTimer = 0f
        floatTimer = 0f
    }
}

/**
 * CoinAnimator - Specialized animator for coin spinning effects
 */
class CoinAnimator {
    
    private var animationTimer = 0f
    
    /**
     * Update coin animation
     */
    fun update() {
        animationTimer += GameConstants.SPIN_ANIMATION_SPEED
    }
    
    /**
     * Get current spin scale for width (creates spinning effect)
     */
    fun getSpinScale(): Float {
        return kotlin.math.abs(sin(animationTimer * GameConstants.COIN_SPIN_SPEED)) * 0.3f + 0.7f
    }
    
    /**
     * Get current glow offset for Y position
     */
    fun getGlowOffset(): Float {
        return sin(animationTimer) * 3f
    }
    
    /**
     * Reset animation state
     */
    fun reset() {
        animationTimer = 0f
    }
}

/**
 * PowerUpAnimator - Animation effects for power-ups
 */
class PowerUpAnimator {
    
    private var animationTimer = 0f
    private var glowTimer = 0f
    
    /**
     * Update power-up animation
     */
    fun update() {
        animationTimer += GameConstants.GLOW_ANIMATION_SPEED
        glowTimer += 0.05f
    }
    
    /**
     * Get floating offset
     */
    fun getFloatOffset(): Float {
        return sin(animationTimer) * 5f
    }
    
    /**
     * Get pulsing glow effect (for special power-ups)
     */
    fun getGlowIntensity(): Float {
        return (sin(glowTimer * 2f) * 0.3f + 0.7f).coerceIn(0.4f, 1.0f)
    }
    
    /**
     * Reset animation state
     */
    fun reset() {
        animationTimer = 0f
        glowTimer = 0f
    }
}