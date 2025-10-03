package com.glacierbird.game

import android.graphics.*
import androidx.core.graphics.toColorInt
import kotlin.math.*

/**
 * LogoRenderer - Professional logo and branding system
 * Creates dynamic, animated logos with professional typography
 */
class LogoRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    companion object {
        private const val LOGO_ANIMATION_SPEED = 0.02f
        private const val GLOW_INTENSITY = 15f
        private const val SHADOW_OFFSET = 8f
    }
    
    private var animationTime = 0f
    
    // Professional logo paints
    private val titlePaint = Paint().apply {
        color = "#2C3E50".toColorInt() // Dark blue-gray
        textSize = 120f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        setShadowLayer(SHADOW_OFFSET, 4f, 4f, "#40000000".toColorInt())
    }
    
    private val subtitlePaint = Paint().apply {
        color = "#34495E".toColorInt() // Lighter gray
        textSize = 36f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        letterSpacing = 0.1f
    }
    
    private val glowPaint = Paint().apply {
        color = "#FFD700".toColorInt() // Gold glow
        textSize = 120f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    private val accentPaint = Paint().apply {
        color = "#3498DB".toColorInt() // Bright blue
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    /**
     * Draw animated professional logo
     */
    fun drawLogo(canvas: Canvas, centerX: Float, centerY: Float, @Suppress("UNUSED_PARAMETER") animationManager: AnimationManager? = null) {
        animationTime += LOGO_ANIMATION_SPEED
        
        // Subtle floating animation
        val floatOffset = sin(animationTime) * 8f
        val glowAlpha = (128 + sin(animationTime * 2f) * 64f).toInt().coerceIn(64, 192)
        
        val logoY = centerY + floatOffset
        
        // Draw background decorative elements
        drawLogoBackground(canvas, centerX, logoY)
        
        // Draw main title with glow effect
        glowPaint.alpha = glowAlpha
        canvas.drawText("GLACIER BIRD", centerX, logoY, glowPaint)
        
        // Draw main title
        canvas.drawText("GLACIER BIRD", centerX, logoY, titlePaint)
        
        // Draw subtitle
        canvas.drawText("The Ultimate Flying Adventure", centerX, logoY + 60f, subtitlePaint)
        
        // Draw decorative elements
        drawDecorations(canvas, centerX, logoY)
    }
    
    /**
     * Draw logo background elements
     */
    private fun drawLogoBackground(canvas: Canvas, centerX: Float, centerY: Float) {
        // Animated background circles
        val circleAlpha = (32 + sin(animationTime * 1.5f) * 16f).toInt().coerceIn(16, 48)
        val backgroundPaint = Paint().apply {
            color = "#3498DB".toColorInt()
            alpha = circleAlpha
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        
        // Draw concentric circles
        for (i in 1..3) {
            val radius = 180f + i * 40f + sin(animationTime + i) * 10f
            canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        }
    }
    
    /**
     * Draw decorative elements around logo
     */
    private fun drawDecorations(canvas: Canvas, centerX: Float, centerY: Float) {
        // Draw side wings/brackets
        val wingWidth = 80f
        val wingHeight = 20f
        val wingY = centerY
        
        // Left wing
        val leftWingPath = Path().apply {
            moveTo(centerX - 200f, wingY - wingHeight/2)
            lineTo(centerX - 200f + wingWidth, wingY)
            lineTo(centerX - 200f, wingY + wingHeight/2)
        }
        canvas.drawPath(leftWingPath, accentPaint)
        
        // Right wing
        val rightWingPath = Path().apply {
            moveTo(centerX + 200f, wingY - wingHeight/2)
            lineTo(centerX + 200f - wingWidth, wingY)
            lineTo(centerX + 200f, wingY + wingHeight/2)
        }
        canvas.drawPath(rightWingPath, accentPaint)
        
        // Draw small decorative dots
        val dotRadius = 4f
        val dotPaint = Paint().apply {
            color = "#FFD700".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        for (i in 0..4) {
            val angle = (i * 72f + animationTime * 30f) * PI / 180f
            val dotX = centerX + cos(angle).toFloat() * 160f
            val dotY = centerY + sin(angle).toFloat() * 160f
            canvas.drawCircle(dotX, dotY, dotRadius, dotPaint)
        }
    }
    
    /**
     * Draw compact version for UI elements
     */
    fun drawCompactLogo(canvas: Canvas, x: Float, y: Float, scale: Float = 1f) {
        val compactTitlePaint = Paint().apply {
            color = "#2C3E50".toColorInt()
            textSize = 48f * scale
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        val compactGlowPaint = Paint().apply {
            color = "#FFD700".toColorInt()
            textSize = 48f * scale
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f * scale
            alpha = 128
        }
        
        // Draw compact logo with glow
        canvas.drawText("GLACIER BIRD", x, y, compactGlowPaint)
        canvas.drawText("GLACIER BIRD", x, y, compactTitlePaint)
    }
    
    /**
     * Draw loading screen logo with progress
     */
    fun drawLoadingLogo(canvas: Canvas, progress: Float) {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Draw main logo
        drawLogo(canvas, centerX, centerY)
        
        // Draw loading progress bar
        val barWidth = 300f
        val barHeight = 8f
        val barX = centerX - barWidth / 2
        val barY = centerY + 120f
        
        // Background bar
        val barBackgroundPaint = Paint().apply {
            color = "#BDC3C7".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(barX, barY, barX + barWidth, barY + barHeight, barHeight/2, barHeight/2, barBackgroundPaint)
        
        // Progress bar
        val progressPaint = Paint().apply {
            color = "#3498DB".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val progressWidth = barWidth * progress.coerceIn(0f, 1f)
        canvas.drawRoundRect(barX, barY, barX + progressWidth, barY + barHeight, barHeight/2, barHeight/2, progressPaint)
        
        // Loading text
        val loadingPaint = Paint().apply {
            color = "#7F8C8D".toColorInt()
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Loading... ${(progress * 100).toInt()}%", centerX, barY + 40f, loadingPaint)
    }
    
    /**
     * Draw achievement badge with logo accent
     */
    fun drawAchievementBadge(canvas: Canvas, x: Float, y: Float, text: String) {
        val badgeRadius = 60f
        
        // Badge background with gradient
        val gradientPaint = Paint().apply {
            shader = RadialGradient(
                x, y, badgeRadius,
                intArrayOf("#FFD700".toColorInt(), "#FFA500".toColorInt()),
                null, Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(x, y, badgeRadius, gradientPaint)
        
        // Badge border
        val borderPaint = Paint().apply {
            color = "#FF8C00".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        canvas.drawCircle(x, y, badgeRadius, borderPaint)
        
        // Badge text
        val badgeTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(text, x, y + 8f, badgeTextPaint)
        
        // Small logo accent
        drawCompactLogo(canvas, x, y - badgeRadius - 20f, 0.3f)
    }
    
    /**
     * Get current animation phase for external sync
     */
    fun getAnimationPhase(): Float = (sin(animationTime) + 1f) / 2f
    
    /**
     * Reset animation timer
     */
    fun resetAnimation() {
        animationTime = 0f
    }
}