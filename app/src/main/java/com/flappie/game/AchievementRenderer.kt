package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * AchievementRenderer - Handles rendering of achievement UI elements
 */
class AchievementRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    private val backgroundPaint = Paint().apply {
        color = "#F5F5F5".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val cardPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(8f, 0f, 4f, "#40000000".toColorInt())
    }
    
    private val titlePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        textSize = 56f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val achievementTitlePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }
    
    private val descriptionPaint = Paint().apply {
        color = "#7F8C8D".toColorInt()
        textSize = 24f
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }
    
    private val progressPaint = Paint().apply {
        color = "#3498DB".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val progressBackgroundPaint = Paint().apply {
        color = "#ECF0F1".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val coinPaint = Paint().apply {
        color = "#F39C12".toColorInt()
        textSize = 28f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    
    private val unlockedPaint = Paint().apply {
        color = "#27AE60".toColorInt()
        textSize = 24f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }
    
    private val iconPaint = Paint().apply {
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val closePaint = Paint().apply {
        color = "#E74C3C".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val closeTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    /**
     * Draw achievements screen overlay
     */
    fun drawAchievementsScreen(
        canvas: Canvas,
        achievements: List<Achievement>,
        scrollOffset: Float = 0f
    ) {
        val centerX = screenWidth / 2f
        
        // Semi-transparent background
        canvas.drawColor("#CC000000".toColorInt())
        
        // Main panel
        val panelPadding = 40f
        val panelRect = RectF(
            panelPadding,
            100f,
            screenWidth - panelPadding,
            screenHeight - 100f
        )
        canvas.drawRoundRect(panelRect, 20f, 20f, backgroundPaint)
        
        // Title
        canvas.drawText("Achievements", centerX, 180f, titlePaint)
        
        // Progress summary
        val unlockedCount = achievements.count { it.isUnlocked }
        val totalCount = achievements.size
        val progressText = "$unlockedCount / $totalCount Unlocked"
        
        val summaryPaint = Paint().apply {
            color = "#7F8C8D".toColorInt()
            textSize = 28f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(progressText, centerX, 220f, summaryPaint)
        
        // Achievement list
        val listStartY = 260f
        val listEndY = screenHeight - 180f
        val itemHeight = 120f
        val itemPadding = 10f
        
        // Clip the list area
        canvas.save()
        val clipRect = RectF(panelPadding + 20f, listStartY, screenWidth - panelPadding - 20f, listEndY)
        canvas.clipRect(clipRect)
        
        var currentY = listStartY - scrollOffset
        
        achievements.forEach { achievement ->
            if (currentY + itemHeight > listStartY - itemHeight && currentY < listEndY + itemHeight) {
                drawAchievementItem(canvas, achievement, panelPadding + 30f, currentY, screenWidth - panelPadding * 2 - 60f, itemHeight)
            }
            currentY += itemHeight + itemPadding
        }
        
        canvas.restore()
        
        // Close button
        val closeButtonSize = 60f
        val closeButtonX = screenWidth - panelPadding - closeButtonSize
        val closeButtonY = 120f
        val closeButtonRect = RectF(
            closeButtonX,
            closeButtonY,
            closeButtonX + closeButtonSize,
            closeButtonY + closeButtonSize
        )
        canvas.drawRoundRect(closeButtonRect, 30f, 30f, closePaint)
        canvas.drawText("✕", closeButtonRect.centerX(), closeButtonRect.centerY() + 12f, closeTextPaint)
    }
    
    /**
     * Draw a single achievement item
     */
    private fun drawAchievementItem(
        canvas: Canvas,
        achievement: Achievement,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        // Card background
        val cardRect = RectF(x, y, x + width, y + height)
        val cardColor = if (achievement.isUnlocked) "#E8F5E8".toColorInt() else Color.WHITE
        val itemCardPaint = Paint(cardPaint).apply { color = cardColor }
        canvas.drawRoundRect(cardRect, 15f, 15f, itemCardPaint)
        
        // Icon
        val iconX = x + 60f
        val iconY = y + height / 2f + 16f
        canvas.drawText(achievement.icon, iconX, iconY, iconPaint)
        
        // Title and description
        val textStartX = x + 120f
        val titleY = y + 35f
        val descY = y + 65f
        
        canvas.drawText(achievement.title, textStartX, titleY, achievementTitlePaint)
        canvas.drawText(achievement.description, textStartX, descY, descriptionPaint)
        
        // Progress bar or status
        if (achievement.isUnlocked) {
            canvas.drawText("✓ UNLOCKED", x + width - 20f, titleY, unlockedPaint)
            canvas.drawText("+${achievement.rewardCoins} coins", x + width - 20f, descY, coinPaint)
        } else {
            // Progress bar
            val progressBarWidth = 120f
            val progressBarHeight = 8f
            val progressBarX = x + width - progressBarWidth - 20f
            val progressBarY = descY - 15f
            
            // Progress background
            val progressBgRect = RectF(
                progressBarX,
                progressBarY,
                progressBarX + progressBarWidth,
                progressBarY + progressBarHeight
            )
            canvas.drawRoundRect(progressBgRect, 4f, 4f, progressBackgroundPaint)
            
            // Progress fill
            val progressWidth = progressBarWidth * achievement.progressPercentage
            if (progressWidth > 0) {
                val progressRect = RectF(
                    progressBarX,
                    progressBarY,
                    progressBarX + progressWidth,
                    progressBarY + progressBarHeight
                )
                canvas.drawRoundRect(progressRect, 4f, 4f, progressPaint)
            }
            
            // Progress text
            val progressText = "${achievement.progress} / ${achievement.targetValue}"
            val progressTextPaint = Paint().apply {
                color = "#95A5A6".toColorInt()
                textSize = 20f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText(progressText, x + width - 20f, progressBarY - 8f, progressTextPaint)
        }
    }
    
    /**
     * Draw achievement notification popup
     */
    fun drawAchievementNotification(
        canvas: Canvas,
        achievement: Achievement,
        animationProgress: Float // 0.0 to 1.0
    ) {
        if (animationProgress <= 0f) return
        
        val centerX = screenWidth / 2f
        val notificationWidth = screenWidth * 0.8f
        val notificationHeight = 120f
        
        // Slide in from top
        val targetY = 100f
        val startY = -notificationHeight
        val currentY = startY + (targetY - startY) * animationProgress
        
        // Notification background with shadow
        val notificationRect = RectF(
            centerX - notificationWidth / 2f,
            currentY,
            centerX + notificationWidth / 2f,
            currentY + notificationHeight
        )
        
        val notificationPaint = Paint().apply {
            color = "#27AE60".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(12f, 0f, 6f, "#40000000".toColorInt())
        }
        canvas.drawRoundRect(notificationRect, 20f, 20f, notificationPaint)
        
        // Achievement icon
        val iconSize = 56f
        val iconX = notificationRect.left + 40f
        val iconY = notificationRect.centerY() + 20f
        
        val notificationIconPaint = Paint().apply {
            textSize = iconSize
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText(achievement.icon, iconX, iconY, notificationIconPaint)
        
        // "Achievement Unlocked!" text
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText("Achievement Unlocked!", iconX + 80f, currentY + 35f, headerPaint)
        
        // Achievement title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText(achievement.title, iconX + 80f, currentY + 65f, titlePaint)
        
        // Reward text
        val rewardPaint = Paint().apply {
            color = "#F1C40F".toColorInt()
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText("+${achievement.rewardCoins} coins", iconX + 80f, currentY + 90f, rewardPaint)
    }
    
    /**
     * Get the close button area for touch detection
     */
    fun getCloseButtonArea(): RectF {
        val panelPadding = 40f
        val closeButtonSize = 60f
        val closeButtonX = screenWidth - panelPadding - closeButtonSize
        val closeButtonY = 120f
        
        return RectF(
            closeButtonX,
            closeButtonY,
            closeButtonX + closeButtonSize,
            closeButtonY + closeButtonSize
        )
    }
}