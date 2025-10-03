package com.glacierbird.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import androidx.core.graphics.toColorInt
import kotlin.math.*

/**
 * AnimationManager - Professional animation system for enhanced UI polish
 * Provides smooth animations, transitions, and visual effects
 */
class AnimationManager {
    
    data class Animation(
        val startTime: Long,
        val duration: Long,
        val startValue: Float,
        val endValue: Float,
        val easingFunction: (Float) -> Float = { it }, // Linear by default
        var isActive: Boolean = true
    )
    
    data class ParticleEffect(
        val x: Float,
        val y: Float,
        val velocityX: Float,
        val velocityY: Float,
        val color: Int,
        val size: Float,
        val lifetime: Long,
        val startTime: Long,
        var alpha: Float = 255f
    )
    
    data class FloatingText(
        val text: String,
        val startX: Float,
        val startY: Float,
        val velocityY: Float,
        val color: Int,
        val textSize: Float,
        val lifetime: Long,
        val startTime: Long,
        var currentX: Float = startX,
        var currentY: Float = startY,
        var alpha: Float = 255f
    )
    
    companion object {
        // Easing functions for professional animations
        fun easeInOut(t: Float): Float = t * t * (3f - 2f * t)
        fun easeOut(t: Float): Float = 1f - (1f - t).pow(2f)
        fun easeIn(t: Float): Float = t.pow(2f)
        fun bounce(t: Float): Float {
            return when {
                t < 1f/2.75f -> 7.5625f * t * t
                t < 2f/2.75f -> 7.5625f * (t - 1.5f/2.75f).pow(2f) + 0.75f
                t < 2.5f/2.75f -> 7.5625f * (t - 2.25f/2.75f).pow(2f) + 0.9375f
                else -> 7.5625f * (t - 2.625f/2.75f).pow(2f) + 0.984375f
            }
        }
        fun elastic(t: Float): Float {
            return if (t == 0f || t == 1f) t
            else -(2f.pow(10f * (t - 1f))) * sin((t - 1f - 0.1f/4f) * (2f * PI.toFloat()) / 0.1f)
        }
    }
    
    private val activeAnimations = mutableMapOf<String, Animation>()
    private val particles = mutableListOf<ParticleEffect>()
    private val floatingTexts = mutableListOf<FloatingText>()
    private val currentTime: Long get() = System.currentTimeMillis()
    
    // Screen shake effect
    private var shakeIntensity = 0f
    private var shakeDuration = 0L
    private var shakeStartTime = 0L
    
    // Button press effects
    private val buttonScales = mutableMapOf<String, Float>()
    
    /**
     * Start a named animation
     */
    fun startAnimation(
        name: String,
        startValue: Float,
        endValue: Float,
        duration: Long,
        easingFunction: (Float) -> Float = ::easeInOut
    ) {
        activeAnimations[name] = Animation(
            startTime = currentTime,
            duration = duration,
            startValue = startValue,
            endValue = endValue,
            easingFunction = easingFunction
        )
    }
    
    /**
     * Get current animated value
     */
    fun getAnimatedValue(name: String, defaultValue: Float = 0f): Float {
        val animation = activeAnimations[name] ?: return defaultValue
        
        if (!animation.isActive) return animation.endValue
        
        val elapsed = currentTime - animation.startTime
        if (elapsed >= animation.duration) {
            animation.isActive = false
            activeAnimations.remove(name)
            return animation.endValue
        }
        
        val progress = elapsed.toFloat() / animation.duration.toFloat()
        val easedProgress = animation.easingFunction(progress)
        
        return animation.startValue + (animation.endValue - animation.startValue) * easedProgress
    }
    
    /**
     * Check if animation is active
     */
    fun isAnimating(name: String): Boolean {
        return activeAnimations[name]?.isActive == true
    }
    
    /**
     * Screen shake effect
     */
    fun addScreenShake(intensity: Float, duration: Long) {
        shakeIntensity = intensity
        shakeDuration = duration
        shakeStartTime = currentTime
    }
    
    /**
     * Get screen shake offset
     */
    fun getScreenShakeOffset(): PointF {
        if (shakeDuration <= 0) return PointF(0f, 0f)
        
        val elapsed = currentTime - shakeStartTime
        if (elapsed >= shakeDuration) {
            shakeDuration = 0
            return PointF(0f, 0f)
        }
        
        val progress = elapsed.toFloat() / shakeDuration.toFloat()
        val currentIntensity = shakeIntensity * (1f - progress) // Fade out
        
        return PointF(
            (Math.random().toFloat() - 0.5f) * 2f * currentIntensity,
            (Math.random().toFloat() - 0.5f) * 2f * currentIntensity
        )
    }
    
    /**
     * Button press animation
     */
    fun onButtonPress(buttonName: String) {
        buttonScales[buttonName] = 0.9f
        startAnimation(
            "${buttonName}_scale",
            0.9f,
            1.0f,
            150L,
            ::easeOut
        )
    }
    
    /**
     * Get button scale for press effect
     */
    fun getButtonScale(buttonName: String): Float {
        return getAnimatedValue("${buttonName}_scale", 1.0f)
    }
    
    /**
     * Create particle explosion effect
     */
    fun createParticleExplosion(x: Float, y: Float, color: Int, particleCount: Int = 15) {
        repeat(particleCount) {
            val angle = (it.toFloat() / particleCount) * 2f * PI.toFloat()
            val speed = 3f + Math.random().toFloat() * 5f
            val size = 8f + Math.random().toFloat() * 12f
            
            particles.add(
                ParticleEffect(
                    x = x,
                    y = y,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed - 2f, // Slight upward bias
                    color = color,
                    size = size,
                    lifetime = 1500L + (Math.random() * 1000).toLong(),
                    startTime = currentTime
                )
            )
        }
    }
    
