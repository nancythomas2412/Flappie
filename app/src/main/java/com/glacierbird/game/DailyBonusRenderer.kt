package com.glacierbird.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * DailyBonusRenderer - Beautiful UI for daily login rewards
 * Optimized for user engagement and retention
 */
class DailyBonusRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    // Background and panels
    private val overlayPaint = Paint().apply {
        color = "#E8000000".toColorInt() // Darker overlay for focus
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val panelPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(20f, 0f, 10f, "#60000000".toColorInt())
    }
    
    // Title and text
    private val titlePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        textSize = 52f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val bonusTitlePaint = Paint().apply {
        color = "#E74C3C".toColorInt()
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val descriptionPaint = Paint().apply {
        color = "#34495E".toColorInt()
        textSize = 28f
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    // Reward displays
    private val coinPaint = Paint().apply {
        color = "#F39C12".toColorInt()
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val lifePaint = Paint().apply {
        color = "#E74C3C".toColorInt()
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    // Buttons
    private val claimButtonPaint = Paint().apply {
        color = "#27AE60".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(8f, 0f, 4f, "#40000000".toColorInt())
    }
    
    private val claimButtonTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val closeButtonPaint = Paint().apply {
        color = "#95A5A6".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val closeButtonTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    // Streak display
    private val streakBackgroundPaint = Paint().apply {
        color = "#3498DB".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val streakTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    /**
     * Draw daily bonus claim dialog
     */
    fun drawDailyBonusDialog(
        canvas: Canvas,
        bonus: DailyBonus,
        streak: Int,
        animationProgress: Float = 1.0f
    ) {
        // Overlay
        canvas.drawColor(overlayPaint.color)
        
        // Main panel with animation
        val panelWidth = screenWidth * 0.88f
        val panelHeight = screenHeight * 0.65f
        val panelX = (screenWidth - panelWidth) / 2f
        val basePanelY = (screenHeight - panelHeight) / 2f
        
        // Animate panel slide up
        val panelY = basePanelY + (screenHeight * 0.3f * (1f - animationProgress))
        
        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        canvas.drawRoundRect(panelRect, 24f, 24f, panelPaint)
        
        // Streak indicator
        drawStreakIndicator(canvas, streak, panelX + panelWidth - 100f, panelY + 20f)
        
        // Title
        canvas.drawText("Daily Bonus! 🎁", screenWidth / 2f, panelY + 80f, titlePaint)
        
        // Bonus specific title
        canvas.drawText(bonus.title, screenWidth / 2f, panelY + 140f, bonusTitlePaint)
        
        // Description
        canvas.drawText(bonus.description, screenWidth / 2f, panelY + 180f, descriptionPaint)
        
        // Rewards display
        drawRewardsDisplay(canvas, bonus, screenWidth / 2f, panelY + 250f, panelWidth)
        
        // Claim button
        val claimButtonY = panelY + panelHeight - 120f
        drawClaimButton(canvas, screenWidth / 2f, claimButtonY, animationProgress)
        
        // Close button
        drawCloseButton(canvas, panelX + panelWidth - 50f, panelY + 20f)
    }
    
    /**
     * Draw weekly bonus calendar view
     */
    fun drawWeeklyCalendar(
        canvas: Canvas,
        weeklyBonuses: List<DailyBonus>,
        currentDay: Int,
        claimedDays: Set<Int>
    ) {
        // Overlay
        canvas.drawColor(overlayPaint.color)
        
        val panelWidth = screenWidth * 0.92f
        val panelHeight = screenHeight * 0.75f
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        
        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        canvas.drawRoundRect(panelRect, 20f, 20f, panelPaint)
        
        // Title
        canvas.drawText("Weekly Bonus Calendar 📅", screenWidth / 2f, panelY + 60f, titlePaint)
        
        // Calendar grid
        val gridStartY = panelY + 120f
        val dayWidth = (panelWidth - 80f) / 7f
        val dayHeight = 120f
        
        weeklyBonuses.forEachIndexed { index, bonus ->
            val dayX = panelX + 40f + (index * dayWidth)
            val dayY = gridStartY
            
            drawCalendarDay(
                canvas,
                bonus,
                index + 1,
                dayX,
                dayY,
                dayWidth - 10f,
                dayHeight,
                index == currentDay,
                claimedDays.contains(index + 1)
            )
        }
        
        // Instructions
        val instructionPaint = Paint().apply {
            color = "#7F8C8D".toColorInt()
            textSize = 24f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Login daily to claim amazing rewards!", screenWidth / 2f, gridStartY + dayHeight + 40f, instructionPaint)
        
        // Close button
        drawCloseButton(canvas, panelX + panelWidth - 50f, panelY + 20f)
    }
    
    /**
     * Draw streak indicator badge
     */
    private fun drawStreakIndicator(canvas: Canvas, streak: Int, x: Float, y: Float) {
        val badgeWidth = 80f
        val badgeHeight = 40f
        
        val badgeRect = RectF(x, y, x + badgeWidth, y + badgeHeight)
        canvas.drawRoundRect(badgeRect, 20f, 20f, streakBackgroundPaint)
        
        canvas.drawText("🔥 $streak", x + badgeWidth/2f, y + badgeHeight/2f + 8f, streakTextPaint)
    }
    
    /**
     * Draw rewards display section
     */
    private fun drawRewardsDisplay(canvas: Canvas, bonus: DailyBonus, centerX: Float, centerY: Float, panelWidth: Float) {
        val rewardBoxWidth = (panelWidth - 120f) / 3f
        val rewardBoxHeight = 80f
        val startX = centerX - (rewardBoxWidth * 1.5f) - 20f
        
        // Coins reward
        if (bonus.coins > 0) {
            drawRewardBox(canvas, startX, centerY, rewardBoxWidth, rewardBoxHeight, "🪙", "${bonus.coins}", "#F39C12")
        }
        
        // Lives reward  
        if (bonus.extraLives > 0) {
            drawRewardBox(canvas, startX + rewardBoxWidth + 20f, centerY, rewardBoxWidth, rewardBoxHeight, "❤️", "${bonus.extraLives}", "#E74C3C")
        }
        
        // Power-ups reward
        if (bonus.powerUps.isNotEmpty()) {
            val powerUpText = "${bonus.powerUps.size}"
            drawRewardBox(canvas, startX + (rewardBoxWidth + 20f) * 2f, centerY, rewardBoxWidth, rewardBoxHeight, "⚡", powerUpText, "#9B59B6")
        }
    }
    
    /**
     * Draw individual reward box
     */
    private fun drawRewardBox(canvas: Canvas, x: Float, y: Float, width: Float, height: Float, icon: String, value: String, colorHex: String) {
        // Background
        val boxPaint = Paint().apply {
            color = "${colorHex}33".toColorInt() // 20% opacity background (33 in hex = ~20% alpha)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val boxRect = RectF(x, y, x + width, y + height)
        canvas.drawRoundRect(boxRect, 12f, 12f, boxPaint)
        
        // Border
        val borderPaint = Paint().apply {
            color = colorHex.toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(boxRect, 12f, 12f, borderPaint)
        
        // Icon
        val iconPaint = Paint().apply {
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(icon, x + width/2f, y + 35f, iconPaint)
        
        // Value
        val valuePaint = Paint().apply {
            color = colorHex.toColorInt()
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(value, x + width/2f, y + 65f, valuePaint)
    }
    
    /**
     * Draw calendar day cell
     */
    private fun drawCalendarDay(
        canvas: Canvas,
        bonus: DailyBonus,
        dayNumber: Int,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        isToday: Boolean,
        isClaimed: Boolean
    ) {
        // Background
        val bgColor = when {
            isClaimed -> "#27AE60" // Green for claimed
            isToday -> "#3498DB"   // Blue for today
            else -> "#ECF0F1"      // Light gray for future
        }
        
        val dayPaint = Paint().apply {
            color = bgColor.toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        val dayRect = RectF(x, y, x + width, y + height)
        canvas.drawRoundRect(dayRect, 12f, 12f, dayPaint)
        
        // Day number
        val dayTextPaint = Paint().apply {
            color = if (bgColor == "#ECF0F1") "#2C3E50".toColorInt() else Color.WHITE
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Day $dayNumber", x + width/2f, y + 25f, dayTextPaint)
        
        // Reward preview
        val rewardTextPaint = Paint().apply {
            color = if (bgColor == "#ECF0F1") "#34495E".toColorInt() else Color.WHITE
            textSize = 16f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("${bonus.coins} coins", x + width/2f, y + 50f, rewardTextPaint)
        
        if (bonus.extraLives > 0) {
            canvas.drawText("+${bonus.extraLives} ❤️", x + width/2f, y + 70f, rewardTextPaint)
        }
        
        if (bonus.powerUps.isNotEmpty()) {
            canvas.drawText("⚡ Power-ups", x + width/2f, y + 90f, rewardTextPaint)
        }
        
        // Status indicator
        if (isClaimed) {
            canvas.drawText("✓", x + width - 15f, y + 20f, dayTextPaint)
        } else if (isToday) {
            canvas.drawText("!", x + width - 15f, y + 20f, dayTextPaint)
        }
    }
    
    /**
     * Draw claim button
     */
    private fun drawClaimButton(canvas: Canvas, centerX: Float, y: Float, animationProgress: Float) {
        val buttonWidth = 200f
        val buttonHeight = 60f
        
        // Animate button scale
        val scale = 0.9f + (0.1f * animationProgress)
        val scaledWidth = buttonWidth * scale
        val scaledHeight = buttonHeight * scale
        val scaledX = centerX - scaledWidth/2f
        
        val buttonRect = RectF(scaledX, y, scaledX + scaledWidth, y + scaledHeight)
        canvas.drawRoundRect(buttonRect, 30f, 30f, claimButtonPaint)
        
        canvas.drawText("CLAIM REWARD!", centerX, y + scaledHeight/2f + 10f, claimButtonTextPaint)
    }
    
    /**
     * Draw close button
     */
    private fun drawCloseButton(canvas: Canvas, x: Float, y: Float) {
        val buttonSize = 40f
        val buttonRect = RectF(x, y, x + buttonSize, y + buttonSize)
        canvas.drawRoundRect(buttonRect, 20f, 20f, closeButtonPaint)
        
        canvas.drawText("✕", x + buttonSize/2f, y + buttonSize/2f + 8f, closeButtonTextPaint)
    }
    
    /**
     * Get button areas for touch detection
     */
    fun getClaimButtonArea(): RectF {
        val centerX = screenWidth / 2f
        val buttonWidth = 200f
        val buttonHeight = 60f
        val panelHeight = screenHeight * 0.65f
        val panelY = (screenHeight - panelHeight) / 2f
        val buttonY = panelY + panelHeight - 120f
        
        return RectF(
            centerX - buttonWidth/2f,
            buttonY,
            centerX + buttonWidth/2f,
            buttonY + buttonHeight
        )
    }
    
    fun getCloseButtonArea(): RectF {
        val panelWidth = screenWidth * 0.88f
        val panelHeight = screenHeight * 0.65f
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        
        return RectF(
            panelX + panelWidth - 50f,
            panelY + 20f,
            panelX + panelWidth - 10f,
            panelY + 60f
        )
    }
}