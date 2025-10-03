package com.glacierbird.game

import android.graphics.*
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave

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
        setShadowLayer(DensityUtils.dp(8f), 0f, DensityUtils.dp(4f), "#40000000".toColorInt())
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
        val panelPadding = DensityUtils.dp(40f)
        val panelRect = RectF(
            panelPadding,
            DensityUtils.dp(100f),
            screenWidth - panelPadding,
            screenHeight - DensityUtils.dp(100f)
        )
        canvas.drawRoundRect(panelRect, DensityUtils.dp(20f), DensityUtils.dp(20f), backgroundPaint)

        // Title
        canvas.drawText("Achievements", centerX, DensityUtils.dp(180f), titlePaint)

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
        canvas.drawText(progressText, centerX, DensityUtils.dp(220f), summaryPaint)

        // Achievement list
        val listStartY = DensityUtils.dp(260f)
        val listEndY = screenHeight - DensityUtils.dp(180f)
        val itemHeight = DensityUtils.dp(120f)
        val itemPadding = DensityUtils.dp(10f)

        // Clip the list area
        canvas.withSave {
            val clipRect = RectF(
                panelPadding + DensityUtils.dp(20f),
                listStartY,
                screenWidth - panelPadding - DensityUtils.dp(20f),
                listEndY
            )
            clipRect(clipRect)

            var currentY = listStartY - scrollOffset

            achievements.forEach { achievement ->
                if (currentY + itemHeight > listStartY - itemHeight && currentY < listEndY + itemHeight) {
                    drawAchievementItem(
                        this,
                        achievement,
                        panelPadding + DensityUtils.dp(30f),
                        currentY,
                        screenWidth - panelPadding * 2 - DensityUtils.dp(60f),
                        itemHeight
                    )
                }
                currentY += itemHeight + itemPadding
            }

        }

        // Close button
        val closeButtonSize = DensityUtils.dp(60f)
        val closeButtonX = screenWidth - panelPadding - closeButtonSize
        val closeButtonY = DensityUtils.dp(120f)
        val closeButtonRect = RectF(
            closeButtonX,
            closeButtonY,
            closeButtonX + closeButtonSize,
            closeButtonY + closeButtonSize
        )
        canvas.drawRoundRect(closeButtonRect, closeButtonSize/2f, closeButtonSize/2f, closePaint)
        canvas.drawText("✕", closeButtonRect.centerX(), closeButtonRect.centerY() + DensityUtils.dp(12f), closeTextPaint)
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
        canvas.drawRoundRect(cardRect, DensityUtils.dp(15f), DensityUtils.dp(15f), itemCardPaint)

        // Icon
        val iconX = x + DensityUtils.dp(60f)
        val iconY = y + height / 2f + DensityUtils.dp(16f)
        canvas.drawText(achievement.icon, iconX, iconY, iconPaint)

        // Title and description
        val textStartX = x + DensityUtils.dp(120f)
        val titleY = y + DensityUtils.dp(35f)
        val descY = y + DensityUtils.dp(65f)

        canvas.drawText(achievement.title, textStartX, titleY, achievementTitlePaint)
        canvas.drawText(achievement.description, textStartX, descY, descriptionPaint)

        // Progress bar or status
        if (achievement.isUnlocked) {
            canvas.drawText("✓ UNLOCKED", x + width - DensityUtils.dp(20f), titleY, unlockedPaint)
            canvas.drawText("+${achievement.rewardCoins} coins", x + width - DensityUtils.dp(20f), descY, coinPaint)
        } else {
            // Progress bar
            val progressBarWidth = DensityUtils.dp(120f)
            val progressBarHeight = DensityUtils.dp(8f)
            val progressBarX = x + width - progressBarWidth - DensityUtils.dp(20f)
            val progressBarY = descY - DensityUtils.dp(15f)

            // Progress background
            val progressBgRect = RectF(
                progressBarX,
                progressBarY,
                progressBarX + progressBarWidth,
                progressBarY + progressBarHeight
            )
            canvas.drawRoundRect(progressBgRect, DensityUtils.dp(4f), DensityUtils.dp(4f), progressBackgroundPaint)

            // Progress fill
            val progressWidth = progressBarWidth * achievement.progressPercentage
            if (progressWidth > 0) {
                val progressRect = RectF(
                    progressBarX,
                    progressBarY,
                    progressBarX + progressWidth,
                    progressBarY + progressBarHeight
                )
                canvas.drawRoundRect(progressRect, DensityUtils.dp(4f), DensityUtils.dp(4f), progressPaint)
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
            canvas.drawText(progressText, x + width - DensityUtils.dp(20f), progressBarY - DensityUtils.dp(8f), progressTextPaint)
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
        val notificationWidth = DensityUtils.UI.getNotificationWidth()
        val notificationHeight = DensityUtils.dp(120f)

        // Slide in from top
        val targetY = DensityUtils.dp(100f)
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
            setShadowLayer(DensityUtils.dp(12f), 0f, DensityUtils.dp(6f), "#40000000".toColorInt())
        }
        canvas.drawRoundRect(notificationRect, DensityUtils.dp(20f), DensityUtils.dp(20f), notificationPaint)

        // Achievement icon
        val iconSize = DensityUtils.dp(56f)
        val iconX = notificationRect.left + DensityUtils.dp(40f)
        val iconY = notificationRect.centerY() + DensityUtils.dp(20f)

        val notificationIconPaint = Paint().apply {
            textSize = iconSize
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText(achievement.icon, iconX, iconY, notificationIconPaint)

        // "Achievement Unlocked!" text
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = DensityUtils.dp(14.3f)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText("Achievement Unlocked!", iconX + DensityUtils.dp(80f), currentY + DensityUtils.dp(35f), headerPaint)

        // Achievement title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = DensityUtils.dp(12.5f)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText(achievement.title, iconX + DensityUtils.dp(80f), currentY + DensityUtils.dp(65f), titlePaint)

        // Reward text
        val rewardPaint = Paint().apply {
            color = "#F1C40F".toColorInt()
            textSize = DensityUtils.dp(12.5f)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText("+${achievement.rewardCoins} coins", iconX + DensityUtils.dp(80f), currentY + DensityUtils.dp(80f), rewardPaint)
    }

    /**
     * Get the close button area for touch detection
     */
    fun getCloseButtonArea(): RectF {
        val panelPadding = DensityUtils.dp(40f)
        val closeButtonSize = DensityUtils.dp(60f)
        val closeButtonX = screenWidth - panelPadding - closeButtonSize
        val closeButtonY = DensityUtils.dp(120f)

        return RectF(
            closeButtonX,
            closeButtonY,
            closeButtonX + closeButtonSize,
            closeButtonY + closeButtonSize
        )
    }
}