    /**
     * Add floating text effect
     */
    fun addFloatingText(
        text: String,
        x: Float,
        y: Float,
        color: Int = "#FFD700".toColorInt(),
        textSize: Float = 48f,
        lifetime: Long = 2000L
    ) {
        floatingTexts.add(
            FloatingText(
                text = text,
                startX = x,
                startY = y,
                velocityY = -2f,
                color = color,
                textSize = textSize,
                lifetime = lifetime,
                startTime = currentTime
            )
        )
    }
    
    /**
     * Update and draw all particle effects
     */
    fun updateAndDrawParticles(canvas: Canvas) {
        val particlePaint = Paint().apply {
            isAntiAlias = true
        }
        
        particles.removeAll { particle ->
            val elapsed = currentTime - particle.startTime
            if (elapsed >= particle.lifetime) {
                true // Remove expired particles
            } else {
                // Update particle position
                val progress = elapsed.toFloat() / particle.lifetime.toFloat()
                particle.alpha = 255f * (1f - progress) // Fade out
                
                // Frame rate independent particle movement
                val timeScale = elapsed / 1000f // Convert milliseconds to seconds
                val currentX = particle.x + particle.velocityX * timeScale
                val currentY = particle.y + particle.velocityY * timeScale + 0.1f * timeScale * timeScale // Gravity
                
                // Draw particle
                particlePaint.color = particle.color
                particlePaint.alpha = particle.alpha.toInt().coerceIn(0, 255)
                canvas.drawCircle(currentX, currentY, particle.size * (1f - progress * 0.5f), particlePaint)
                
                false // Keep particle
            }
        }
    }
    
    /**
     * Update and draw floating text effects
     */
    fun updateAndDrawFloatingText(canvas: Canvas) {
        val textPaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        
        floatingTexts.removeAll { floatingText ->
            val elapsed = currentTime - floatingText.startTime
            if (elapsed >= floatingText.lifetime) {
                true // Remove expired text
            } else {
                // Update text position
                val progress = elapsed.toFloat() / floatingText.lifetime.toFloat()
                floatingText.alpha = 255f * (1f - progress) // Fade out
                // Frame rate independent floating text movement
                val timeScale = elapsed / 1000f // Convert milliseconds to seconds
                floatingText.currentY = floatingText.startY + floatingText.velocityY * timeScale
                
                // Apply easing for smooth movement
                val easedProgress = easeOut(progress)
                val scale = 1f + easedProgress * 0.2f // Slight scale up
                
                // Draw floating text
                textPaint.color = floatingText.color
                textPaint.alpha = floatingText.alpha.toInt().coerceIn(0, 255)
                textPaint.textSize = floatingText.textSize * scale
                
                canvas.drawText(
                    floatingText.text,
                    floatingText.currentX,
                    floatingText.currentY,
                    textPaint
                )
                
                false // Keep text
            }
        }
    }
    
    /**
     * Create achievement celebration effect
     */
    fun celebrateAchievement(x: Float, y: Float) {
        // Golden particle explosion
        createParticleExplosion(x, y, "#FFD700".toColorInt(), 20)
        
        // Screen shake for impact
        addScreenShake(8f, 300L)
        
        // Floating text
        addFloatingText("ACHIEVEMENT!", x, y - 50f, "#FFD700".toColorInt(), 54f, 2500L)
    }
    
    /**
     * Create score popup effect
     */
    fun createScorePopup(x: Float, y: Float, score: Int, isBonus: Boolean = false) {
        val color = if (isBonus) "#FF6B35".toColorInt() else "#32CD32".toColorInt()
        val text = if (isBonus) "+$score BONUS!" else "+$score"
        
        addFloatingText(text, x, y, color, 42f, 1500L)
        
        if (isBonus) {
            createParticleExplosion(x, y, color, 8)
        }
    }
    
    /**
     * Fade transition effect
     */
    fun startFadeTransition(fadeOut: Boolean, duration: Long = 300L) {
        val startValue = if (fadeOut) 0f else 255f
        val endValue = if (fadeOut) 255f else 0f
        
        startAnimation("fade_transition", startValue, endValue, duration, ::easeInOut)
    }
    
    /**
     * Get fade overlay alpha
     */
    fun getFadeAlpha(): Int {
        return getAnimatedValue("fade_transition", 0f).toInt().coerceIn(0, 255)
    }
    
    /**
     * Draw fade overlay
     */
    fun drawFadeOverlay(canvas: Canvas, screenWidth: Int, screenHeight: Int) {
        val alpha = getFadeAlpha()
        if (alpha > 0) {
            val overlayPaint = Paint().apply {
                color = "#000000".toColorInt()
                this.alpha = alpha
            }
            canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), overlayPaint)
        }
    }
    
    /**
     * Clean up all animations and effects
     */
    fun cleanup() {
        activeAnimations.clear()
        particles.clear()
        floatingTexts.clear()
        buttonScales.clear()
        shakeDuration = 0L
    }
    
    /**
     * Get animation system status for debugging
     */
    fun getAnimationStatus(): Map<String, Any> {
        return mapOf(
            "activeAnimations" to activeAnimations.size,
            "particles" to particles.size,
            "floatingTexts" to floatingTexts.size,
            "isShaking" to (shakeDuration > 0)
        )
    }
}